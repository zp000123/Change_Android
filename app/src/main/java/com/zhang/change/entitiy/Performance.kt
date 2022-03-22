package com.zhang.change.entitiy

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    indices = [Index(value = ["userId"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("userId"),
        onDelete = CASCADE // 外键关联，父键更改的时候，将所有子健进行更新。主要用于 onDelete(),onUpdate()。
    )]
)
/**
 * 业绩 / 工资表
 */
data class Performance(
    @ColumnInfo(name = "userId")
    val userId: Int = 0,
    val dateStamp: Long = 0L, // 毫秒
    val income: Int = 0, // 收入
    val salary: Int = 0 // 薪水
) {
    @PrimaryKey(autoGenerate = true)
    var pid: Int = 0
}