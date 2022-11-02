package com.example.mandiprice



import com.example.mandiprice.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//@InstallIn(SingletonComponent::class)
//@Module
class NetworkBuilder {
//    @Singleton
//    @Provides
//    fun getRetrofit(): Retrofit {
//        return Retrofit.Builder().baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create()).build()
//    }
//
//    @Singleton
//    @Provides
//    fun getQuoteAPI(retrofit: Retrofit): ServiceRetrofit{
//        return retrofit.create(ServiceRetrofit::class.java)
//    }


    companion object {
        private val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        fun getInstance(): Retrofit {
            return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URl)
                .client(OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build())
                .build()
        }


    }
}