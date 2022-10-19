package com.example.soiltesting.repository

import androidx.lifecycle.MutableLiveData
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.model.checksoil.CheckSoilResponse
import com.example.soiltesting.model.feedback.FeedbackRequest
import com.example.soiltesting.model.postsoil.NewSoilResponse
import com.example.soiltesting.model.postsoil.NewSoilTestPost
import com.example.soiltesting.utils.NetworkResult
import org.json.JSONObject

class CheckSoilTestingRepository constructor(private val apiService: ApiService) {
    val token:String="Bearer 104|Zd8Uq4USCYVLr7GS7P9aygT58XlzzxxZmJUgd29N"
    private val _checkSoilTestLiveData = MutableLiveData<NetworkResult<CheckSoilResponse>>()
    val checkSoilTestLiveData get() = _checkSoilTestLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    private val _newSoilTestLiveData = MutableLiveData<NetworkResult<NewSoilResponse>>()
    val newSoilTestLiveData get() = _newSoilTestLiveData

    private val _feedbackLiveData = MutableLiveData<NetworkResult<FeedbackRequest>>()
    val feedbackLiveData get() = _feedbackLiveData

    suspend fun getSoilTest(userAccount: Int,lat:Double,long:Double) {
        _checkSoilTestLiveData.postValue(NetworkResult.Loading())
        val headers= mapOf<String,String>("Accept" to "application/json, text/plain, */*",
            "Authorization" to token)
        val response = apiService.getSoilTest(headers,userAccount,lat,long)


        if (response.isSuccessful && response.body() != null) {
            _checkSoilTestLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _checkSoilTestLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _checkSoilTestLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun postNewSoil(newSoilTestPost: NewSoilTestPost) {
        _newSoilTestLiveData.postValue(NetworkResult.Loading())
        val headers= mapOf<String,String>("Accept" to "application/json, text/plain, */*",
            "Authorization" to token)
        val response = apiService.postNewSoil(headers,newSoilTestPost)

        if (response.isSuccessful && response.body() != null) {
            _newSoilTestLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _newSoilTestLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _newSoilTestLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
    suspend fun postFeedback(feedbackRequest: FeedbackRequest) {
        _feedbackLiveData.postValue(NetworkResult.Loading())
        val headers= mapOf<String,String>("Accept" to "application/json, text/plain, */*",
            "Authorization" to token)
        val response = apiService.postFeedback(feedbackRequest)

        if (response.isSuccessful && response.body() != null) {
            _feedbackLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _feedbackLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _feedbackLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}