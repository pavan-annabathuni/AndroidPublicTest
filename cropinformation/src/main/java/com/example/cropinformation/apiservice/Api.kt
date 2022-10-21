package com.example.cropinformation.apiservice

import com.example.cropinformation.apiservice.response.Data
import com.example.cropinformation.apiservice.response.DataX
import com.example.cropinformation.apiservice.response.VideoResponse
import com.example.cropinformation.apiservice.response.cropAdvisory.CropInformation
import com.example.cropinformation.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URl)
    .build()

interface Api {
    @Headers("Authorization:Bearer 49|cBe4HrON8HSezTGAWDx4mw9rI5D0MjOwi5RcgOsW")
    @GET("api/v1/vans-feeder")
    suspend fun getVideo(
        @Query("vans_type") vans_type: String,
        @Query("tags") tags: String
    ):VideoResponse


    @Headers("Authorization:Bearer 49|cBe4HrON8HSezTGAWDx4mw9rI5D0MjOwi5RcgOsW")
    @GET("api/v1/crop-advisory")
    suspend fun getInfromation(
    ):CropInformation
}
    object videoApi {
        val retrofitService: Api by lazy {
            retrofit.create(Api::class.java)
        }
}