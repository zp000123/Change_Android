package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.Performance
import com.zhang.change.utils.getMaxDateStampDay
import com.zhang.change.utils.getMinDateStampDay

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
    this.delete(uId, dateStamp.getMinDateStampDay(), dateStamp.getMaxDateStampDay())
    this.insert(Performance(uId, dateStamp, income, salary))
}

