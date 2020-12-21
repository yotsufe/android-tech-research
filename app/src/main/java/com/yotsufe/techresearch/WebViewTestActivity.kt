package com.yotsufe.techresearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.databinding.ActivityHttpTestBinding
import com.yotsufe.techresearch.databinding.ActivityMainBinding
import com.yotsufe.techresearch.databinding.ActivityWebViewTestBinding

class WebViewTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view_test)
        WebView.setWebContentsDebuggingEnabled(true)

        binding.wevViewTest.let {
            it.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }
            }
            it.loadUrl("https://blog.codecamp.jp")
        }

    }
}