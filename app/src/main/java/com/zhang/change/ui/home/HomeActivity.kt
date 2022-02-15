package com.zhang.change.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.zhang.change.databinding.ActivityHomeBinding
import com.zhang.change.dialog.AddShopNameDialog
import com.zhang.change.rounter.start2Activity
import com.zhang.change.ui.expend_statistic.ExpendStatisticActivity
import com.zhang.change.ui.performance_statistic.PerformanceStatisticActivity
import kotlinx.coroutines.*

class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        checkShopName()
    }

    private fun checkShopName() {
        launch {
            if (viewModel.shopName.isNullOrBlank()) {
                withContext(Dispatchers.Default) {
                    if (viewModel.hasUser()) {
                        viewModel.setShopName("茭白园路店")
                    } else {
                        withContext(Dispatchers.Main) {
                            AddShopNameDialog()
                                .show(supportFragmentManager, onEnsure = { _shopName ->
                                    viewModel.setShopName(_shopName)
                                    title = _shopName
                                }, onCancel = {
                                    finish()
                                })
                        }
                    }
                }
            }
            title = viewModel.shopName
        }
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
