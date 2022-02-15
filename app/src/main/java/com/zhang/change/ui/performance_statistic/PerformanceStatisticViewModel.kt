package com.zhang.change.ui.performance_statistic

import androidx.lifecycle.ViewModel
import com.zhang.change.AppHolder
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill
import com.zhang.change.utils.SharePreferencesUtils

class PerformanceStatisticViewModel : ViewModel() {
    val shopName by lazy {
        SharePreferencesUtils.getCommonSpf(AppHolder.application)
            .getString(SharePreferencesUtils.SHOP_NAME, "").toString()
    }
    private val userDao: UserDao by lazy { AppHolder.db.userDao() }
    private val userBillDao: UserBillDao by lazy { AppHolder.db.userBillDao() }
    private val performanceDao: PerformanceDao by lazy { AppHolder.db.performanceDao() }


    suspend fun queryBillByDate(minDate: Long, maxDate: Long): List<UserBill> {
        return userBillDao.queryBillByDate(
            minDate,
            maxDate
        )
    }

    suspend fun queryAllUser(): List<User> {
        return userDao.queryAllUser()
    }

    suspend fun queryBillByNoAndDate(no: Int, minDate: Long, maxDate: Long): List<UserBill> {
        return userBillDao.queryBillByNoAndDate(
            no,
            minDate, maxDate
        )
    }
}