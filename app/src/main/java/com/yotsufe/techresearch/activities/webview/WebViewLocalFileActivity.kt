package com.yotsufe.techresearch.activities.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityWebViewTestBinding
import java.lang.Exception

class WebViewLocalFileActivity : AppCompatActivity() {

    companion object {
        private const val BUNDLE_KEY_EMAIL = "bundle_key_email"
        private const val BUNDLE_KEY_MESSAGE = "bundle_key_message"
        private const val ELEMENT_ID_EMAIL = "email_body"
        private const val ELEMENT_ID_MESSAGE = "message_body"
    }

    private lateinit var binding: ActivityWebViewTestBinding
    private var email: String = ""
    private var message: String = ""
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            if (result.data != null) {
                uploadMessage?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
                )
                uploadMessage = null
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_web_view_test
        )
        WebView.setWebContentsDebuggingEnabled(true)

        binding.webView.let {
            it.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.loadUrl( "javascript:setValue('message_body', '$message')")
                    view?.loadUrl( "javascript:setValue('email_body', '$email')")
                }
            }
            it.webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    if (uploadMessage != null) {
                        uploadMessage?.onReceiveValue(null)
                        uploadMessage = null
                    }

                    uploadMessage = filePathCallback

                    try {
                        fileChooserParams?.createIntent().run {
                            startForResult.launch(this)
                        }
                    } catch (e: Exception) {
                        uploadMessage = null
                        return false
                    }
                    return true
                }
            }
            it.settings.javaScriptEnabled = true
            it.addJavascriptInterface(FormJavascriptInterface(), "Android")
            it.loadUrl("file:///android_asset/form.html")
        }
    }

    override fun onPause() {
        super.onPause()

        binding.webView.loadUrl("javascript:getValueById('message_body')")
        binding.webView.loadUrl("javascript:getValueById('email_body')")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(BUNDLE_KEY_MESSAGE, message)
        outState.putString(BUNDLE_KEY_EMAIL, email)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        message = savedInstanceState.getString(BUNDLE_KEY_MESSAGE) ?: ""
        email = savedInstanceState.getString(BUNDLE_KEY_EMAIL) ?: ""
    }

    inner class FormJavascriptInterface {
        @JavascriptInterface
        fun getValue(id: String, value: String) {
            when (id) {
                ELEMENT_ID_MESSAGE -> {
                    message = value.replace("\n", "\\n")
                }
                ELEMENT_ID_EMAIL -> {
                    email = value
                }
            }
        }
    }

}
