package com.zhang.change.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhang.change.R
import com.zhang.change.rounter.start2Activity
import com.zhang.change.ui.expend_statistic.ExpendStatisticActivity
import com.zhang.change.ui.performance_statistic.PerformanceStatisticActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        v_performance.setOnClickListener {
            start2Activity(PerformanceStatisticActivity::class.java)
        }
        v_income_expend.setOnClickListener {
            start2Activity(ExpendStatisticActivity::class.java)
        }
    }
}
