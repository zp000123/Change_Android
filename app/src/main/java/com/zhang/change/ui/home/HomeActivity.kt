package com.zhang.change.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.zhang.change.MyApplication
import com.zhang.change.R
import com.zhang.change.dao.UserDao
import com.zhang.change.dialog.AddShopNameDialog
import com.zhang.change.rounter.start2Activity
import com.zhang.change.ui.expend_statistic.ExpendStatisticActivity
import com.zhang.change.ui.performance_statistic.PerformanceStatisticActivity
import com.zhang.change.utils.SharePreferencesUtils
import com.zhang.change.utils.SharePreferencesUtils.SHOP_NAME
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import org.jetbrains.anko.alert

class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initDao()
        initView()
        checkShopName()
    }

    private fun checkShopName() {
        val myApplication = application as MyApplication
        launch {
            if (myApplication.shopName.isEmpty()) {
                withContext(Dispatchers.Default) {
                    if (userDao.queryAllUser().isNotEmpty()) {
                        myApplication.shopName = "茭白园路店"
                    } else {
                        withContext(Dispatchers.Main) {
                            AddShopNameDialog()
                                .show(supportFragmentManager, onEnsure = { _shopName ->
                                    myApplication.shopName = _shopName
                                    title = _shopName
                                }, onCancel = {
                                    finish()
                                })
                        }
                    }
                }
                title = myApplication.shopName
            } else {
                title = myApplication.shopName
            }
        }
    }

    private fun initDao() {
        val db = (application as MyApplication).db
        userDao = db.userDao()
    }

    private fun initView() {
        v_performance.setOnClickListener {
            start2Activity(PerformanceStatisticActivity::class.java)
        }
        v_income_expend.setOnClickListener {
            start2Activity(ExpendStatisticActivity::class.java)
        }
    }


}
