package com.yotsufe.techresearch.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.ktx.Firebase
import com.yotsufe.techresearch.*
import com.yotsufe.techresearch.activities.recordingvideo.RecordingVideoTestActivity
import com.yotsufe.techresearch.databinding.ActivityMainBinding
import com.yotsufe.techresearch.inappmessaging.CustomMessagingDisplayComponent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

        binding.btnNetworkTest.setOnClickListener {
            startActivity(Intent(this, NetworkTestActivity::class.java))
        }

        Firebase.inAppMessaging.setMessageDisplayComponent(CustomMessagingDisplayComponent(this))
        binding.btnRemoteConfigTest.setOnClickListener {
            startActivity(Intent(this, RemoteConfigTestActivity::class.java))
        }

        binding.btnHttpTest.setOnClickListener {
            startActivity(Intent(this, HttpTestActivity::class.java))
        }

        binding.btnAnimationTest.setOnClickListener {
            startActivity(Intent(this, AnimationTestActivity::class.java))
        }

        binding.btnWebViewTest.setOnClickListener {
            startActivity(Intent(this, WebViewTestActivity::class.java))
        }

        binding.btnRecordingTest.setOnClickListener {
            startActivity(Intent(this, RecordingTestActivity::class.java))
        }

        binding.btnRecordingVideoTest.setOnClickListener {
            startActivity(Intent(this, RecordingVideoTestActivity::class.java))
        }

        binding.btnMediaPlayerTest.setOnClickListener {
            startActivity(Intent(this, MediaPlayerTestActivity::class.java))
        }


    }

}
