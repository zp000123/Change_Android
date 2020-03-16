package com.zhang.change.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.ExpendType
import com.zhang.change.utils.getNiceStr
import kotlinx.android.synthetic.main.item_expend_date.view.*
import org.jetbrains.anko.textColor
import java.text.FieldPosition

/**
 *  选择员工的适配器
 */
class ExpendDateAdapter(dataList: MutableList<ExpendDate>) :
    BaseQuickAdapter<ExpendDate, BaseViewHolder>(R.layout.item_expend_date, dataList) {

    override fun convert(helper: BaseViewHolder, item: ExpendDate) {
        with(helper.itemView) {
            tv_date.text = item.dateStr
            tv_income.text = item.income.getNiceStr()

            tv_recent_money.text = item.recentMoney.getNiceStr()

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

            tv_income.refreshTextColor(helper.adapterPosition)
            tv_living_cost.refreshTextColor(helper.adapterPosition)
            tv_water_cost.refreshTextColor(helper.adapterPosition)
            tv_other.refreshTextColor(helper.adapterPosition)
            tv_salary.refreshTextColor(helper.adapterPosition)
            tv_draw.refreshTextColor(helper.adapterPosition)
            tv_group_purchase.refreshTextColor(helper.adapterPosition)
            tv_receive_money.refreshTextColor(helper.adapterPosition)
            tv_kou_bei.refreshTextColor(helper.adapterPosition)
            tv_pos.refreshTextColor(helper.adapterPosition)
        }


        addChildClickViewIds(R.id.v_edit)
    }


    private fun TextView.refreshTextColor(position: Int) {
        this.textColor = if (position % 2 == 0) context.resources.getColor(R.color.colorAccent)
        else context.resources.getColor(R.color.c_6)

    }
}


data class ExpendDate(val dateStr: String) {
    var income: Int = 0
    var expandList: List<Expend> = emptyList()
    val recentMoney get() = income - expandList.sumBy { it.money }
}