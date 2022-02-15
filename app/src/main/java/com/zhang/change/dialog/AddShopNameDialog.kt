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
import com.zhang.change.databinding.LayoutAddShopNameBinding
import com.zhang.change.utils.SharePreferencesUtils
import com.zhang.change.utils.SharePreferencesUtils.SHOP_NAME
import com.zhang.change.utils.showKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddShopNameDialog : DialogFragment(), CoroutineScope by MainScope() {
    private var onEnsure: ((String) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null
    private val handler = Handler()
    private lateinit var mContext: Context
    private lateinit var binding:LayoutAddShopNameBinding
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAddShopNameBinding.inflate(LayoutInflater.from(context),container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vEnsure.setOnClickListener {
            if (binding.etShop.text.isEmpty()) {
                Toast.makeText(context, R.string.add_shop_hint, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shopName =binding. etShop.text.toString()
            launch {
                SharePreferencesUtils.getCommonSpf(mContext)
                    .edit {
                        this.putString(SHOP_NAME, shopName)
                    }

                onEnsure?.let { it1 -> it1(shopName) }
                dismiss()
            }
        }

        binding.vCancel.setOnClickListener {
            onCancel?.let { it1 -> it1() }
            dismiss()
        }

        handler.postDelayed({
            binding.etShop.showKeyboard()
        }, 200)
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }


    fun show(manager: FragmentManager, onEnsure: (String) -> Unit, onCancel: () -> Unit) {
        this.onEnsure = onEnsure
        this.onCancel = onCancel
        super.show(manager, "AddShopNameDialog")
    }

}