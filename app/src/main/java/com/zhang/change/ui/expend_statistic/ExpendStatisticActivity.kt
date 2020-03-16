package com.zhang.change.ui.expend_statistic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.zhang.change.ui.add_expend.AddExpendActivity
import com.zhang.change.utils.*
import kotlinx.android.synthetic.main.activity_expend_statistic.*
import kotlinx.coroutines.*
import java.util.*

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
            findExpendDateList()
        }
        v_next.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setMothText()
            findExpendDateList()
        }
        setMothText()
        findExpendDateList()
    }

    @SuppressLint("SetTextI18n")
    private fun setMothText() {
        v_date.text = "${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月"
    }

    private fun findExpendDateList() {
        val dateStamp = calendar.timeInMillis
        val minDay = dateStamp.getMinDayInMonth()
        val endDay = if (dateStamp.isCurrMonth()) {
            dateStamp.getDayInMonth()
        } else {
            dateStamp.getMaxDayInMonth()
        }
        launch {
            expendDateList.clear()
            expendAdapter.notifyDataSetChanged()
            withContext(Dispatchers.Default) {
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
                    expendDateList.add(expendDate)
                    _calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
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
        val sum = expendList.filter { it.type == expendType }.sumBy { it.money }
        this.text = sum.getNiceStr()
        this.setColorByValue(sum)
    }


    private fun initDao() {
        val db = (application as MyApplication).db
        userDao = db.userDao()
        expendDao = db.expendDao()
        performanceDao = db.performanceDao()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_performance_statistic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(baseContext, AddExpendActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
