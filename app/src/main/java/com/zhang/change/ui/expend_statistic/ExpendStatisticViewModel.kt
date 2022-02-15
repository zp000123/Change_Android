package com.zhang.change.ui.expend_statistic

import androidx.lifecycle.ViewModel
import com.zhang.change.AppHolder
import com.zhang.change.dao.ExpendDao
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.entitiy.Expend
import com.zhang.change.utils.SharePreferencesUtils

class ExpendStatisticViewModel : ViewModel() {
    val shopName by lazy {
        SharePreferencesUtils.getCommonSpf(AppHolder.application)
            .getString(SharePreferencesUtils.SHOP_NAME, "").toString()
    }

    private val expendDao: ExpendDao by lazy { AppHolder.db.expendDao() }
    private val performanceDao: PerformanceDao by lazy { AppHolder.db.performanceDao() }

    suspend fun sumIncomeByDate(minDate: Long, maxDate: Long): Int? {
        return performanceDao.sumIncomeByDate(minDate, maxDate)
    }

    suspend fun queryExpendList(minDate: Long, maxDate: Long): List<Expend> {
        return expendDao.queryExpendList(minDate, maxDate)
    }
}