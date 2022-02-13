package com.zhang.change.entitiy

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class User(
    var no: Int = 0,
    var name: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
    @Ignore
    var u_selected = false
}

