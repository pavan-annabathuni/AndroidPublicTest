package com.example.profile.apiService

import com.example.profile.apiService.profilePic.profile_pic
import com.example.profile.apiService.response.profile
import com.example.profile.apiService.userResponse.Users
import com.example.profile.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

var gson: Gson = GsonBuilder()
    .setLenient()
    .create()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URl)
    .build()

    interface Api {
        //@Multipart
        @Headers(
            "Authorization: Bearer 49|cBe4HrON8HSezTGAWDx4mw9rI5D0MjOwi5RcgOsW")
        @FormUrlEncoded
       // @Multipart
        @POST("api/v1/profiles")
        suspend fun updateProfile(
            @FieldMap map:Map<String,String>,
//            @PartMap map:Map<String,String>,
//            @Part image: MultipartBody.Part

        ): profile
        @Headers(
            "Authorization: Bearer 49|cBe4HrON8HSezTGAWDx4mw9rI5D0MjOwi5RcgOsW")
        @GET("api/v1/users-details")
        suspend fun getProfile():Users?


       @Headers(
        "Authorization: Bearer 49|cBe4HrON8HSezTGAWDx4mw9rI5D0MjOwi5RcgOsW")
       @Multipart
         @POST("api/v1/update-profile-picture")
         suspend fun getProfilePic(
          // @PartMap map: Map<String,MultipartBody>,
           @Part image: MultipartBody.Part
         ):profile_pic
        }


object ProfileApi{
    val retrofitService:Api by lazy {
        retrofit.create(Api::class.java)
    }
}
