package com.zhang.change.ui.performance_statistic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment.DIRECTORY_DCIM
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.adapter.BillDateAdapter
import com.zhang.change.adapter.UserListAdapter
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.databinding.ActivityPerformanceStatisticBinding
import com.zhang.change.dialog.AddUserDialog
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import com.zhang.change.ui.performance_add.AddPerformanceActivity
import com.zhang.change.utils.*
import jxl.Workbook
import jxl.write.Label
import jxl.write.Number
import jxl.write.WritableWorkbook
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*


class PerformanceStatisticActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val calendar = Calendar.getInstance()
    private val userList = arrayListOf<User>()
    private lateinit var mContext: Context
    private lateinit var userDao: UserDao
    private lateinit var userBillDao: UserBillDao
    private lateinit var performanceDao: PerformanceDao
    private val billList = arrayListOf<UserBill>()
    private val billDateAdapter = BillDateAdapter(billList)
    private val userListAdapter = UserListAdapter(userList) {
        curUser = it
        findUserBillAndRefreshView()
    }
    private var curUser: User? = null
    private lateinit var binding: ActivityPerformanceStatisticBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = baseContext
        binding = ActivityPerformanceStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDao()
        initView()
    }


    private fun initView() {
        setMothText()
        with(binding) {


            with(rv) {
                layoutManager = LinearLayoutManager(context)
                adapter = billDateAdapter
            }
            with(rvUser) {
                layoutManager = GridLayoutManager(context, 5)
                adapter = userListAdapter
            }
            findUserListAndRefreshView()

            vAddUser.setOnClickListener {
                val dialog = AddUserDialog()
                dialog.show(supportFragmentManager, userDao) { user ->
                    curUser = user
                    findUserListAndRefreshView()
                }
            }
            vPrev.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                setMothText()
                findUserBillAndRefreshView()
            }
            vNext.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                setMothText()
                findUserBillAndRefreshView()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMothText() {
        binding.vDate.text = "${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月"
    }

    override fun onResume() {
        super.onResume()
        findUserListAndRefreshView()
    }

    private fun initDao() {
        val db = (mContext.applicationContext as MyApplication).db
        userBillDao = db.userBillDao()
        performanceDao = db.performanceDao()
        userDao = db.userDao()
    }

    private fun findUserListAndRefreshView() {
        launch {
            val userList = withContext(Dispatchers.Default) { userDao.queryAllUser() }
            this@PerformanceStatisticActivity.userList.let {
                it.clear()
                it.addAll(userList)
            }
            userList.forEach { it.u_selected = false }
            if (curUser == null) curUser = userList.getOrNull(0)
            userList.find { it.no == curUser?.no }?.u_selected = true
            userListAdapter.notifyDataSetChanged()
            findUserBillAndRefreshView()
        }
    }


    private fun initEmptyBill(isMax: Boolean = false): ArrayList<UserBill> {
        val arrayList = arrayListOf<UserBill>()
        val minDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val minDateStamp = calendar.timeInMillis.getMinDateStampMonth()
        val endDay = if (isCurrMonth() && !isMax) {
            calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        val _calendar = Calendar.getInstance().apply {
            timeInMillis = minDateStamp
        }
        var dateStamp = _calendar.timeInMillis
        for (i in minDay..endDay) {
            arrayList.add(UserBill.of(dateStamp))
            _calendar.add(Calendar.DAY_OF_MONTH, 1)
            dateStamp = _calendar.timeInMillis
        }
        return arrayList
    }

    private fun isCurrMonth() =
        calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)


    private fun findUserBillAndRefreshView() {
        if (curUser == null) return
        launch {
            val dbBillList = withContext(Dispatchers.Default) {
                val dateStamp = calendar.timeInMillis
                userBillDao.queryBillByNoAndDate(
                    curUser!!.no,
                    dateStamp.getMinDateStampMonth(),
                    dateStamp.getMaxDateStampMonth()
                )
            }
            val emptyBill = initEmptyBill()

            copyDate2EmptyList(emptyBill, dbBillList)

            this@PerformanceStatisticActivity.billList.let {
                it.clear()
                it.addAll(emptyBill)
            }
            billDateAdapter.notifyDataSetChanged()
            binding.tvTotalIncome.text = billList.sumBy { it.income }.getNiceStr()
            binding.tvTotalSalary.text = billList.sumBy { it.salary }.getNiceStr()

        }
    }

    private fun copyDate2EmptyList(
        emptyBill: ArrayList<UserBill>,
        dbBillList: List<UserBill>
    ): ArrayList<UserBill> {
        val eCalendar = Calendar.getInstance()
        val dbCalendar = Calendar.getInstance()
        emptyBill.forEach { eBill ->
            eCalendar.timeInMillis = eBill.dateStamp
            dbBillList.forEach { dbBill ->
                dbCalendar.timeInMillis = dbBill.dateStamp
                if (eCalendar.get(Calendar.YEAR) == dbCalendar.get(Calendar.YEAR)
                    && eCalendar.get(Calendar.MONTH) == dbCalendar.get(Calendar.MONTH)
                    && eCalendar.get(Calendar.DAY_OF_MONTH) == dbCalendar.get(Calendar.DAY_OF_MONTH)
                ) {
                    eBill.copy(dbBill)
                }
            }
        }
        return emptyBill
    }


    private fun copy2EmptyListGroupByUserId(dbBillList: List<UserBill>): HashMap<Int, List<UserBill>> {
        val userBillMap = dbBillList.groupBy { it.uid }
        val uIdBillMap = hashMapOf<Int, List<UserBill>>()
        userBillMap.forEach {
            val list = it.value
            uIdBillMap[it.key] = copyDate2EmptyList(initEmptyBill(true), list)
        }
        return uIdBillMap
    }

    /**
     * Key: YYYY_MM_DD
     * Pair:income,salary(分)
     */
    private fun sumByDate(dbBillList: List<UserBill>): Map<String, Pair<Int, Int>> {
        val userBillMap = dbBillList.groupBy { it.dateStamp.date2String(DateFormat.YYYY_MM_DD) }
        val uIdBillMap = TreeMap<String, Pair<Int, Int>>()
        userBillMap.forEach {
            uIdBillMap[it.key] = Pair(it.value.sumBy { it.income }, it.value.sumBy { it.salary })
        }
        return uIdBillMap
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_performance_statistic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(baseContext, AddPerformanceActivity::class.java))
            }
            R.id.action_extract -> {
                toExcel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toExcel() {
        launch {
            val shopName = (application as MyApplication).shopName
            val monthDes = calendar.timeInMillis.date2String(DateFormat.YYYY_MM)
            val file = File(
                getExternalFilesDir(DIRECTORY_DCIM),
                "${shopName}${monthDes}营业额报表.xls"
            )


            withContext(Dispatchers.Default) {
                val dateStamp = calendar.timeInMillis
                val billList = userBillDao.queryBillByDate(
                    dateStamp.getMinDateStampMonth(),
                    dateStamp.getMaxDateStampMonth()
                )

                val uIdBillMap = copy2EmptyListGroupByUserId(billList)
                val performanceSumList = sumByDate(uIdBillMap.flatMap { it.value })
                val userList =
                    billList.map { User(it.no, it.name).apply { uid = it.uid } }.distinct()

                if ((!file.exists())) {
                    file.createNewFile()
                } else {
                    file.delete()
                    file.createNewFile()
                }
                var wb: WritableWorkbook? = null
                try {
                    wb = Workbook.createWorkbook(file) //创建xls表格文件
                    // 表头显示
                    val wcfTitle = ExcelUtils.getTitleCellFormat()
                    // 内容显示
                    val wcfContent = ExcelUtils.getContentCellFormat()
                    // 总计显示
                    val wcfTotal = ExcelUtils.getTotalCellFormat()


                    val ws = wb!!.createSheet("sheet1", 0)

                    ws.addCell(Label(0, 0, "${shopName} ${monthDes} 营业记录", wcfTitle))

                    ws.addCell(Label(0, 1, getString(R.string.no), wcfTitle))
                    var totalCol = 0
                    for (i in userList.indices) { // 工号
                        ws.addCell(Label(2 * i + 1, 1, "#${userList[i].no}", wcfTitle))
                        ws.addCell(Label(2 * i + 2, 1, " ", wcfTitle))
                        totalCol = 2 * i + 2
                        ws.mergeCells(2 * i + 1, 1, 2 * i + 2, 1)
                    }

                    ws.addCell(Label(totalCol + 1, 1, getString(R.string.total), wcfTitle))
                    ws.mergeCells(totalCol + 1, 1, totalCol + 2, 1)
                    ws.addCell(Label(totalCol + 1, 2, "总业绩", wcfTotal))
                    ws.addCell(Label(totalCol + 2, 2, "总工资", wcfTotal))

                    performanceSumList.map { it.value }.forEachIndexed { index, pair ->
                        val totalIncome = pair.first
                        val totalSalary = pair.second
                        if (totalIncome != 0) {
                            ws.addCell(
                                Number(
                                    totalCol + 1,
                                    3 + index,
                                    totalIncome.div(100.0),
                                    wcfTotal
                                )
                            )
                        }
                        if (totalSalary != 0) {
                            ws.addCell(
                                Number(
                                    totalCol + 2,
                                    3 + index,
                                    totalSalary.div(100.0),
                                    wcfTotal
                                )
                            )
                        }
                    }

                    ws.mergeCells(0, 0, totalCol + 2, 0)
                    ws.addCell(Label(0, 2, getString(R.string.date), wcfTitle))

                    val perStr = getString(R.string.performance)
                    val salaryStr = getString(R.string.salary)
                    for (i in userList.indices) {
                        ws.addCell(Label(2 * i + 1, 2, perStr, wcfTitle))
                        ws.addCell(Label(2 * i + 2, 2, salaryStr, wcfTitle))
                    }

                    for (i in userList.indices) { // 填充内容
                        val user = userList[i]
                        val currBillList = uIdBillMap[user.uid]
                        currBillList?.forEachIndexed { index, userBill ->
                            if (userBill.income != 0) {
                                ws.addCell(
                                    Number(
                                        2 * i + 1,
                                        3 + index,
                                        userBill.income.div(100.0),
                                        wcfContent
                                    )
                                )
                            }
                            if (userBill.salary != 0) {
                                ws.addCell(
                                    Number(
                                        2 * i + 2,
                                        3 + index,
                                        userBill.salary.div(100.0),
                                        wcfContent
                                    )
                                )
                            }
                        }
                    }

                    val dateList = initEmptyBill(true)

                    dateList.forEachIndexed { index, userBill ->
                        ws.addCell(
                            Label(
                                0,
                                3 + index,
                                userBill.dateStamp.date2String(DateFormat.D),
                                wcfContent
                            )
                        )
                    }
                    val totalRow = 3 + dateList.size

                    ws.addCell(Label(0, totalRow, getString(R.string.total), wcfTotal))

                    val incomeSum = performanceSumList.map { it.value }.sumBy { it.first }
                    val salarySum = performanceSumList.map { it.value }.sumBy { it.second }
                    ws.addCell(Number(totalCol + 1, totalRow, incomeSum.div(100.0), wcfTotal))
                    ws.addCell(Number(totalCol + 2, totalRow, salarySum.div(100.0), wcfTotal))

                    for (i in userList.indices) {
                        val user = userList[i]
                        val currBillList = uIdBillMap[user.uid].orEmpty()
                        ws.addCell(
                            Number(
                                2 * i + 1,
                                totalRow,
                                currBillList.sumBy { it.income }.div(100.0),
                                wcfTotal
                            )
                        )
                        ws.addCell(
                            Number(
                                2 * i + 2,
                                totalRow,
                                currBillList.sumBy { it.salary }.div(100.0),
                                wcfTotal
                            )
                        )
                    }
                    wb.write()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    wb?.close()
                }

            }
            Toast.makeText(baseContext, "导出成功", Toast.LENGTH_SHORT).show()
            baseContext.shareFile(file)
        }
    }


}
