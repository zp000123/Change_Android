package com.zhang.change.entitiy

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    indices = [Index(value = ["userId"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("userId")
        , onDelete = CASCADE
    )]
)
/**
 * 业绩 / 工资表
 */
data class Performance(
    @ColumnInfo(name = "userId")
    var userId: Int = 0,
    var dateStamp: Long = 0L, // 毫秒
    var income: Int = 0, // 分
    var salary: Int = 0 // 分
) {
    @PrimaryKey(autoGenerate = true)
    var pid: Int = 0
}