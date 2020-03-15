package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.Performance
import java.util.*

@Dao
interface PerformanceDao {
    @Query("SELECT * from performance")
    suspend fun queryAll(): List<Performance>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: Performance)

    @Delete
    suspend fun delete(data: Performance)

    @Query("DELETE  From performance WHERE pid=:pid")
    suspend fun deleteById(pid: Int)

    @Query("DELETE  From performance WHERE userId=:uId AND dateStamp   BETWEEN :minDate AND :maxDate")
    suspend fun delete(uId: Int, minDate: Long, maxDate: Long)
}

@Transaction
suspend fun PerformanceDao.insertReplace(uId: Int, dateStamp: Long, income: Int, salary: Int) {
    this.delete(uId, dateStamp.getMinDateStamp(), dateStamp.getMaxDateStamp())
    this.insert(Performance(uId, dateStamp, income, salary))
}

private fun Long.getMinDateStamp(): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMinDateStamp }
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))
    return calendar.timeInMillis
}

private fun Long.getMaxDateStamp(): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getMaxDateStamp }
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND))

    return calendar.timeInMillis
}