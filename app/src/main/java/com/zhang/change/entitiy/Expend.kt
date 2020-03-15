package com.zhang.change.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 开销的类型
 * 金额
 * 日期
 */
@Entity
data class Expend(
    val type: ExpendType,
    val money: Long,//分
    val dateStamp: Long// 毫秒

) {
    @PrimaryKey(autoGenerate = true)
    var eId: Int = 0
}

enum class ExpendType(val type: Int) {
    LIVING_COST(1),
    WATER_COST(2),
    OTHER(3),
    SALARY(4),
    DRAW(5),
    GROUP_PURCHASE(6),
    RECEIVE_MONEY(7),
    KOU_BEI(8),
    POS(9);


    companion object {
        fun ofType(type: Int): ExpendType? {
            return when (type) {
                1 -> LIVING_COST
                2 -> WATER_COST
                3 -> OTHER
                4 -> SALARY
                5 -> DRAW
                6 -> GROUP_PURCHASE
                7 -> RECEIVE_MONEY
                8 -> KOU_BEI
                9 -> POS
                else -> null
            }
        }
    }


}