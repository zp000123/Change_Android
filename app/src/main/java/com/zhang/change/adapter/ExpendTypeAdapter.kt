package com.zhang.change.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.entitiy.ExpendTypeW
import kotlinx.android.synthetic.main.item_expend_type.view.*


/**
 *  选择员工的适配器
 */
class ExpendTypeAdapter(dataList: MutableList<ExpendTypeW>,val onSelect: (user: ExpendTypeW) -> Unit) :
    BaseQuickAdapter<ExpendTypeW, BaseViewHolder>(R.layout.item_expend_type, dataList) {
    override fun convert(helper: BaseViewHolder, item: ExpendTypeW) {
        with(helper.itemView) {
            v_expend.text = item.expendType.des
            v_expend.isChecked = item.u_selected
            v_expend.setOnClickListener {
                item.u_selected = true
                onSelect(item)
                data.filter { it != item }.forEach { it.u_selected = false }
                notifyDataSetChanged()
            }
        }
    }
}
