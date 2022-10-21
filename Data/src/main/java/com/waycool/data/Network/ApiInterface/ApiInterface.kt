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
import java.text.DateFormat
import java.util.*

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

    //vans Feeder

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
    //add crop get api

    @GET("api/v1/soil-types")
    suspend fun getAddCropType(
        @HeaderMap headerMap: Map<String, String>
    ): Response<AddCropTypeDTO>


    //soil testing
    @GET("api/v1/soil-test-request/history")
    suspend fun getSoilTestAllHistory(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_id") user_id: Int
    ): Response<SoilTestHistoryDTO>


    //SoilTest check

    @GET("api/v1/check-soil-test")
    suspend fun getSoilTestLab(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_id") user_id: Int,
        @Query("lat") lat: Double,
        @Query("long") long: Double
    ): Response<CheckSoilTestLabDTO>

    //Status Tracker Api

    @GET("api/v1/soil-test-tracker")
    suspend fun getTracker(
        @HeaderMap headerMap: Map<String, String>,
        @Query("soil_test_request_id") soil_test_request_id: Int
    ): Response<TrackerDTO>

    @FormUrlEncoded
    @POST("api/v1/plots")
    suspend fun addCropPassData( @HeaderMap map: Map<String, String>,
                                 @Field("crop_id")crop_id:Int,
                                 @Field("account_no_id")account_no_id:Int,
                                 @Field("plot_nickname")plot_nickname:String,
                                 @Field("is_active")is_active:Int,
                                 @Field("sowing_date")sowing_date:String
    )
//                                 @Body addCropPost: AddCropRequestDomain)
            : Response<AddCropResponseDTO>
    //New Soil Test Request
    @FormUrlEncoded
    @POST("api/v1/soil-test-request")
    suspend fun postNewSoil( @HeaderMap headerMap: Map<String, String>,
                             @Field("plot_no")plot_no:String?,
                             @Field("pincode")pincode:String?,
                             @Field("address")address:String?,
                             @Field("number")number:String?,
                             ): Response<SoilTestResponseDTO>

}