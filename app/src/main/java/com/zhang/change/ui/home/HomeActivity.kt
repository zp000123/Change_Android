package com.zhang.change.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
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


    private fun initEmptyBill(): ArrayList<UserBill> {
        val arrayList = arrayListOf<UserBill>()
        val minDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val minDateStamp = getMinDateStamp(calendar)
        val endDay = if (isCurrMonth()) {
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

            this@HomeActivity.billList.let {
                it.clear()
                it.addAll(emptyBill)
            }
            billDateAdapter.notifyDataSetChanged()
            tv_total_income.text = billList.sumBy { it.income }.getNicePenStr()
            tv_total_salary.text = billList.sumBy { it.salary }.getNicePenStr()

        }
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
        }
        return super.onOptionsItemSelected(item)
    }


}
