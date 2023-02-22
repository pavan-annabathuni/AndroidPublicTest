package com.waycool.data.Network.ApiInterface


import android.text.Editable
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkModels.LanguageMasterDTO
import com.waycool.data.Network.NetworkModels.TagsAndKeywordsDTO
import com.waycool.data.repository.domainModels.MandiDomain
import com.waycool.data.repository.domainModels.MandiHistoryDomain
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query
import retrofit2.http.QueryMap
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
//        @Field("password") password: String,
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
        @HeaderMap map: Map<String, String>,
        @Query("lang") lang: String = "en"
    ): Response<ModuleMasterDTO>


    @POST("api/v1/register")
    @FormUrlEncoded
    suspend fun register(
        @HeaderMap map: Map<String, String>,
        @FieldMap data: Map<String, String>
    ): Response<RegisterDTO>

    @GET("api/v1/crop-master")
    suspend fun getCropMaster(
        @HeaderMap map: Map<String, String>, @Query("lang") lang: String = "en"
    ): Response<CropMasterDTO>

    @GET("api/v1/crop-category-master")
    suspend fun getCropCategoryMaster(
        @HeaderMap map: Map<String, String>, @Query("lang") lang: String = "en"
    ): Response<CropCategoryMasterDTO>

    @GET("api/v1/pest-disease-master")
    suspend fun getPestDisease(
        @HeaderMap map: Map<String, String>,
        @Query("crop_id") id: Int? = null,
        @Query("lang") lang: String = "en"
    ): Response<PestDiseaseDTO>

    //videos category
    @GET("api/v1/vans-category-master")
    suspend fun getVansCategory(
        @HeaderMap map: Map<String, String>,
        @Query("lang") lang: String = "en"
    ): Response<VansCategoryDTO?>

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
        @HeaderMap headerMap: Map<String, String>,
        @Query("lang") lang: String = "en"

    ): Response<AiCropHistoryDTO>

    @Multipart
    @POST("api/v1/ai-crop-health")
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
        @HeaderMap headerMap: Map<String, String>,
        @Query("lang") lang: String = "en"
    ): Response<AddCropTypeDTO>


    //soil testing
    @GET("api/v1/soil-test-request/history")
    suspend fun getSoilTestAllHistory(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_id") account_id: Int
    ): Response<SoilTestHistoryDTO>


    //SoilTest check

    @GET("api/v1/check-soil-test")
    suspend fun getSoilTestLab(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_id") user_id: Int,
        @Query("lat") lat: String?,
        @Query("long") long: String?
    ): Response<CheckSoilTestLabDTO>

    //Status Tracker Api

    @GET("api/v1/soil-test-tracker")
    suspend fun getTracker(
        @HeaderMap headerMap: Map<String, String>,
        @Query("soil_test_request_id") soil_test_request_id: Int,
        @Query("lang") lang: String = "en"

    ): Response<TrackerDTO>

    @FormUrlEncoded
    @POST("api/v1/plots")
    suspend fun addCropPassData(
        @HeaderMap map: Map<String, String>,
        @Field("crop_id") crop_id: Int?,
        @Field("account_no_id") account_no_id: Int?,
        @Field("plot_nickname") plot_nickname: String?,
        @Field("is_active") is_active: Int?,
        @Field("sowing_date") sowing_date: String?,
        @Field("area") area: Editable?
    )
//                                 @Body addCropPost: AddCropRequestDomain)
            : Response<AddCropResponseDTO>

    //New Soil Test Request
    @FormUrlEncoded
    @POST("api/v1/soil-test-request")
    suspend fun postNewSoil(
        @HeaderMap headerMap: Map<String, String>,
        @Field("account_id") account_id: Int,
        @Field("lat") lat: Double,
        @Field("long") long: Double,
        @Field("org_id") org_id: Int?,
        @Field("plot_no") plot_no: String?,
        @Field("pincode") pincode: String?,
        @Field("address") address: String?,
        @Field("state") state: String,
        @Field("district") district: String,
        @Field("number") number: String?,
        @Field("crop_id") plot_id: Int

    ): Response<SoilTestResponseDTO>


    @GET("api/v1/crop-advisory")
    suspend fun getCropInformation(
        @HeaderMap headerMap: Map<String, String>,
        @Query("lang") lang: String = "en"
    ): Response<CropInfo>

    @PUT("api/v1/profiles")
    @FormUrlEncoded
    suspend fun updateProfile(
        @HeaderMap map: Map<String, String>,
        @FieldMap date: Map<String, String>
//        @Field("name") name: String,
//        @Field("address") address: String,
//        @Field("village") village: String,
//        @Field("pincode") pincode: String,
//        @Field("state") state: String,
//        @Field("district") district: String
    ): Response<ProfileUpdateResponseDTO>

    @Multipart
    @POST("api/v1/update-profile-picture")
    suspend fun getProfilePic(
        @HeaderMap headerMap: Map<String, String>,
        @Part file: MultipartBody.Part
    ): Response<profilePicModel>

    @GET("api/v1/get-mandi")
    suspend fun getMandiList(
        @HeaderMap map: Map<String, String>?,
        @Query("lat") lat: String?,
        @Query("long") long: String?,
        @Query("crop_category") category: String?,
        @Query("state") state: String?,
        @Query("crop") crop: String?,
        @Query("page") page: Int,
        @Query("sort_by") sort_by: String?,
        @Query("order_by") orderBy: String?,
        @Query("search") search: String?,
        @Query("lang")lang: String = "en"
    ): Response<MandiDomain>

    @GET("api/v1/get-mandi-history")
    suspend fun getMandiHistory(
        @HeaderMap map: Map<String, String>?,
        @Query("crop_master_id") crop_master_id: Int?,
        @Query("mandi_master_id") mandi_master_id: Int?,
        @Query("sub_record_id")sub_record_id:String?
    ): Response<MandiHistoryDomain>

    @GET("api/v1/india-state-master")
    suspend fun getStateList(
        @HeaderMap map: Map<String, String>?,
        @Query("lang")lang: String = "en"
    ): Response<StateModel>

    @FormUrlEncoded
    @POST("api/v1/check-token")
    suspend fun checkToken(
        @HeaderMap headerMap: Map<String, String>,
        @Field("user_id") user_id: Int,
        @Field("token") token: String,
    ): Response<CheckTokenResponseDTO>


    @GET("api/v1/my-crops")
    suspend fun getMyCrops(
        @HeaderMap map: Map<String, String>?,
        @Query("account_id") account_id: Int,
        @Query("lang")lang: String = "en"
    ): Response<MyCropsModel>


    @DELETE("api/v1/plots/{plots}")
    suspend fun editMyCrops(
        @HeaderMap map: Map<String, String>?,
        @Path("plots") plots: Int
    ): Response<Unit>

    @GET("api/v1/view-crop")
    suspend fun advIrrigation(
        @HeaderMap map: Map<String, String>?,
        @Query("account_id") accountId: Int,
        @Query("plot_id") plotId: Int
    ): Response<AdvIrrigationModel>

    @POST("api/v1/farms")
    @FormUrlEncoded
    suspend fun addFarm(
        @HeaderMap map: Map<String, String>?,
        @Field("account_no_id") accountId: Int,
        @Field("farm_name") farmName: String,
        @Field("farm_center") farm_center: String,
        @Field("farm_area") farm_area: String,
        @Field("farm_json") farm_json: String,
        @Field("plot_ids") plot_ids: String? = null,
        @Field("is_primary") is_primary: Int? = null,
        @Field("farm_water_source") farm_water_source: String? = null,
        @Field("farm_pump_hp") farm_pump_hp: String? = null,
        @Field("farm_pump_type") farm_pump_type: String? = null,
        @Field("farm_pump_depth") farm_pump_depth: String? = null,
        @Field("farm_pump_pipe_size") farm_pump_pipe_size: String? = null,
        @Field("farm_pump_flow_rate") farm_pump_flow_rate: String? = null,
    ): Response<ResponseBody>

    @PUT("api/v1/farms/{farmId}")
    @FormUrlEncoded
    suspend fun updateFarm(
        @HeaderMap map: Map<String, String>?,
        @Field("account_no_id") accountId: Int,
        @Field("farm_name") farmName: String,
        @Field("farm_center") farm_center: String,
        @Field("farm_area") farm_area: String,
        @Field("farm_json") farm_json: String,
        @Field("plot_ids") plot_ids: String? = null,
        @Field("is_primary") is_primary: Int? = null,
        @Field("farm_water_source") farm_water_source: String? = null,
        @Field("farm_pump_hp") farm_pump_hp: String? = null,
        @Field("farm_pump_type") farm_pump_type: String? = null,
        @Field("farm_pump_depth") farm_pump_depth: String? = null,
        @Field("farm_pump_pipe_size") farm_pump_pipe_size: String? = null,
        @Field("farm_pump_flow_rate") farm_pump_flow_rate: String? = null,
        @Path("farmId") farmId:Int?
    ): Response<ResponseBody>


    //Add crop Fro Free And Premium
    @FormUrlEncoded
    @POST("api/v1/plots")
    @JvmSuppressWildcards
    suspend fun addCropDataPass(
        @HeaderMap map: Map<String, String>,
//                                @Field("account_no_id")account_no_id:Int?,
//                                @Field("crop_id")crop_id:Int?,
        @FieldMap bodymap: Map<String, Any>
    ): Response<AddCropResponseDTO>

    //Activate Device

    @FormUrlEncoded
    @POST("api/v1/activate-devices")
    @JvmSuppressWildcards
    suspend fun activateDevice(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap devicedata: Map<String, Any>

    ): Response<ActivateDeviceDTO>

    @FormUrlEncoded
    @POST("api/v1/verify-device")
    suspend fun verifyQR(
        @HeaderMap headerMap: Map<String, String>,
        @Field("device_number") deviceNumber: String,
        @Field("is_device_qr") isDeviceQR: Int
    ): Response<VerifyQrDTO>


    //view report

    @POST("api/v1/soil-test-report-data")
    suspend fun viewReport(
        @HeaderMap headerMap: Map<String, String>,
        @Query("id") id: Int

    ): Response<SoilTestReportMaster>

    //    @Streaming
    @GET("api/v1/download-soil-test-report")
    suspend fun pdfDownload(
        @HeaderMap map: Map<String, String>?,
        @Query("id") id: Int
    ): Response<ResponseBody>

    @GET("api/v1/view-devices")
    suspend fun getIotDevice(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_no_id")accountNo:Int
    ): Response<ViewDeviceDTO>

    @GET("api/v1/view-devices")
    suspend fun getGraphsViewDevice(
        @HeaderMap headerMap: Map<String, String>,
        @Query("serial_no_id") serial_no_id: Int?,
        @Query("device_model_id") device_model_id: Int?,
        @Query("value") value: String?
    ): Response<GraphsViewDataDTO>


    //    @FormUrlEncoded
    @GET("api/v1/farm/my-farm")
    suspend fun getMyFarms(
        @HeaderMap map: Map<String, String>?,
        @Query("account_no_id") account_no_id: Int
    ): Response<MyFarmsDTO>

    @GET("api/v1/dashboard")
    suspend fun dashBoard(
        @HeaderMap map: Map<String, String>?,

//        @Query("")
    ): Response<DashBoardDTO>

    @GET("api/v1/get-delta-t-data")
    suspend fun farmDetailsDelta(
        @HeaderMap map: Map<String, String>?,
        @Query("farm_id") farmId: Int
    ): Response<FarmDetailsDTO>

    //variety crop
    @GET("api/v1/crop-variety")
    suspend fun cropVariety(
        @HeaderMap map: Map<String, String>?,
        @Query("crop_id") crop_id: Int,
        @Query("lang") lang: String
    ): Response<VarietyCropDTO>

    @GET("api/v1/app-translations")
    suspend fun getTranslations(
        @HeaderMap map: Map<String, String>,
        @Query("lang") lang: String
    ): Response<AppTranlationsDTO>

//    suspend fun getTranslations(
//        @HeaderMap map: Map<String, String>,
//        @Query("lang") lang: String
//    ): Response<AppTranlationsDTO>


    @PUT("api/v1/plots/{plot}")
    @FormUrlEncoded
    suspend fun harvestDate(
        @HeaderMap map: Map<String, String>?,
        @Path("plot") plots: Int,
        @Field("account_no_id")accountId: Int,
        @Field("crop_id")crop_id: Int,
        @Field("actual_harvest_date") actual_harvest_date: String,
        @Field("actual_yield") actual_yield: Int,
        @Field("_method") method: String
    ): Response<HarvestDateModel>

    @POST("api/v1/irrigation-forecasts/{irrigation_forecast}")
    @FormUrlEncoded
    suspend fun irrigationPerDay(
        @HeaderMap map: Map<String, String>?,
        @Path("irrigation_forecast") irrigationId: Int,
        @Field("irrigation") irrigation: Int,
        @Field("_method") method: String
    ): Response<IrrigationPerDay>

    @POST("api/v1/plot-stage-calender")
    @FormUrlEncoded
    suspend fun updateCropStage(
        @HeaderMap map: Map<String, String>?,
        @Field("account_no_id") accountId: Int,
        @Field("farm_id") farmId: Int,
        @Field("plot_id") plotId: Int,
        @Field("fruit_pruning_fruit_pruning") fruit_pruning: String?,
        @Field("fruit_pruning_bud_break") bud_break: String?,
        @Field("fruit_pruning_removal_of_excessive") fruit_removal: String?,
        @Field("fruit_pruning_shoot_development") fruit_development: String?,
        @Field("fruit_pruning_flowering") fruit_flowering: String?,
        @Field("fruit_pruning_fruit_set") fruit_set: String?,
        @Field("fruit_pruning_berry_development") fruit_berry: String?,
        @Field("fruit_pruning_beginning_of_veraison") fruit_version: String?,
        @Field("fruit_pruning_harvest") fruit_harvest: String?,
        @Field("fruit_pruning_rest_period") fruit_rest: String?,
        @Field("foundation_pruning_foundation_pruning") foundation_pruning: String?,
        @Field("foundation_pruning_bud_break") foundation_pruning_bud_break: String?,
        @Field("foundation_pruning_cane_thinning") foundation_sub_cane_thinning: String?,
        @Field("foundation_pruning_sub_cane") foundation_sub_cane: String?,
        @Field("foundation_pruning_topping") foundation_topping: String?,
    ): Response<CropStageModel>

    @GET("api/v1/get-crop-stage-masters")
    suspend fun getCropStage(
        @HeaderMap map: Map<String, String>?,
        @Query("account_no_id") account: Int?,
        @Query("plot_id") plotId: Int
    ): Response<CropStageModel>

    @POST("api/v1/add-farm-support")
    @FormUrlEncoded
    suspend fun updateFarmSupport(
        @HeaderMap map: Map<String, String>?,
        @Field("account_no_id")accountId: Int,
        @Field("name") name: String,
        @Field("contact") contact: Long,
        @Field("lat") lat: Double,
        @Field("long") long: Double,
        @Field("role_id") roleId: Int,
        @Field("pincode") pincode: Int?,
        @Field("village") village: String?,
        @Field("address") address: String?,
        @Field("state") state: String?,
        @Field("district") district: String
    ): Response<FarmSupportModel>

    @GET("api/v1/get-farm-support-users")
    suspend fun getFarmSupportUser(
        @HeaderMap map: Map<String, String>?,
        @Query("account_id") accountId: Int
    ): Response<GetFarmSupport>

    @DELETE("api/v1/delete-farm-support/{id}")
    suspend fun deleteFarmSupport(
        @HeaderMap map: Map<String, String>?,
        @Path("id") id: Int
    ): Response<DeleteFarmSupport>

    @GET("api/v1/get-user-ndvi-history")
    suspend fun getNdvi(
        @HeaderMap map: Map<String, String>?,
        @Query("farm_id") farmId: Int,
        @Query("account_no_id") account_no_id: Int
    ): Response<NdviModel>

    @GET
    suspend fun getNDVIMean(@Url url: String): Response<NDVIMean>


    @GET("api/v1/user-notifications")
    suspend fun getNotification(
        @HeaderMap map: Map<String, String>?,
    ): Response<NotificationModel>

    @PUT("api/v1/update-user-notification")
    @FormUrlEncoded
    suspend fun updateNotification(
        @HeaderMap map: Map<String, String>?,
        @Field("notification_id") Nid: String
    ): Response<UpdateNotification>

    @POST("api/v1/plot-stage-calender")
    suspend fun updateCropStage(
        @HeaderMap map: Map<String, String>?,
        @Query("account_no_id") accountId: Int,
        @Query("crop_stage_master_id") stageId: Int,
        @Query("plot_id") plotId: Int,
        @Query("date") date: String
    ): Response<UpdateCropStage>

    @GET("api/v1/view-crop-disease")
    suspend fun getDisease(
        @HeaderMap map: Map<String, String>?,
        @Query("account_id") accountId: Int,
        @Query("plot_id") plotId: Int,
        @Query("lang")lang: String = "en"
    ): Response<PestAndDiseaseModel>

    @GET("api/v1/get-mandi-master")
    suspend fun getMandiMaster(
        @HeaderMap map: Map<String, String>?,
        @Query("lang")lang: String = "en"
    ): Response<MandiMasterModel>

    //crop variety

}