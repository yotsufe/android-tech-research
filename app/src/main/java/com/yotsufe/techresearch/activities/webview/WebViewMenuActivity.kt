package com.yotsufe.techresearch.activities.webview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.ktx.Firebase
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.activities.NetworkTestActivity
import com.yotsufe.techresearch.activities.RemoteConfigTestActivity
import com.yotsufe.techresearch.databinding.ActivityWebViewMenuBinding
import com.yotsufe.techresearch.inappmessaging.CustomMessagingDisplayComponent

class WebViewMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view_menu)

        binding.btnWebViewNet.setOnClickListener {
            startActivity(Intent(this, WebViewTestActivity::class.java))
        }

        binding.btnWebViewLocal.setOnClickListener {
            startActivity(Intent(this, WebViewLocalFileActivity::class.java))
        }

    }
}
