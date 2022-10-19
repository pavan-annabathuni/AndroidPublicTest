package com.example.soiltesting.apiclient

import com.example.soiltesting.model.checksoil.CheckSoilResponse
import com.example.soiltesting.model.feedback.FeedbackRequest
import com.example.soiltesting.model.history.SoilHistory
import com.example.soiltesting.model.postsoil.NewSoilResponse
import com.example.soiltesting.model.postsoil.NewSoilTestPost
import com.example.soiltesting.model.tracker.TrackerResponseX
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
//"26|lL1bA6UYLOqozoHfPbqtkUqOyfUm48BhWCQGIdGU"

    //History Api

    @GET("api/v1/soil-test-request/history")
    suspend fun getAllHistory(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_id") user_id: Int
    ): Response<SoilHistory>

    //Status Tracker Api

    @GET("api/v1/soil-test-tracker")
    suspend fun getTracker(
        @HeaderMap headerMap: Map<String, String>,
        @Query("soil_test_request_id") soil_test_request_id: Int
    ): Response<TrackerResponseX>

    //SoilTest check

    @GET("api/v1/check-soil-test")
    suspend fun getSoilTest(
        @HeaderMap headerMap: Map<String, String>,
        @Query("account_id") user_id: Int,
        @Query("lat") lat: Double,
        @Query("long") long: Double
    ): Response<CheckSoilResponse>

    //New Soil Test Request

    @POST("api/v1/soil-test-request")
    suspend fun postNewSoil( @HeaderMap headerMap: Map<String, String>,@Body newSoilTestPost: NewSoilTestPost): Response<NewSoilResponse>

    //Feedback Api

    @POST("api/v1/feedback-recorder")
    suspend fun postFeedback(@Body feedbackRequest: FeedbackRequest): Response<FeedbackRequest>


}