package com.zhang.change.dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.zhang.change.R
import com.zhang.change.utils.SharePreferencesUtils
import com.zhang.change.utils.SharePreferencesUtils.SHOP_NAME
import com.zhang.change.utils.showKeyboard
import kotlinx.android.synthetic.main.layout_add_shop_name.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddShopNameDialog : DialogFragment(), CoroutineScope by MainScope() {
    private var onEnsure: ((String) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null
    private val handler = Handler()
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.layout_add_shop_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        v_ensure.setOnClickListener {
            if (et_shop.text.isEmpty()) {
                Toast.makeText(context, R.string.add_shop_hint, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shopName = et_shop.text.toString()
            launch {

                SharePreferencesUtils.getCommonSpf(mContext)
                    .edit {
                        this.putString(SHOP_NAME, shopName)
                    }

                onEnsure?.let { it1 -> it1(shopName) }
                dismiss()
            }
        }

        v_cancel.setOnClickListener {
            onCancel?.let { it1 -> it1() }
            dismiss()
        }

        handler.postDelayed({
            et_shop.showKeyboard()
        }, 200)
    }


    fun show(manager: FragmentManager, onEnsure: (String) -> Unit, onCancel: () -> Unit) {
        this.onEnsure = onEnsure
        this.onCancel = onCancel
        super.show(manager, "AddShopNameDialog")
    }

}