package com.example.mandiprice.api

import com.example.mandiprice.api.mandiResponse.Mandi
import com.example.mandiprice.api.stateRespond.IndianStates
import com.example.mandiprice.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

var gson: Gson = GsonBuilder()
    .setLenient()
    .create()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URl)
    .build()
interface apiService {
    @Headers(
        "Authorization: Bearer 60|HPoO8s9qjT4Iwii5gseBz2bA5f22rPEamcXnv3Un")
    @GET("get-mandi")
      suspend fun getList(
        @Query("lat") lat:String,
        @Query("long") long:String,
        @Query("crop_category") category:String?,
        @Query("state") state:String?,
        @Query("crop") crop:String?,
        @Query("page") page: Int,
        @Query("sort_by") sort_by:String,
        @Query("order_by") orderBy:String?
      ):Mandi

    @Headers(
        "Authorization: Bearer 60|HPoO8s9qjT4Iwii5gseBz2bA5f22rPEamcXnv3Un")
    @GET("get-mandi")
    suspend fun randomList(
        @Query("lat")lat:String,
        @Query("long")long:String,
        @Query("page")page:Int,
        @Query("sort_by")value:String,
        @Query("order_by")sort:String?
    ):Mandi

    @Headers(
        "Authorization: Bearer 60|HPoO8s9qjT4Iwii5gseBz2bA5f22rPEamcXnv3Un")
    @GET("get-mandi")
    suspend fun searchList(
        @Query("lat")lat:String,
        @Query("long")long:String,
        @Query("search")search:String?,
        @Query("sort_by")value:String,
        @Query("order_by")sort:String?
    ):Mandi

    @Headers(
        "Authorization: Bearer 60|HPoO8s9qjT4Iwii5gseBz2bA5f22rPEamcXnv3Un","Accept: application/json")
    @GET("india-state-master")
    suspend fun stateList(
    ):IndianStates
}

object MandiApi{
    val retrofitService: apiService by lazy {
        retrofit.create(apiService::class.java)
    }
}
