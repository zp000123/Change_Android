package com.zhang.change.dao

import androidx.room.Dao
import androidx.room.Query
import com.zhang.change.entitiy.UserBill

@Dao
interface UserBillDao {
    @Query("SELECT * from userbill")
    suspend fun queryAllUserBill(): List<UserBill>

    @Query("SELECT * from userbill WHERE dateStamp BETWEEN :minDate AND :maxDate")
    suspend fun queryAllUserBillByDate(minDate: Long, maxDate: Long): List<UserBill>

    @Query("SELECT * from userbill WHERE `no` = :no")
    suspend fun queryAllUserBillByNo(no: Int): List<UserBill>
}