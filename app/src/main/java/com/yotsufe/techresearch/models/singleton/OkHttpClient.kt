package com.yotsufe.techresearch.models.singleton

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpClientFactory {

    private const val CONNECTION_TIME_OUT = 15
    private const val READ_TIME_OUT = 15

    fun newInstance(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request()
                chain.proceed(request)
            }
            .connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

}
