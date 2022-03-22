package com.zhang.change

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zhang.change.converter.Converters
import com.zhang.change.dao.ExpendDao
import com.zhang.change.dao.PerformanceDao
import com.zhang.change.dao.UserBillDao
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.Performance
import com.zhang.change.entitiy.User
import com.zhang.change.entitiy.UserBill

/**
 * Room 数据库配置类
 */
@Database(
    entities = [User::class, Performance::class, Expend::class], // 数据表
    views = [UserBill::class], // 视图
    version = 2,// 数据库版本号
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun performanceDao(): PerformanceDao
    abstract fun userBillDao(): UserBillDao
    abstract fun expendDao(): ExpendDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(lock = AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        val builder =
                            Room.databaseBuilder(context, AppDatabase::class.java, "bill.db")
                                .allowMainThreadQueries()

                        builder.addMigrations(M_1_2)
                        INSTANCE = builder
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val M_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Expend` (`eId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` INTEGER NOT NULL, `money` INTEGER NOT NULL, `dateStamp` INTEGER NOT NULL)")
            }
        }
    }


}