package com.waycool.data.Network.ApiInterface

import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkModels.LanguageMasterDTO
import com.waycool.data.Network.NetworkModels.TagsAndKeywordsDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiInterface {
    @GET("api/v1/language-master")
    suspend fun getLanguageMaster(
        @HeaderMap map: Map<String, String>
    ): Response<LanguageMasterDTO>

    @POST("api/v1/login")
    @FormUrlEncoded
    suspend fun login(
        @HeaderMap map: Map<String, String>,
        @Field("contact") contact: String,
        @Field("password") password: String,
        @Field("fcm_token") fcm_token: String,
        @Field("mobile_model") mobile_model: String,
        @Field("mobile_manufacturer") mobile_manufacturer: String,
    ): Response<LoginDTO>


    @POST("api/v1/logout")
    @FormUrlEncoded
    suspend fun logout(
        @HeaderMap map: Map<String, String>,
        @Field("contact") contact: String
    ): Response<LogoutDTO>


    @GET("api/v1/module-masters")
    suspend fun getModuleMaster(
        @HeaderMap map: Map<String, String>
    ): Response<ModuleMasterDTO>


    @POST("api/v1/register")
    @FormUrlEncoded
    suspend fun register(
        @HeaderMap map: Map<String, String>,
        @FieldMap data: Map<String, String>
    ): Response<RegisterDTO>

    @GET("api/v1/crop-master")
    suspend fun getCropMaster(
        @HeaderMap map: Map<String, String>
    ): Response<CropMasterDTO>

    @GET("api/v1/crop-category-master")
    suspend fun getCropCategoryMaster(
        @HeaderMap map: Map<String, String>
    ): Response<CropCategoryMasterDTO>

    @GET("api/v1/pest-disease-master")
    suspend fun getPestDisease(
        @HeaderMap map: Map<String, String>,
        @Query("crop_id") id: Int? = null
    ): Response<PestDiseaseDTO>

    //videos category
    @GET("api/v1/vans-category-master")
    suspend fun getVansCategory(@HeaderMap map: Map<String, String>): Response<VansCategoryDTO?>

    //news list
    @GET("api/v1/vans-feeder")
    suspend fun getVansFeeder(
        @HeaderMap map: Map<String, String>,
        @QueryMap map1: Map<String, String>
    ): Response<VansFeederDTO?>

    //news list
    @GET("api/v1/tags-keywords")
    suspend fun getTagsAndKeywords(
        @HeaderMap map: Map<String, String>
    ): Call<TagsAndKeywordsDTO>


    //news list
    @POST("api/v1/feedback-recorder")
    @FormUrlEncoded
    fun setFeedback(
        @HeaderMap map: Map<String, String>,
        @FieldMap map1: Map<String, String>
    ): Response<FeedbackMaster?>


    @GET("api/v1/users-details")
    suspend fun getUserDetails(
        @HeaderMap map: Map<String, String>,
    ): Response<UserDetailsDTO>

    @GET("api/v1/ai-crop-health/history")
    suspend fun getAiCropHistory(
        @HeaderMap headerMap: Map<String, String>
    ): Response<AiCropHistoryDTO>

    @POST("api/v1/ai-crop-health")
    @Multipart
    suspend fun postAiCrop(
        @HeaderMap headerMap: Map<String, String>,
        @Part("user_id") user_id: Int,
        @Part("crop_id") crop_id: Int,
        @Part("crop_name") crop_name: String,
        @Part file: MultipartBody.Part
    ): Response<AiCropDetectionDTO>

    @GET("api/v1/crop-advisory")
    suspend fun getCropInformation(
        @HeaderMap headerMap: Map<String, String>,
    ):Response<CropInfo>

    @PUT("api/v1/profiles")
    @FormUrlEncoded
    suspend fun updateProfile(
        @HeaderMap map: Map<String, String>,
        @Field("name")name:String,
        @Field("address") address:String,
        @Field("village") village:String,
        @Field("pincode") pincode:String,
        @Field("state") state:String,
        @Field("district")district:String
    ):Response<profile>

    @Multipart
    @POST("api/v1/update-profile-picture")
    suspend fun getProfilePic(
        @HeaderMap headerMap: Map<String, String>,
        @Part file:MultipartBody.Part
    ):Response<profilePicModel>
}
