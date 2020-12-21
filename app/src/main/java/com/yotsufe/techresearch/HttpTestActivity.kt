package com.yotsufe.techresearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.databinding.ActivityHttpTestBinding
import com.yotsufe.techresearch.databinding.ActivityMainBinding

class HttpTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHttpTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_http_test)
    }
}