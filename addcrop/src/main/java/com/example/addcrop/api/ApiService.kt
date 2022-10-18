package com.example.addcrop.api

import com.example.addcrop.model.AddCropResponse
import com.example.addcrop.model.addcroppost.AddCropRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/v1/soil-types")
    suspend fun getAllHistory(
        @HeaderMap headerMap: Map<String, String>
    ): Response<AddCropResponse>

    @POST("/users/signup")
    suspend fun addCropPassData(@Body addCropPost: AddCropRequest) : Response<AddCropRequest>
    //type
    //https://adminuat.outgrowdigital.com/api/v1/soil-types
}