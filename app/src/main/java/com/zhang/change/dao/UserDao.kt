package com.zhang.change.dao

import androidx.room.*
import com.zhang.change.entitiy.User

@Dao
interface UserDao {
    @Query("SELECT * from user order by `no`")
    suspend fun queryAllUser(): List<User>

    @Query("SELECT * from user WHERE `no`=:no limit 1")
    suspend fun queryUserByNo(no: Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE from user WHERE `no` =:newNo")
    suspend fun deleteByNo(newNo: Int)

}