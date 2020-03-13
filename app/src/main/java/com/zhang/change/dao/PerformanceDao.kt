package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.Performance

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

    @Query("DELETE  From performance WHERE userId=:uId AND dateStamp ==:dateStamp")
    suspend fun delete(uId: Int, dateStamp: Long)
}

@Transaction
suspend fun PerformanceDao.insertReplace(uId: Int, dateStamp: Long, income: Int, salary: Int) {
    this.delete(uId, dateStamp)
    this.insert(Performance(uId, dateStamp, income, salary))
}