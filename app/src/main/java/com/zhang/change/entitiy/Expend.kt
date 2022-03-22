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
    var type: ExpendType,
    var money: Int,//分
    var dateStamp: Long// 毫秒

) {
    @PrimaryKey(autoGenerate = true)
    var eId: Int = 0
}

/**
 * 支出类型
 */
enum class ExpendType(val type: Int, val des: String) {
    LIVING_COST(1, "生活开支"),
    WATER_COST(2, "水电燃气"),
    OTHER(3, "其他"),
    SALARY(4, "工资"),
    DRAW(5, "支取"),
    GROUP_PURCHASE(6, "团购"),
    RECEIVE_MONEY(7, "收钱吧"),
    KOU_BEI(8, "口碑"),
    POS(9, "pos机");


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

        fun getExpendTypeList(): List<ExpendType> {
            return arrayListOf<ExpendType>().apply {
                add(LIVING_COST)
                add(WATER_COST)
                add(OTHER)
                add(SALARY)
                add(DRAW)
                add(GROUP_PURCHASE)
                add(RECEIVE_MONEY)
                add(KOU_BEI)
                add(POS)
            }
        }
    }
}

class ExpendTypeW(val expendType: ExpendType, var u_selected: Boolean = false) {
    companion object {
        fun getExpendTypeWList(): List<ExpendTypeW> {
            return arrayListOf<ExpendTypeW>().apply {
                add(ExpendTypeW(ExpendType.LIVING_COST, true))
                add(ExpendTypeW(ExpendType.WATER_COST))
                add(ExpendTypeW(ExpendType.OTHER))
                add(ExpendTypeW(ExpendType.SALARY))
                add(ExpendTypeW(ExpendType.DRAW))
                add(ExpendTypeW(ExpendType.GROUP_PURCHASE))
                add(ExpendTypeW(ExpendType.RECEIVE_MONEY))
                add(ExpendTypeW(ExpendType.KOU_BEI))
                add(ExpendTypeW(ExpendType.POS))
            }
        }
    }
}