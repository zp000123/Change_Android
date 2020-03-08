package com.zhang.change.entitiy

import androidx.room.*

@Entity(
    indices = [Index(value = ["userId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("userId")
    )]
)
data class Performance(
    @ColumnInfo(name = "userId")
    val userId: Int = 0,
    val dateStamp: Long = 0L, // 毫秒
    val income: Int = 0, // 分
    val salary: Int = 0 // 分
) {
    @PrimaryKey(autoGenerate = true)
    var pid: Int = 0
}