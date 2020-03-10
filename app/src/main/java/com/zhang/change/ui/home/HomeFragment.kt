package com.zhang.change.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhang.change.MainActivity
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.adapter.BillDateAdapter
import com.zhang.change.adapter.getNicePenStr
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*

class HomeFragment : Fragment(), CoroutineScope by MainScope() {
    private val userList = arrayListOf<User>()
    private lateinit var mContext: Context
    private lateinit var userDao: UserDao
    private lateinit var userBillDao: UserBillDao
    private lateinit var performanceDao: PerformanceDao
    private val billList = arrayListOf<UserBill>()
    private val billDateAdapter = BillDateAdapter(billList)
    private var curUser: User? = null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDao()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(rv) {
            layoutManager = LinearLayoutManager(context)
            adapter = billDateAdapter
        }
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
            this@HomeFragment.userList.let {
                it.clear()
                it.addAll(userList)
            }
            curUser = userList.getOrNull(0)
            findUserBillAndRefreshView()
        }
    }

    private fun findUserBillAndRefreshView() {
        if (curUser == null) return
        launch {
            val billList = withContext(Dispatchers.Default) {
                userBillDao.queryAllUserBillByNo(curUser!!.no)
            }
            this@HomeFragment.billList.let {
                it.clear()
                it.addAll(billList)
            }
            billDateAdapter.notifyDataSetChanged()
            tv_total_income.text = billList.sumBy { it.income }.getNicePenStr()
            tv_total_salary.text = billList.sumBy { it.salary }.getNicePenStr()

        }
    }
}
