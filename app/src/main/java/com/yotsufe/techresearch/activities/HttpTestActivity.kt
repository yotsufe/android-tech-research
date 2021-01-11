package com.yotsufe.techresearch.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityHttpTestBinding
import com.yotsufe.techresearch.models.network.api.TechResearchService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class HttpTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHttpTestBinding
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_http_test)

        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.4:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        binding.getUser.setOnClickListener {
            getUser()
        }
    }

    private fun getUser() {
        val handler = Handler()

        thread {
            try {
                val service: TechResearchService = retrofit.create(TechResearchService::class.java)
                val user = service.getUser().execute().body()

                handler.post {
                    binding.id.text = user?.id.toString()
                    binding.name.text = user?.name
                    binding.password.text = user?.password
                }

            } catch (e: Exception) {
                Log.d("###", e.message!!)
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
