package com.waycool.data.Network.ApiInterface

import retrofit2.http.GET
import com.waycool.data.Network.NetworkModels.OTPResponseDTO
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface OTPApiInterface {
    @GET("api/v5/otp")
   suspend fun sendOTP(
        @Query("authkey") authkey: String?,
        @Query("template_id") templateId: String?,
        @Query("mobile") mobile: String?
    ): Response<OTPResponseDTO?>

    @POST("api/v5/otp/retry")
    suspend fun retryOTP(
        @Query("authkey") authkey: String?,
        @Query("mobile") mobile: String?,
        @Query("retrytype") retryType: String?
    ): Response<OTPResponseDTO?>

    @POST("api/v5/otp/verify")
  suspend  fun verifyOTP(
        @Query("authkey") authkey: String?,
        @Query("otp") otp: String?,
        @Query("mobile") mobile: String?
    ): Response<OTPResponseDTO?>
}