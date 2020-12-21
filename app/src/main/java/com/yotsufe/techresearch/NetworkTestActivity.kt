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
    private var isOnline = false
    private val connectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_network_test)

        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        Log.d("###", "")
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network : Network) {
                    Log.e("###", "onAvailable")
                    Log.e("###", "The default network is now: " + network)
                }

                override fun onLost(network : Network) {
                    Log.e("###", "onLost")
                    Log.e("###", "The application no longer has a default network. The last default network was " + network)
                }

                override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
                    Log.e("###", "onCapabilitiesChanged")
                    Log.e("###", "The default network changed capabilities: " + networkCapabilities)
                }

                override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
                    Log.e("###", "onLinkPropertiesChanged")
                    Log.e("###", "The default network changed link properties: " + linkProperties)
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
//        connectivityManager.unregisterNetworkCallback()
    }
}
