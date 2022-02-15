package com.zhang.change.ui.performance_add

import androidx.lifecycle.ViewModel
import com.zhang.change.AppHolder
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.dao.insertReplace
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill

class AddPerformanceViewModel: ViewModel() {

    private val userDao: UserDao by  lazy { AppHolder.db.userDao() }
    private val userBillDao: UserBillDao  by lazy { AppHolder.db.userBillDao() }
    private val performanceDao: PerformanceDao by  lazy { AppHolder.db.performanceDao() }

   suspend fun insertReplace(uId: Int, dateStamp: Long, income: Int, salary: Int) {
        performanceDao
            .insertReplace(uId,  dateStamp, income, salary)
    }

    suspend fun queryBillByDate(minDate: Long, maxDate: Long): List<UserBill>{
       return userBillDao.queryBillByDate(
            minDate,
            maxDate
        )
    }

    suspend fun queryAllUser(): List<User>{
      return  userDao.queryAllUser()
    }

    suspend fun deleteById(pid: Int){
        performanceDao.deleteById(pid)
    }

}