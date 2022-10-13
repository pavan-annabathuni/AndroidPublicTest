package com.waycool.core.retrofit

import retrofit2.Retrofit
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.waycool.core.retrofit.OTPApiCient
import com.waycool.core.utils.AppSecrets
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

object OTPApiCient {
    private val BASE_URL by lazy { AppSecrets.getOTPBaseURL() }

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