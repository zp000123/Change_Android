package com.zhang.change.entitiy

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 员工表
 */
@Entity
data class User(
    val no: Int = 0,
    val name: String? = null // 保留字段没有用到
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
    @Ignore
    var u_selected = false
}

