package com.zhang.change

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.Performance
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill

@Database(
    entities = [User::class, Performance::class],
    views = [UserBill::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun performanceDao(): PerformanceDao
    abstract fun userBillDao(): UserBillDao

}