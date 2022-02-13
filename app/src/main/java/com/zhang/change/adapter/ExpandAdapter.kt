package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.databinding.ItemExpendBinding
import com.zhang.change.entitiy.Expend
import com.zhang.change.utils.getNiceStr


/**
 *  选择员工的适配器
 */
class ExpandAdapter(dataList: MutableList<Expend>, val onDel: (Expend) -> Unit) :
    BaseQuickAdapter<Expend, BaseViewHolder>(R.layout.item_expend, dataList) {
    private lateinit var binding:ItemExpendBinding
    override fun convert(helper: BaseViewHolder, item: Expend) {
        binding = ItemExpendBinding.bind(helper.itemView)
        with(binding) {
            tvType.text = item.type.des
            vExpend.text = item.money.getNiceStr()
            vDel.setOnClickListener { onDel(item) }

        }
    }
}