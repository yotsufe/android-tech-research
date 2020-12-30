package com.yotsufe.techresearch.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.ktx.Firebase
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityRemoteConfigTestBinding

class RemoteConfigTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemoteConfigTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_remote_config_test
        )

        Firebase.inAppMessaging.addClickListener { _, _ -> Log.d("####", "messageClicked") }

        binding.btnEvent1.setOnClickListener {
            FirebaseAnalytics.getInstance(applicationContext)
                .logEvent("event_1", null)
        }

        binding.btnEvent2.setOnClickListener {
            FirebaseAnalytics.getInstance(applicationContext)
                .logEvent("event_2", null)
        }

        binding.btnEvent3.setOnClickListener {
            FirebaseAnalytics.getInstance(applicationContext)
                .logEvent("event_3", null)
        }
    }
}
