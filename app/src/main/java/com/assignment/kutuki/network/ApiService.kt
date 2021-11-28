package com.assignment.kutuki.network

import com.assignment.kutuki.constants.Constants
import com.assignment.kutuki.pojo.GetVideoList
import com.assignment.kutuki.pojo.GetCategoryList
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    companion object{


        private val retrofitService by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

        fun getInstance():ApiService= retrofitService
    }
    @Headers("Content-Type: application/json")
    @GET("/v2/5e2beb5a3100006600267e4e")
    suspend fun getVideosList():Response<GetVideoList>

    @Headers("Content-Type: application/json")
    @GET("/v2/5e2bebd23100007a00267e51")
    suspend fun getCategoryList():Response<GetCategoryList>








}