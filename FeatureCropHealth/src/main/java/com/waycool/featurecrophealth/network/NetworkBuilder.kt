package com.waycool.featurecrophealth.network

import com.waycool.featurecrophealth.utils.Constant.BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NetworkBuilder {
    companion object {
        private val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        var gson = GsonBuilder()
            .setLenient()
            .create()

        fun getInstance(): Retrofit {
            return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }


    }

}