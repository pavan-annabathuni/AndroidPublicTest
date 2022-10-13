package com.waycool.featurecrophealth.api


import com.waycool.featurecrophealth.model.apicroppost.AiCropPostResponse
import com.waycool.featurecrophealth.model.cropcate.CropCategory
import com.waycool.featurecrophealth.model.cropdetails.CropDetails
import com.waycool.featurecrophealth.model.historydata.HistoryResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    //https://adminuat.outgrowdigital.com/api/v1/ai-crop-health/history?user_id=1
//    @Headers("Authorization:Bearer 16|QsTi3RVI8HdjhYmA9ITnTz8QK8hU1v61ALuybJdK")
    @GET("api/v1/ai-crop-health/history")
    suspend fun getAllHistory(
        @HeaderMap headerMap: Map<String, String>,
        @Query("user_id") user_id: Int
    ): Response<HistoryResponse>

    //https://adminuat.outgrowdigital.com/api/v1/crop-category-master
    @GET("api/v1/crop-category-master")
    suspend fun getCropCategory(
        @HeaderMap headerMap: Map<String, String>
    ): Response<CropCategory>


    @GET("api/v1/crop-master")
    suspend fun getCropDetails(
        @HeaderMap headerMap: Map<String, String>
    ): Response<CropDetails>

    //https://adminuat.outgrowdigital.com/api/v1/ai-crop-health
//    @Headers("Authorization:Bearer 1|aSu4q2WVDEJCFXXmvm60BoS0uNQDLKUslM1IkhDC")

    @POST("api/v1/ai-crop-health")
    @Multipart
    suspend fun postAiCrop(
        @HeaderMap headerMap: Map<String, String>,
        @Part("user_id") user_id: Int,
        @Part("crop_id") crop_id: Int,
        @Part("crop_name") crop_name: String,
        @Part file: MultipartBody.Part
    ): Response<AiCropPostResponse>


}