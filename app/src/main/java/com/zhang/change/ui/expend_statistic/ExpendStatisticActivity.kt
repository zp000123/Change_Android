package com.zhang.change.ui.expend_statistic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.adapter.ExpendDate
import com.zhang.change.adapter.ExpendDateAdapter
import com.zhang.change.dao.ExpendDao
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.ExpendType
import com.zhang.change.ui.expend_add.AddExpendActivity
import com.zhang.change.utils.*
import jxl.Workbook
import jxl.write.WritableWorkbook
import kotlinx.android.synthetic.main.activity_expend_statistic.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException
import java.util.*

/**
 * 开销记录统计
 */
class ExpendStatisticActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val calendar = Calendar.getInstance()
    private val expendDateList = arrayListOf<ExpendDate>()
    private val expendAdapter = ExpendDateAdapter(expendDateList)
    private lateinit var userDao: UserDao
    private lateinit var performanceDao: PerformanceDao
    private lateinit var expendDao: ExpendDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expend_statistic)
        initDao()
        initView()

    }

    private fun initView() {
        with(rv_expend) {
            layoutManager = LinearLayoutManager(baseContext)
            adapter = expendAdapter
        }
        v_prev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            setMothText()
            findExpendDateListAndRefreshView()
        }
        v_next.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setMothText()
            findExpendDateListAndRefreshView()
        }
        setMothText()
        findExpendDateListAndRefreshView()
    }

    @SuppressLint("SetTextI18n")
    private fun setMothText() {
        v_date.text = "${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月"
    }


    private suspend fun findExpendDateList(isMax: Boolean = false): Deferred<ArrayList<ExpendDate>> {

        val dateStamp = calendar.timeInMillis
        val minDay = dateStamp.getMinDayInMonth()
        val endDay = if (dateStamp.isCurrMonth() && !isMax) {
            dateStamp.getDayInMonth()
        } else {
            dateStamp.getMaxDayInMonth()
        }

        return async {
            val newExpendDateList = arrayListOf<ExpendDate>()
            val _calendar =
                Calendar.getInstance().apply { timeInMillis = dateStamp.getMinDateStampMonth() }
            for (i in minDay..endDay) {
                val nowStamp = _calendar.timeInMillis

                val minMillsNow = nowStamp.getMinDateStampDay()
                val maxMillsNow = nowStamp.getMaxDateStampDay()

                val dbTotalIncome =
                    performanceDao.sumIncomeByDate(minMillsNow, maxMillsNow) ?: 0
                val dbExpendDateList = expendDao.queryExpendList(minMillsNow, maxMillsNow)

                val expendDate = ExpendDate(nowStamp.date2String(DateFormat.M_D))
                expendDate.apply {
                    income = dbTotalIncome
                    expandList = dbExpendDateList
                }
                newExpendDateList.add(expendDate)
                _calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            newExpendDateList
        }
    }

    private fun findExpendDateListAndRefreshView() {


        launch {
            val newExpendDateList = findExpendDateList().await()
            expendDateList.clear()
            expendDateList.addAll(newExpendDateList)
            expendAdapter.notifyDataSetChanged()

            val totalIncome = expendDateList.sumBy { it.income }
            tv_total_income.text = totalIncome.getNiceStr()

            val expendList = expendDateList.flatMap { it.expandList }
            tv_total_living_cost.setSumText(expendList, ExpendType.LIVING_COST)
            tv_total_water_cost.setSumText(expendList, ExpendType.WATER_COST)
            tv_total_other.setSumText(expendList, ExpendType.OTHER)
            tv_total_salary.setSumText(expendList, ExpendType.SALARY)
            tv_total_draw.setSumText(expendList, ExpendType.DRAW)
            tv_total_group_purchase.setSumText(expendList, ExpendType.GROUP_PURCHASE)
            tv_total_receive_money.setSumText(expendList, ExpendType.RECEIVE_MONEY)
            tv_total_kou_bei.setSumText(expendList, ExpendType.KOU_BEI)
            tv_total_pos.setSumText(expendList, ExpendType.POS)


            val totalRecent = totalIncome - expendList.sumBy { it.money }
            tv_total_recent_money.text = totalRecent.getNiceStr()
            tv_total_recent_money.setColorByValue(totalRecent)

        }
    }


    private fun TextView.setSumText(expendList: List<Expend>, expendType: ExpendType) {
        val sum = expendList.sumExpendType(expendType)
        this.text = sum.getNiceStr()
        this.setColorByValue(sum)
    }

    private fun List<Expend>.sumExpendType(expendType: ExpendType): Int {
        return this.filter { it.type == expendType }.sumBy { it.money }
    }


    private fun initDao() {
        val db = (application as MyApplication).db
        userDao = db.userDao()
        expendDao = db.expendDao()
        performanceDao = db.performanceDao()
    }

    override fun onResume() {
        super.onResume()
        findExpendDateListAndRefreshView()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_expend_statistic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(baseContext, AddExpendActivity::class.java))
            }
            R.id.action_extract -> {
                doExtract()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doExtract() {
        val shopName = (application as MyApplication).shopName
        launch {
            val monthDes = calendar.timeInMillis.date2String(DateFormat.YYYY_MM)
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_DCIM),
                "${shopName}${monthDes}收支报表.xls"
            )
            withContext(Dispatchers.Default) {
                val dateStamp = calendar.timeInMillis

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

                    val wcfMinus = ExcelUtils.getMinusCellFormat()


                    val ws = wb!!.createSheet("sheet1", 0)

                    var nRow = 0
                    ws.addLabel(0, nRow++, "$shopName $monthDes 收支报表", wcfTitle)
                    ws.mergeCells(0, 0, 11, 0)
                    var nCol = 0
                    ws.addLabel(nCol++, nRow, getString(R.string.date), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.income), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.living_cost), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.water_cost), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.other), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.salary), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.draw), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.group_purchase), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.receiver_money), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.kou_bei), wcfContent)
                    ws.addLabel(nCol++, nRow, getString(R.string.pos), wcfContent)
                    ws.addLabel(nCol, nRow, getString(R.string.recent_money), wcfContent)
                    nCol = 0
                    nRow++
                    val dbExpendDateList = findExpendDateList().await()

                    val contentRow = nRow
                    // 添加日期,营业额
                    dbExpendDateList.forEach {
                        val row = nRow++
                        ws.addLabel(nCol, row, it.dateStr, wcfContent)
                        if (it.income != 0) {
                            ws.addNumber(nCol + 1, row, it.income.div(100.0), wcfContent)
                        }

                    }

                    nRow = contentRow
                    nCol = 1
                    // 添加内容
                    dbExpendDateList.forEach { expendDate ->
                        expendDate.expandList.forEach { expend ->
                            val money = expend.money
                            if (money != 0) {
                                when (expend.type) {
                                    ExpendType.LIVING_COST -> {
                                        ws.addNumber(2, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.WATER_COST -> {
                                        ws.addNumber(3, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.OTHER -> {
                                        ws.addNumber(4, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.SALARY -> {
                                        ws.addNumber(5, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.DRAW -> {
                                        ws.addNumber(6, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.GROUP_PURCHASE -> {
                                        ws.addNumber(7, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.RECEIVE_MONEY -> {
                                        ws.addNumber(8, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.KOU_BEI -> {
                                        ws.addNumber(9, nRow, money.div(100.0), wcfContent)
                                    }
                                    ExpendType.POS -> {
                                        ws.addNumber(10, nRow, money.div(100.0), wcfContent)
                                    }
                                }
                            }
                        }
                        nRow++
                    }

                    nRow = contentRow
                    // 添加剩余金额
                    dbExpendDateList.forEach { expendDate ->
                        val income = expendDate.income
                        val sumExpend = expendDate.expandList.sumBy { it.money }
                        val recentMoney = income - sumExpend

                        val wcf = if (recentMoney >= 0) wcfTotal else wcfMinus
                        ws.addNumber(11, nRow, recentMoney.div(100.0), wcf)
                        nRow++
                    }

                    val totalRow = nRow

                    ws.addLabel(0, totalRow, getString(R.string.total), wcfContent)
                    val totalIncome = expendDateList.sumBy { it.income }
                    ws.addNumber(1, totalRow, totalIncome.div(100.0), wcfContent)
                    val expendList = expendDateList.flatMap { it.expandList }
                    ws.addNumber(
                        2,
                        totalRow,
                        expendList.sumExpendType(ExpendType.LIVING_COST).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        3,
                        totalRow,
                        expendList.sumExpendType(ExpendType.WATER_COST).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        4,
                        totalRow,
                        expendList.sumExpendType(ExpendType.OTHER).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        5,
                        totalRow,
                        expendList.sumExpendType(ExpendType.SALARY).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        6,
                        totalRow,
                        expendList.sumExpendType(ExpendType.DRAW).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        7,
                        totalRow,
                        expendList.sumExpendType(ExpendType.GROUP_PURCHASE).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        8,
                        totalRow,
                        expendList.sumExpendType(ExpendType.RECEIVE_MONEY).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        9,
                        totalRow,
                        expendList.sumExpendType(ExpendType.KOU_BEI).div(100.0),
                        wcfContent
                    )
                    ws.addNumber(
                        10,
                        totalRow,
                        expendList.sumExpendType(ExpendType.POS).div(100.0),
                        wcfContent
                    )

                    val totalRecent = totalIncome - expendList.sumBy { it.money }
                    val wcf = if (totalRecent >= 0) wcfTotal else wcfMinus
                    ws.addNumber(11, totalRow, totalRecent.div(100.0), wcf)
                    wb.write()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    wb?.close()
                }

            }
            toast("导出成功")
            baseContext.shareFile(file)
        }
    }


}
