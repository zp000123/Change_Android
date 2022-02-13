package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.databinding.ItemExpendDateBinding
import com.zhang.change.entitiy.Expend
import com.zhang.change.entitiy.ExpendType
import com.zhang.change.utils.getNiceStr
import com.zhang.change.utils.refreshTextStyle

/**
 *  选择员工的适配器
 */
@Suppress("DEPRECATION")
class ExpendDateAdapter(dataList: MutableList<ExpendDate>) :
    BaseQuickAdapter<ExpendDate, BaseViewHolder>(R.layout.item_expend_date, dataList) {
    private lateinit var binding :ItemExpendDateBinding
    override fun convert(helper: BaseViewHolder, item: ExpendDate) {
        binding = ItemExpendDateBinding.bind(helper.itemView)
        with(binding) {

            tvDate.text = item.dateStr
            tvIncome.text = item.income.getNiceStr()

            tvReceiveMoney.text = item.recentMoney.getNiceStr()

            tvLivingCost.text = ""
            tvWaterCost.text = ""
            tvOther.text = ""
            tvSalary.text = ""
            tvDraw.text = ""
            tvGroupPurchase.text = ""
            tvReceiveMoney.text = ""
            tvKouBei.text = ""
            tvPos.text = ""
            for (expend in item.expandList) {
                when (expend.type) {
                    ExpendType.LIVING_COST -> {
                        tvLivingCost.text = expend.money.getNiceStr()
                    }
                    ExpendType.WATER_COST -> {
                        tvWaterCost.text = expend.money.getNiceStr()
                    }
                    ExpendType.OTHER -> {
                        tvOther.text = expend.money.getNiceStr()
                    }
                    ExpendType.SALARY -> {
                        tvSalary.text = expend.money.getNiceStr()
                    }
                    ExpendType.DRAW -> {
                        tvDraw.text = expend.money.getNiceStr()
                    }
                    ExpendType.GROUP_PURCHASE -> {
                        tvGroupPurchase.text = expend.money.getNiceStr()
                    }
                    ExpendType.RECEIVE_MONEY -> {
                        tvReceiveMoney.text = expend.money.getNiceStr()
                    }
                    ExpendType.KOU_BEI -> {
                        tvKouBei.text = expend.money.getNiceStr()
                    }
                    ExpendType.POS -> {
                        tvPos.text = expend.money.getNiceStr()
                    }
                }
            }

            tvRecentMoney.setTextColor(
                if (item.recentMoney >= 0) context.resources.getColor(R.color.black)
                else context.resources.getColor(R.color.red))

            tvIncome.refreshTextStyle(helper.adapterPosition)
            tvLivingCost.refreshTextStyle(helper.adapterPosition)
            tvWaterCost.refreshTextStyle(helper.adapterPosition)
            tvOther.refreshTextStyle(helper.adapterPosition)
            tvSalary.refreshTextStyle(helper.adapterPosition)
            tvDraw.refreshTextStyle(helper.adapterPosition)
            tvGroupPurchase.refreshTextStyle(helper.adapterPosition)
            tvReceiveMoney.refreshTextStyle(helper.adapterPosition)
            tvKouBei.refreshTextStyle(helper.adapterPosition)
            tvPos.refreshTextStyle(helper.adapterPosition)
        }


        addChildClickViewIds(R.id.v_edit)
    }


}


data class ExpendDate(val dateStr: String) {
    var income: Int = 0
    var expandList: List<Expend> = arrayListOf()
    val recentMoney get() = income - expandList.sumBy { it.money }
}