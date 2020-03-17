package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.ExpendType
import com.zhang.change.utils.getNiceStr
import com.zhang.change.utils.refreshTextStyle
import kotlinx.android.synthetic.main.item_expend_date.view.*
import org.jetbrains.anko.textColor

/**
 *  选择员工的适配器
 */
@Suppress("DEPRECATION")
class ExpendDateAdapter(dataList: MutableList<ExpendDate>) :
    BaseQuickAdapter<ExpendDate, BaseViewHolder>(R.layout.item_expend_date, dataList) {

    override fun convert(helper: BaseViewHolder, item: ExpendDate) {
        with(helper.itemView) {
            tv_date.text = item.dateStr
            tv_income.text = item.income.getNiceStr()

            tv_recent_money.text = item.recentMoney.getNiceStr()

            tv_living_cost.text = ""
            tv_water_cost.text = ""
            tv_other.text = ""
            tv_salary.text = ""
            tv_draw.text = ""
            tv_group_purchase.text = ""
            tv_receive_money.text = ""
            tv_kou_bei.text = ""
            tv_pos.text = ""
            for (expend in item.expandList) {
                when (expend.type) {
                    ExpendType.LIVING_COST -> {
                        tv_living_cost.text = expend.money.getNiceStr()
                    }
                    ExpendType.WATER_COST -> {
                        tv_water_cost.text = expend.money.getNiceStr()
                    }
                    ExpendType.OTHER -> {
                        tv_other.text = expend.money.getNiceStr()
                    }
                    ExpendType.SALARY -> {
                        tv_salary.text = expend.money.getNiceStr()
                    }
                    ExpendType.DRAW -> {
                        tv_draw.text = expend.money.getNiceStr()
                    }
                    ExpendType.GROUP_PURCHASE -> {
                        tv_group_purchase.text = expend.money.getNiceStr()
                    }
                    ExpendType.RECEIVE_MONEY -> {
                        tv_receive_money.text = expend.money.getNiceStr()
                    }
                    ExpendType.KOU_BEI -> {
                        tv_kou_bei.text = expend.money.getNiceStr()
                    }
                    ExpendType.POS -> {
                        tv_pos.text = expend.money.getNiceStr()
                    }
                }
            }

            tv_recent_money.textColor =
                if (item.recentMoney >= 0) context.resources.getColor(R.color.black)
                else context.resources.getColor(R.color.red)

            tv_income.refreshTextStyle(helper.adapterPosition)
            tv_living_cost.refreshTextStyle(helper.adapterPosition)
            tv_water_cost.refreshTextStyle(helper.adapterPosition)
            tv_other.refreshTextStyle(helper.adapterPosition)
            tv_salary.refreshTextStyle(helper.adapterPosition)
            tv_draw.refreshTextStyle(helper.adapterPosition)
            tv_group_purchase.refreshTextStyle(helper.adapterPosition)
            tv_receive_money.refreshTextStyle(helper.adapterPosition)
            tv_kou_bei.refreshTextStyle(helper.adapterPosition)
            tv_pos.refreshTextStyle(helper.adapterPosition)
        }


        addChildClickViewIds(R.id.v_edit)
    }


}


data class ExpendDate(val dateStr: String) {
    var income: Int = 0
    var expandList: List<Expend> = arrayListOf()
    val recentMoney get() = income - expandList.sumBy { it.money }
}