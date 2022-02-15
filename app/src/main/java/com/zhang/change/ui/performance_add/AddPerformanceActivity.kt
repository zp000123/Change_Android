package com.zhang.change.ui.performance_add


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import com.zhang.change.databinding.ActivityAddPerformanceBinding
import com.zhang.change.dialog.AddUserDialog
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import com.zhang.change.rounter.KEY_DATE
import com.zhang.change.utils.*
import kotlinx.coroutines.*
import java.util.*


class AddPerformanceActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val viewModel by viewModels<AddPerformanceViewModel>()

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
    private lateinit var binding: ActivityAddPerformanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dateStamp = intent.getLongExtra(KEY_DATE, calendar.apply {
            add(Calendar.DAY_OF_MONTH, -1) // 默认进来是昨天
        }.timeInMillis)
        calendar.timeInMillis = dateStamp

        initView()
    }


    private fun initView() {
        with(binding) {


            vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
            vDate.setOnClickListener {
                val dialog = DatePickerDialog(
                    this@AddPerformanceActivity,
                    OnDateSetListener { _, year, month, dayOfMonth ->
                        Log.d(TAG, "onDateSet: year: $year, month: $month, dayOfMonth: $dayOfMonth")
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
                        findUserBillAndRefreshView()
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
                findUserBillAndRefreshView()
            }
            vNext.setOnClickListener {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                vDate.text = calendar.timeInMillis.date2String(DateFormat.YYYY_MM_DD)
                findUserBillAndRefreshView()
            }

            findUserListAndRefreshView()
            findUserBillAndRefreshView()

            vAdd.setOnClickListener {
                val dialog = AddUserDialog()
                dialog.show(supportFragmentManager) { user ->
                    selectUser = user
                    findUserListAndRefreshView()
                }
            }

            with(rvNo) {
                layoutManager = GridLayoutManager(context, 5)
                adapter = userAdapter
            }

            with(rvPerformance) {
                layoutManager = LinearLayoutManager(context)
                adapter = billAdapter
            }
        }
    }

    private fun showDelDialog(item: UserBill) {
        AlertDialog.Builder(this)
            .setTitle("确认删除 #" + item.no + " 的数据吗？")
            .setPositiveButton("确定") { d, w ->
                launch {
                    withContext(Dispatchers.Default) {
                     viewModel.deleteById(item.pid)
                    }
                    findUserBillAndRefreshView()
                }
            }
            .setNegativeButton("取消") { d, w ->

            }.show()
    }


    private fun findUserListAndRefreshView() {
        launch {
            val userList = withContext(Dispatchers.Default) { viewModel.queryAllUser()}
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
                viewModel.queryBillByDate(
                    minMills,
                    maxMills
                )
            }

            this@AddPerformanceActivity.billList.let {
                it.clear()
                it.addAll(billList)
            }
            billAdapter.notifyDataSetChanged()
            binding.tvTotalIncome.text = billList.sumBy { it.income }.getNiceStr()
            binding.tvTotalSalary.text = billList.sumBy { it.salary }.getNiceStr()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
        with(binding) {

            if (etIncome.text.isEmpty()) {
                toast("请输入业绩")
                return
            }
            val income = etIncome.text.toString().toBigDecimal().multiply(BigDecimal_100).toInt()
            if (etIncome.text.isEmpty()) {
                toast("请输入工资")
                return
            }
            val salary = etSalary.text.toString().toBigDecimal().multiply(BigDecimal_100).toInt()
            if (selectUser == null) {
                toast("请选择工号，或者添加工号")
                return
            }
            if (billList.map { it.uid }.contains(selectUser!!.uid)) {
                AlertDialog.Builder(this@AddPerformanceActivity)
                    .setTitle("已有 #" + selectUser!!.no + " 的数据，确认替换？")
                    .setPositiveButton("确定") { d, w ->
                        insertOrReplacePerformance(income, salary)
                    }
                    .setNegativeButton("取消") { d, w ->

                    }.show()
            } else {
                insertOrReplacePerformance(income, salary)
            }
        }
    }

    private fun insertOrReplacePerformance(income: Int, salary: Int) {

        launch {
            withContext(Dispatchers.Default) {
                viewModel
                    .insertReplace(selectUser!!.uid, calendar.timeInMillis, income, salary)
            }
            toast("添加成功")
            binding.etIncome.setText("")
            binding.etSalary.setText("")
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
