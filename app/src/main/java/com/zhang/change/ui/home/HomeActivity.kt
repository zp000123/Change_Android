package com.zhang.change.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment.DIRECTORY_DCIM
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.adapter.BillDateAdapter
import com.zhang.change.adapter.UserListAdapter
import com.zhang.change.adapter.getNicePenStr
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.dialog.AddUserDialog
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import com.zhang.change.ui.add_performance.AddPerformanceActivity
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import com.zhang.change.utils.shareFile
import jxl.Workbook
import jxl.format.Alignment
import jxl.format.VerticalAlignment
import jxl.write.Label
import jxl.write.Number
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import java.io.File
import java.util.*


class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = baseContext
        setContentView(R.layout.activity_home)
        initDao()
        initView()
    }


    private fun initView() {
        setMothText()
        with(rv) {
            layoutManager = LinearLayoutManager(context)
            adapter = billDateAdapter
        }
        with(rv_user) {
            layoutManager = GridLayoutManager(context, 5)
            adapter = userListAdapter
        }
        findUserListAndRefreshView()

        v_add_user.setOnClickListener {
            val dialog = AddUserDialog()
            dialog.show(supportFragmentManager, userDao) { user ->
                curUser = user
                findUserListAndRefreshView()
            }
        }
        v_prev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            setMothText()
            findUserBillAndRefreshView()
        }
        v_next.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setMothText()
            findUserBillAndRefreshView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMothText() {
        v_date.text = "${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月"
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
            this@HomeActivity.userList.let {
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
        val minDateStamp = getMinDateStamp(calendar)
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

    private fun getMinDateStamp(_calendar: Calendar): Long {
        val calendar = _calendar.clone() as Calendar

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))
        return calendar.timeInMillis
    }

    private fun getMaxDateStamp(_calendar: Calendar): Long {
        val calendar = _calendar.clone() as Calendar

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND))

        return calendar.timeInMillis
    }

    private fun findUserBillAndRefreshView() {
        if (curUser == null) return
        launch {
            val dbBillList = withContext(Dispatchers.Default) {
                userBillDao.queryBillByNoAndDate(
                    curUser!!.no,
                    getMinDateStamp(calendar),
                    getMaxDateStamp(calendar)
                )
            }
            val emptyBill = initEmptyBill()

            copyDate2EmptyList(emptyBill, dbBillList)

            this@HomeActivity.billList.let {
                it.clear()
                it.addAll(emptyBill)
            }
            billDateAdapter.notifyDataSetChanged()
            tv_total_income.text = billList.sumBy { it.income }.getNicePenStr()
            tv_total_salary.text = billList.sumBy { it.salary }.getNicePenStr()

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


    private fun copyDate2EmptyListGroupByUserId(dbBillList: List<UserBill>): HashMap<Int, List<UserBill>> {
        val userBillMap = dbBillList.groupBy { it.uid }
        val uIdBillMap = hashMapOf<Int, List<UserBill>>()
        userBillMap.forEach {
            val list = it.value
            uIdBillMap[it.key] = copyDate2EmptyList(initEmptyBill(true), list)
        }
        return uIdBillMap
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
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
            val MonthDes = calendar.timeInMillis.date2String(DateFormat.YYYY_MM)
            val file = File(
                getExternalFilesDir(DIRECTORY_DCIM),
                "茭白园路店$MonthDes.xls"
            )


            withContext(Dispatchers.Default) {
                val billList = userBillDao.queryBillByDate(
                    getMinDateStamp(calendar),
                    getMaxDateStamp(calendar)
                )

                val uIdBillMap = copyDate2EmptyListGroupByUserId(billList)
                val userList =
                    billList.map { User(it.no, it.name).apply { uid = it.uid } }.distinct()

                if ((!file.exists())) {
                    file.createNewFile()
                } else {
                    file.delete()
                    file.createNewFile()
                }
                val wb = Workbook.createWorkbook(file) //创建xls表格文件
                // 表头显示
                val wcf = WritableCellFormat()
                wcf.alignment = Alignment.CENTRE // 水平居中
                wcf.verticalAlignment = VerticalAlignment.CENTRE // 垂直居中
                wcf.setFont(WritableFont(WritableFont.TIMES, 13, WritableFont.BOLD)) // 表头字体 加粗 13号


                // 内容显示
                val wcf2 = WritableCellFormat()
                wcf2.alignment = Alignment.CENTRE //水平居中
                wcf2.verticalAlignment = VerticalAlignment.CENTRE // 垂直居中
                wcf2.setFont(WritableFont(WritableFont.TIMES, 11)) // 内容字体 11号

                val ws = wb!!.createSheet("sheet1", 0)

                ws.addCell(Label(0, 0, "茭白园路店 $MonthDes", wcf))

                ws.addCell(Label(0, 1, getString(R.string.no), wcf))
                var totalCol = 0
                // 导出时生成表头
                for (i in userList.indices) { // 工号
                    ws.addCell(Label(2 * i + 1, 1, "#${userList[i].no}", wcf))
                    ws.addCell(Label(2 * i + 2, 1, " ", wcf))
                    totalCol = 2 * i + 2
                    ws.mergeCells(2 * i + 1, 1, 2 * i + 2, 1)
                }
                ws.mergeCells(0, 0, totalCol, 0)
                ws.addCell(Label(0, 2, getString(R.string.date), wcf))
                val perStr = getString(R.string.performance)
                val salaryStr = getString(R.string.salary)
                for (i in userList.indices) {
                    ws.addCell(Label(2 * i + 1, 2, perStr, wcf))
                    ws.addCell(Label(2 * i + 2, 2, salaryStr, wcf))
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
                                    wcf2
                                )
                            )
                        }
                        if (userBill.salary != 0) {
                            ws.addCell(
                                Number(
                                    2 * i + 2,
                                    3 + index,
                                    userBill.salary.div(100.0),
                                    wcf2
                                )
                            )
                        }
                    }
                }

                val dateList = initEmptyBill(true)

                dateList.forEachIndexed { index, userBill ->
                    val dateStamp = userBill.dateStamp
                    ws.addCell(Label(0, 3 + index, dateStamp.date2String(DateFormat.M_D), wcf2))
                }
                val totalRow = 3 + dateList.size

                ws.addCell(Label(0, totalRow, getString(R.string.total), wcf2))

                for (i in userList.indices) { // 填充内容
                    val user = userList[i]
                    val currBillList = uIdBillMap[user.uid].orEmpty()
                    ws.addCell(
                        Number(
                            2 * i + 1,
                            totalRow,
                            currBillList.sumBy { it.income }.div(100.0),
                            wcf2
                        )
                    )
                    ws.addCell(
                        Number(
                            2 * i + 2,
                            totalRow,
                            currBillList.sumBy { it.salary }.div(100.0),
                            wcf2
                        )
                    )
                }

                wb.write()
                wb.close()
            }
            toast("导出成功")
            val uri = FileProvider.getUriForFile(
                baseContext,
                "com.zhang.change.provider", //(use your app signature + ".provider" )
                file
            )
            baseContext.shareFile(uri)
        }
    }


}
