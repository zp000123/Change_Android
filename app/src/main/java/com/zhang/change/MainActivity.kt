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
import com.zhang.change.adapter.UserListAdapter
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.Performance
import com.zhang.change.entitiy.User
import com.zhang.change.utils.DateFormat
import com.zhang.change.utils.date2String
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var userDao: UserDao
    private lateinit var userBillDao: UserBillDao
    private lateinit var performanceDao: PerformanceDao
    private val calendar = Calendar.getInstance()
    private val userList = arrayListOf<User>()
    private val userAdapter = UserListAdapter(userList)
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
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }


        findUserListAndRefreshView()


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
                }
                findUserListAndRefreshView()
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
        launch {
            withContext(Dispatchers.Default) {
                performanceDao
                    .insert(Performance(selectUser!!.uid, calendar.timeInMillis, income, salary))
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    companion object {
        const val TAG = "MainActivity"
        val BigDecimal_100 = BigDecimal(100)
    }

}
