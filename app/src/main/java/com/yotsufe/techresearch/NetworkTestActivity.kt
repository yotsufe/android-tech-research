package com.yotsufe.techresearch

import android.content.Context
import android.net.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.databinding.ActivityNetworkTestBinding

class NetworkTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNetworkTestBinding
    private val connectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_network_test)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network : Network) {
                    Log.e("###", "onAvailable")
                }

                override fun onLost(network : Network) {
                    Log.e("###", "onLost")
                }

                override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
                    Log.e("###", "onCapabilitiesChanged")
                }

                override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
                    Log.e("###", "onLinkPropertiesChanged")
                }
            })
        }
    }

}
