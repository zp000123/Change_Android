package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.Expend
import com.zhang.change.utils.getMaxDateStampDay
import com.zhang.change.utils.getMinDateStampDay

/**
 * 开销表操作类
 */
@Dao
interface ExpendDao {
    /** 根据时间查询开销表中的记录 */
    @Query("SELECT * from expend WHERE dateStamp BETWEEN :minDate AND :maxDate order by type  asc")
    suspend fun queryExpendList(minDate: Long, maxDate: Long): List<Expend>
    /** 插入或替换数据 */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: Expend)
    /** 根据主键删除数据 */
    @Query("DELETE  From expend WHERE eId=:eId")
    suspend fun deleteById(eId: Int)
    /** 删除指定时间内的数据 */
    @Query("DELETE  From expend WHERE type=:type AND dateStamp  BETWEEN :minDate AND :maxDate")
    suspend fun delete(type: Int, minDate: Long, maxDate: Long)
}

@Transaction
suspend fun ExpendDao.insertReplace(expend: Expend) {
    val dateStamp = expend.dateStamp
    this.delete(expend.type.type, dateStamp.getMinDateStampDay(), dateStamp.getMaxDateStampDay())
    this.insert(expend)
}
