package com.zhang.change.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhang.change.R
import com.zhang.change.databinding.ItemUserBinding
import com.zhang.change.entitiy.User

/**
 *  选择员工的适配器
 */
class UserListAdapter(dataList: MutableList<User>, val onSelect: (user: User) -> Unit) :
    BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_user, dataList) {
    private lateinit var binding:ItemUserBinding

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: User) {
        binding = ItemUserBinding.bind(helper.itemView)
        with(binding) {
            vNo.text = "#${item.no}"
            vNo.isChecked = item.u_selected
            vNo.setOnClickListener {
                item.u_selected = true
                onSelect(item)
                data.filter { it != item }.forEach { it.u_selected = false }
                notifyDataSetChanged()
            }
        }
    }

}