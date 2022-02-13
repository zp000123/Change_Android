package com.zhang.change.dialog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.zhang.change.dao.UserDao
import com.zhang.change.databinding.LayoutAddUserBinding
import com.zhang.change.entitiy.User
import com.zhang.change.utils.showKeyboard
import kotlinx.coroutines.*

class AddUserDialog : DialogFragment(), CoroutineScope by MainScope() {
    private var onEnsure: ((User) -> Unit)? = null
    private lateinit var userDao: UserDao
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding :LayoutAddUserBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAddUserBinding.inflate(LayoutInflater.from(context),container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding. vEnsure.setOnClickListener {
            if (binding.etNo.text.isEmpty()) {
                Toast.makeText(context, "请输入工号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newNo =binding. etNo.text.toString().toInt()
            launch {

                val dbUser = async { userDao.queryUserByNo(newNo) }.await()
                if (dbUser != null) {
                    Toast.makeText(context, "已有改工号，无需添加", Toast.LENGTH_SHORT).show()
                    return@launch
                } else {
                    withContext(Dispatchers.Default) {
                        userDao.insert(User(no = newNo))
                    }
                    val selectUser = async { userDao.queryUserByNo(newNo) }.await()
                    onEnsure?.let { it1 -> it1(selectUser!!) }
                    dismiss()
                }
            }
        }
        binding.vCancel.setOnClickListener {
            dismiss()
        }

        handler.postDelayed({
            binding.etNo.showKeyboard()
        },200)
    }


    fun show(manager: FragmentManager, userDao: UserDao, onEnsure: (User) -> Unit) {
        this.userDao = userDao
        this.onEnsure = onEnsure
        super.show(manager, "AddUserDialog")
    }

}