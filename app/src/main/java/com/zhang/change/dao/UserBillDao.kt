package com.zhang.change.dao

import androidx.room.Dao
import androidx.room.Query
import com.zhang.change.entitiy.UserBill

@Dao
interface UserBillDao {
    @Query("SELECT * from userbill")
    suspend fun queryAllUserBill(): List<UserBill>

    @Query("SELECT * from userbill WHERE dateStamp BETWEEN :minDate AND :maxDate order by `no` asc")
    suspend fun queryBillByDate(minDate: Long, maxDate: Long): List<UserBill>

    @Query("SELECT * from userbill WHERE `no` = :no AND dateStamp BETWEEN :minDate AND :maxDate order by dateStamp  asc")
    suspend fun queryBillByNoAndDate(no: Int, minDate: Long, maxDate: Long): List<UserBill>
}