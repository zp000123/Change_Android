package com.zhang.change.dao

import androidx.room.Dao
import androidx.room.Query
import com.zhang.change.entitiy.UserBill

@Dao
interface UserBillDao {
    @Query("SELECT * from userbill")
    suspend fun queryAllUserBill(): List<UserBill>
}