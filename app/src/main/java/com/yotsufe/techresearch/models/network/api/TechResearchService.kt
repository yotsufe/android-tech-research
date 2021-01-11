package com.yotsufe.techresearch.models.network.api

import com.yotsufe.techresearch.models.entities.User
import retrofit2.Call
import retrofit2.http.GET

interface TechResearchService {

    @GET("user")
    fun getUser(): Call<User>

}
