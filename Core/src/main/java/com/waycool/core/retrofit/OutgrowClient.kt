package com.waycool.core.retrofit

import android.os.SystemClock
import android.util.Log
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.waycool.core.BuildConfig
import com.waycool.core.utils.AppSecrets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object OutgrowClient {

    private lateinit var BASE_URL: String
    var retrofit: Retrofit


    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .dispatcher(Dispatcher().apply { maxRequests = 1 })
        .addNetworkInterceptor(StethoInterceptor())
//        .addNetworkInterceptor {
//            val request: Request = it.request()
//
//            // try the request
//            var response = it.proceed(request)
//
//            var tryCount = 0
//            while (!response.isSuccessful && tryCount < 3) {
//
//                Log.d("intercept", "Request is not successful - $tryCount")
//
//                tryCount++
//                SystemClock.sleep((2000 * tryCount).toLong())
//
//                // retry the request
//                response.close()
//                response = it.proceed(request)
//            }
//
//            // otherwise just pass the original response on
//            response
//        }
        .build()
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
//        if (BuildConfig.DEBUG) {
        BASE_URL = AppSecrets.getBASEURLDebug()
//        }

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}