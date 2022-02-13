package com.zhang.change.ui.expend_add

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.adapter.ExpandAdapter
import com.zhang.change.adapter.ExpendTypeAdapter
import com.zhang.change.dao.ExpendDao
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserDao
import com.zhang.change.dao.insertReplace
import com.zhang.change.databinding.ActivityAddExpendBinding
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.ExpendType
import com.zhang.change.entitiy.ExpendTypeW
import com.zhang.change.rounter.start2AddPerformanceActivity
import com.zhang.change.utils.*
import kotlinx.coroutines.*
import java.util.*

class AddExpendActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var userDao: UserDao
    private lateinit var performanceDao: PerformanceDao
    private lateinit var expendDao: ExpendDao
    private val calendar = Calendar.getInstance()
    private lateinit var binding: ActivityAddExpendBinding

    private val expendTypeList = arrayListOf<ExpendTypeW>().apply {
        addAll(ExpendTypeW.getExpendTypeWList())
    }
    private val expendList = arrayListOf<Expend>()
    private val expendTypeAdapter = ExpendTypeAdapter(expendTypeList) {
        selectType = it.expendType
    }
    private val expendAdapter = ExpandAdapter(expendList) {
        showDelDialog(it)
    }


    private var selectType: ExpendType? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDao()
        initView()
    }

    private fun initView() {
        selectType = expendTypeList.getOrNull(0)?.expendType
        calendar.add(Calendar.DATE, -1)
        with(binding) {
            vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
            vDate.setOnClickListener {
                val dialog = DatePickerDialog(
                    this@AddExpendActivity,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        Log.d(TAG, "onDateSet: year: $year, month: $month, dayOfMonth: $dayOfMonth")
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
                        findExpendAndRefreshView()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                dialog.show()
            }

            vPrev.setOnClickListener {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
                findExpendAndRefreshView()
            }
            vNext.setOnClickListener {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
                findExpendAndRefreshView()
            }

            vAddPerformance.setOnClickListener {
                start2AddPerformanceActivity(calendar.timeInMillis)
            }

            with(rvType) {
                layoutManager = GridLayoutManager(context, 5)
                adapter = expendTypeAdapter
            }

            with(rvExpend) {
                layoutManager = LinearLayoutManager(context)
                adapter = expendAdapter
            }

        }
        findExpendAndRefreshView()
    }

    private fun findExpendAndRefreshView() {
        val dateStamp = calendar.timeInMillis
        dateStamp.getMinDateStampDay()
        val minMills = dateStamp.getMinDateStampDay()
        val maxMills = dateStamp.getMaxDateStampDay()

        launch {
            val performanceSum =
                async { performanceDao.sumIncomeByDate(minMills, maxMills) }.await() ?: 0
            val dbExpendList = async { expendDao.queryExpendList(minMills, maxMills) }.await()
            this@AddExpendActivity.expendList.let {
                it.clear()
                it.addAll(dbExpendList)
            }
            expendAdapter.notifyDataSetChanged()

            with(binding) {
                vAddPerformance.visibility = if (performanceSum != 0) View.GONE else View.VISIBLE

                vIncome.text = performanceSum.getNiceStr()
                val recentMoney = (performanceSum - dbExpendList.sumBy { it.money })
                tvRecentMoney.text = recentMoney.div(100).toString()
                tvRecentMoney.setTextColor(
                    if (recentMoney >= 0) resources.getColor(R.color.black)
                    else resources.getColor(R.color.red))
            }

        }


    }


    private fun initDao() {
        val db = (application as MyApplication).db
        userDao = db.userDao()
        expendDao = db.expendDao()
        performanceDao = db.performanceDao()
    }


    private fun showDelDialog(item: Expend) {
        AlertDialog.Builder(baseContext)
            .setTitle("确认删除 #" + item.type.des + " 的数据吗？")
            .setPositiveButton("确定") { d, w ->
                launch {
                    withContext(Dispatchers.Default) {
                        expendDao.deleteById(item.eId)
                    }
                    findExpendAndRefreshView()
                }
            }
            .setNegativeButton("取消") { d, w ->

            }.show()


    }

    private fun save() {
        with(binding){

        if (etMoney.text.isEmpty()) {
            toast("请输入金额")
            return
        }
        val money = etMoney.text.toString().toBigDecimal().multiply(BigDecimal_100).toInt()
        if (selectType == null) {
            toast("请选择类型")
            return
        }
        if (expendList.map { it.type }.contains(selectType!!)) {
            AlertDialog.Builder(baseContext)
                .setTitle("已有  ${selectType!!.des} 的数据，确认替换？")
                .setPositiveButton("确定") { d, w ->
                    insertOrReplaceExpend(selectType!!, money)
                }
                .setNegativeButton("取消") { d, w ->

                }.show()
        } else {
            insertOrReplaceExpend(selectType!!, money)
        }
        }
    }

    private fun insertOrReplaceExpend(selectType: ExpendType, money: Int) {
        launch {
            withContext(Dispatchers.Default) {
                expendDao
                    .insertReplace(Expend(selectType, money, calendar.timeInMillis))
            }
            toast("添加成功")
            binding.etMoney.setText("")
            findExpendAndRefreshView()
        }
    }


    override fun onResume() {
        super.onResume()
        findExpendAndRefreshView()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_expend, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_save -> save()

        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
