package com.zhang.change.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.UserBill

@Dao
interface ExpendDao {
    @Query("SELECT * from expend WHERE dateStamp BETWEEN :minDate AND :maxDate order by type  asc")
    suspend fun queryExpendList(minDate: Long, maxDate: Long): List<UserBill>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: Expend)

    @Query("DELETE  From expend WHERE eId=:eId")
    suspend fun deleteById(eId: Int)

}