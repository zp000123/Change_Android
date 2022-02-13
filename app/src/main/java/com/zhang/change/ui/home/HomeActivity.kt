package com.zhang.change.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhang.change.MyApplication
import com.zhang.change.dao.UserDao
import com.zhang.change.databinding.ActivityHomeBinding
import com.zhang.change.dialog.AddShopNameDialog
import com.zhang.change.rounter.start2Activity
import com.zhang.change.ui.expend_statistic.ExpendStatisticActivity
import com.zhang.change.ui.performance_statistic.PerformanceStatisticActivity
import kotlinx.coroutines.*

class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        with(binding) {
            vPerformance.setOnClickListener {
                start2Activity(PerformanceStatisticActivity::class.java)
            }
            vIncomeExpend.setOnClickListener {
                start2Activity(ExpendStatisticActivity::class.java)
            }
        }

    }


}
