package com.waycool.core.retrofit

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.waycool.core.utils.AppSecrets
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapsClient {

    private val BASE_URL by lazy { AppSecrets.getGeoBaseUrl() }

    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build()


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val apiClient: Retrofit
        get() {
            return retrofit
        }
}