package com.zhang.change.entitiy

import androidx.room.DatabaseView
import java.util.*

@DatabaseView(
    "SELECT user.uid, user.no,user.name, " +
            "performance.pid, performance.dateStamp, performance.income, performance.salary " +
            "FROM user " +
            "INNER JOIN performance ON user.uid = performance.userId"
)
class UserBill(
    val uid: Int,
    val no: String,
    val name: Int,
    val pid: Int,
    val dateStamp: Long, // 毫秒
    val income: Int, // 分
    val salary: Int // 分
)