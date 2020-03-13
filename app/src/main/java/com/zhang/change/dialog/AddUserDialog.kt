package com.zhang.change.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.zhang.change.R
import com.zhang.change.dao.UserDao
import com.zhang.change.entitiy.User
import kotlinx.android.synthetic.main.layout_add_user.*
import kotlinx.coroutines.*

class AddUserDialog : DialogFragment(), CoroutineScope by MainScope() {
    private var onEnsure: ((User) -> Unit)? = null
    private lateinit var userDao: UserDao
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.layout_add_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        v_ensure.setOnClickListener {
            if (et_no.text.isEmpty()) {
                Toast.makeText(context, "请输入工号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newNo = et_no.text.toString().toInt()
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
        v_cancel.setOnClickListener {
            dismiss()
        }
    }


    fun show(manager: FragmentManager, userDao: UserDao, onEnsure: (User) -> Unit) {
        this.userDao = userDao
        this.onEnsure = onEnsure
        super.show(manager, "AddUserDialog")
    }

}