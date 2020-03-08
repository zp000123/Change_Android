package com.zhang.change.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    val no: Int = 0,
    val name: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

