package com.zhang.change.converter

import androidx.room.TypeConverter
import com.zhang.change.entitiy.ExpendType
import java.util.*

/**
 * 数据库相关的装换类
 */
class Converters {
    @TypeConverter
    fun fromExpendType(value: ExpendType?): Int {
        return value?.type ?: 0
    }

    @TypeConverter
    fun toExpendType(value: Int): ExpendType? {
        return ExpendType.ofType(value)
    }
}