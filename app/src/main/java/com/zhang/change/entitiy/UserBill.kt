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
    var uid: Int,
    var no: Int,
    var name: String?,
    var pid: Int,
    var dateStamp: Long, // 毫秒
    var income: Int, // 分
    var salary: Int // 分
) {
    fun copy(dbBill: UserBill) {
         this.uid = dbBill.uid
        this.no = dbBill.no
        this.name = dbBill.name
        this.pid = dbBill.pid
        this.dateStamp = dbBill.dateStamp
        this.income = dbBill.income
        this.salary = dbBill.salary
    }

    companion object {
        fun of(dateStamp: Long): UserBill {
            return UserBill(0, 0, "", 0, dateStamp, 0, 0)
        }
    }
}