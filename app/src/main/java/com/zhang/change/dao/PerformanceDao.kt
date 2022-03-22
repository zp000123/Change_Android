package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.Performance
import com.zhang.change.utils.getMaxDateStampDay
import com.zhang.change.utils.getMinDateStampDay

/**
 * 业绩、工资相关的数据表操作类
 */
@Dao
interface PerformanceDao {
    /** 查询所有的业绩工资数据 */
    @Query("SELECT * from performance")
    suspend fun queryAll(): List<Performance>
    /** 插入数据 */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: Performance)
    /** 删除数据 */
    @Delete
    suspend fun delete(data: Performance)
    /** 根据主键删除业绩工资记录 */
    @Query("DELETE  From performance WHERE pid=:pid")
    suspend fun deleteById(pid: Int)
    /** 根据日期删除员工的业绩工资记录 */
    @Query("DELETE  From performance WHERE userId=:uId AND dateStamp  BETWEEN :minDate AND :maxDate")
    suspend fun delete(uId: Int, minDate: Long, maxDate: Long)

    /** 根据日期计算总收入 */
    @Query("SELECT  SUM(performance.income)  From performance WHERE  dateStamp   BETWEEN :minDate AND :maxDate")
    suspend fun sumIncomeByDate(minDate: Long, maxDate: Long): Int?

    /** 根据日期计算总薪水 */
    @Query("SELECT  SUM(performance.salary)  From performance WHERE  dateStamp   BETWEEN :minDate AND :maxDate")
    suspend fun sumSalaryByDate(minDate: Long, maxDate: Long): Long
}

/**
 * 开启事务插入或替换数据。
 */
@Transaction
suspend fun PerformanceDao.insertReplace(uId: Int, dateStamp: Long, income: Int, salary: Int) {
    this.delete(uId, dateStamp.getMinDateStampDay(), dateStamp.getMaxDateStampDay())
    this.insert(Performance(uId, dateStamp, income, salary))
}

