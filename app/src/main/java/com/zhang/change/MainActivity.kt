package com.zhang.change


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zhang.change.adapter.BillAdapter
import com.zhang.change.adapter.UserListAdapter
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.Performance
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var userDao: UserDao
    private lateinit var userBillDao: UserBillDao
    private lateinit var performanceDao: PerformanceDao
    private val calendar = Calendar.getInstance()
    private val userList = arrayListOf<User>()
    private val billList = arrayListOf<UserBill>()
    private val userAdapter = UserListAdapter(userList)
    private val billAdapter = BillAdapter(billList)
    private var selectUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDao()
        initView()
    }


    private fun initView() {
        calendar.add(Calendar.DATE, -1)
        v_date.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
        v_date.setOnClickListener {
            val dialog = DatePickerDialog(
                this@MainActivity,
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
            if (et_no.text.isEmpty()) {
                Toast.makeText(baseContext, "请输入工号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newNo = et_no.text.toString().toInt()

            userList.forEach {
                if (it.no == newNo) {
                    Toast.makeText(baseContext, "已有改工号，无需添加", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            launch {
                withContext(Dispatchers.Default) {
                    userDao.insert(User(no = newNo))
                    selectUser = userDao.queryUserByNo(newNo)
                }
                findUserListAndRefreshView()
                Snackbar.make(rv_performance, "添加 #${newNo}", Snackbar.LENGTH_LONG)
                    .setAction("取消") {
                        // todo 取消无效
                        launch {
                            selectUser = null
                            withContext(Dispatchers.Default) {
                                userDao.deleteByNo(newNo)
                            }
                            findUserListAndRefreshView()
                        }
                    }.show()
            }
        }

        with(rv_no) {
            layoutManager = GridLayoutManager(context, 4)
            adapter = userAdapter
        }
        userAdapter.setOnItemClickListener { _, _, position ->
            selectUser = userList[position]
            et_no.setText(selectUser!!.no.toString())
        }

        with(rv_performance) {
            layoutManager = LinearLayoutManager(context)
            adapter = billAdapter
        }
        billAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.v_del -> {
                    val item = billList[position]
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
                R.id.v_edit -> {

                }
            }
        }
    }


    private fun findUserListAndRefreshView() {
        launch {
            val userList = withContext(Dispatchers.Default) { userDao.queryAllUser() }
            this@MainActivity.userList.let {
                it.clear()
                it.addAll(userList)
            }
            userAdapter.notifyDataSetChanged()
        }
    }


    private fun findUserBillAndRefreshView() {
        launch {
            val minDate = calendar.timeInMillis / ONE_DAY_MILLIS * ONE_DAY_MILLIS
            val maxDate = minDate + ONE_DAY_MILLIS
            val billList = withContext(Dispatchers.Default) {
                userBillDao.queryAllUserBillByDate(
                    minDate,
                    maxDate
                )
            }

            this@MainActivity.billList.let {
                it.clear()
                it.addAll(billList)
            }
            billAdapter.notifyDataSetChanged()
        }
    }

    private fun initDao() {
        val db = (application as MyApplication).db
        userDao = db.userDao()
        userBillDao = db.userBillDao()
        performanceDao = db.performanceDao()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu);
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
                    .insert(Performance(selectUser!!.uid, calendar.timeInMillis, income, salary))
            }
            findUserBillAndRefreshView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    companion object {
        const val TAG = "MainActivity"
        val BigDecimal_100 = BigDecimal(100)
        const val ONE_DAY_MILLIS = 86_400_000L
    }

}
