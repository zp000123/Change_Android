package com.zhang.change.ui.expend_add

import androidx.lifecycle.ViewModel
import com.zhang.change.AppHolder
import com.zhang.change.dao.insertReplace
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.ExpendType
import java.util.*

class AddExpendViewModel : ViewModel() {
    val calendar = Calendar.getInstance()
    private val userDao by lazy { AppHolder.db.userDao() }
    private val expendDao by lazy { AppHolder.db.expendDao() }
    private val performanceDao by lazy { AppHolder.db.performanceDao() }



    suspend fun insertReplace(selectType: ExpendType, money: Int) {
        expendDao.insertReplace(Expend(selectType, money, calendar.timeInMillis))
    }

    suspend fun sumIncomeByDate(minDate: Long, maxDate: Long): Int? {
        return performanceDao.sumIncomeByDate(minDate, maxDate)
    }

    suspend fun queryExpendList(minDate: Long, maxDate: Long): List<Expend> {
        return expendDao.queryExpendList(minDate, maxDate)
    }

    suspend fun deleteById(eId: Int) {
        expendDao.deleteById(eId)
    }
}