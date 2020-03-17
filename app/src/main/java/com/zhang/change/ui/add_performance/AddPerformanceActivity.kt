package com.zhang.change.ui.add_performance


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.adapter.BillAdapter
import com.zhang.change.adapter.UserListAdapter
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.dao.insertReplace
import com.zhang.change.dialog.AddUserDialog
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import com.zhang.change.rounter.KEY_DATE
import com.zhang.change.utils.BigDecimal_100
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import com.zhang.change.utils.getNiceStr
import kotlinx.android.synthetic.main.activity_add_performance.*
import kotlinx.coroutines.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*


class AddPerformanceActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var userDao: UserDao
    private lateinit var userBillDao: UserBillDao
    private lateinit var performanceDao: PerformanceDao
    private val calendar = Calendar.getInstance()
    private val userList = arrayListOf<User>()
    private val billList = arrayListOf<UserBill>()
    private val userAdapter = UserListAdapter(userList) {
        selectUser = it
    }
    private val billAdapter = BillAdapter(billList) {
        showDelDialog(it)
    }
    private var selectUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_performance)
        val dateStamp = intent.getLongExtra(KEY_DATE, calendar.apply {
            add(Calendar.DAY_OF_MONTH, -1) // 默认进来是昨天
        }.timeInMillis)
        calendar.timeInMillis = dateStamp
        initDao()
        initView()
    }


    private fun initView() {
        v_date.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
        v_date.setOnClickListener {
            val dialog = DatePickerDialog(
                this@AddPerformanceActivity,
                OnDateSetListener { _, year, month, dayOfMonth ->
                    Log.d(TAG, "onDateSet: year: $year, month: $month, dayOfMonth: $dayOfMonth")
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    v_date.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
                    findUserBillAndRefreshView()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        v_prev.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            v_date.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
            findUserBillAndRefreshView()
        }
        v_next.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            v_date.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
            findUserBillAndRefreshView()
        }

        findUserListAndRefreshView()
        findUserBillAndRefreshView()

        v_add.setOnClickListener {
            val dialog = AddUserDialog()
            dialog.show(supportFragmentManager, userDao) { user ->
                selectUser = user
                findUserListAndRefreshView()
            }
        }

        with(rv_no) {
            layoutManager = GridLayoutManager(context, 5)
            adapter = userAdapter
        }

        with(rv_performance) {
            layoutManager = LinearLayoutManager(context)
            adapter = billAdapter
        }

    }

    private fun showDelDialog(item: UserBill) {
        alert("确认删除 #" + item.no + " 的数据吗？") {
            yesButton {
                launch {
                    withContext(Dispatchers.Default) {
                        performanceDao.deleteById(item.pid)
                    }
                    findUserBillAndRefreshView()
                }
            }
            noButton { }
        }.show()
    }


    private fun findUserListAndRefreshView() {
        launch {
            val userList = withContext(Dispatchers.Default) { userDao.queryAllUser() }
            this@AddPerformanceActivity.userList.let {
                it.clear()
                it.addAll(userList)
            }
            userList.forEach { it.u_selected = false }
            if (selectUser == null) selectUser = userList.getOrNull(0)
            userList.find { it.no == selectUser?.no }?.u_selected = true
            userAdapter.notifyDataSetChanged()
        }
    }


    private fun findUserBillAndRefreshView() {
        launch {
            val _calendar = calendar.clone() as Calendar
            _calendar.set(Calendar.HOUR_OF_DAY, _calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
            _calendar.set(Calendar.MINUTE, _calendar.getActualMinimum(Calendar.MINUTE))
            _calendar.set(Calendar.SECOND, _calendar.getActualMinimum(Calendar.SECOND))
            _calendar.set(Calendar.MILLISECOND, _calendar.getActualMinimum(Calendar.MILLISECOND))


            val minMills = _calendar.timeInMillis
            _calendar.add(Calendar.DAY_OF_MONTH, 1)
            val maxMills = _calendar.timeInMillis - 1
            val billList = withContext(Dispatchers.Default) {
                userBillDao.queryBillByDate(
                    minMills,
                    maxMills
                )
            }

            this@AddPerformanceActivity.billList.let {
                it.clear()
                it.addAll(billList)
            }
            billAdapter.notifyDataSetChanged()
            tv_total_income.text = billList.sumBy { it.income }.getNiceStr()
            tv_total_salary.text = billList.sumBy { it.salary }.getNiceStr()
        }
    }

    private fun initDao() {
        val db = (application as MyApplication).db
        userDao = db.userDao()
        userBillDao = db.userBillDao()
        performanceDao = db.performanceDao()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_performance, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_save -> save()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        if (et_income.text.isEmpty()) {
            toast("请输入业绩")
            return
        }
        val income = et_income.text.toString().toBigDecimal().multiply(BigDecimal_100).toInt()
        if (et_income.text.isEmpty()) {
            toast("请输入工资")
            return
        }
        val salary = et_salary.text.toString().toBigDecimal().multiply(BigDecimal_100).toInt()
        if (selectUser == null) {
            toast("请选择工号，或者添加工号")
            return
        }
        if (billList.map { it.uid }.contains(selectUser!!.uid)) {
            alert("已有 #" + selectUser!!.no + " 的数据，确认替换？") {
                yesButton {

                    insertOrReplacePerformance(income, salary)
                }
                noButton { }
            }.show()
        } else {
            insertOrReplacePerformance(income, salary)
        }

    }

    private fun insertOrReplacePerformance(income: Int, salary: Int) {

        launch {
            withContext(Dispatchers.Default) {
                performanceDao
                    .insertReplace(selectUser!!.uid, calendar.timeInMillis, income, salary)
            }
            toast("添加成功")
            et_income.setText("")
            et_salary.setText("")
            findUserBillAndRefreshView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    companion object {
        const val TAG = "MainActivity"

    }

}
