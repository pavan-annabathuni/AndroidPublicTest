package com.example.soiltesting.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder {
    companion object {
        private val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        var gson = GsonBuilder()
            .setLenient()
            .create()

        fun getInstance(): Retrofit {
            return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://adminuat.outgrowdigital.com/")
                .client(OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build())

                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }


    }

//    fun provideApiService(): ApiService {
//        val builder = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(
//                OkHttpClient.Builder()
//                    .addInterceptor(
//                        HttpLoggingInterceptor()
//                    ).apply { HttpLoggingInterceptor.Level.BODY }
//                    .connectTimeout(TIMEOUT_TIME, TimeUnit.SECONDS)
//                    .build()
//            )
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        return builder.create(ApiService::class.java)
//
//    }

}