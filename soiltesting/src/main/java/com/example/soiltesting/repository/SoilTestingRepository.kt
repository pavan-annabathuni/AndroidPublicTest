package com.example.soiltesting.repository

import androidx.lifecycle.MutableLiveData
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.model.history.SoilHistory
import com.example.soiltesting.model.tracker.TrackerResponseX
import com.example.soiltesting.utils.NetworkResult
import org.json.JSONObject

class SoilTestingRepository constructor(private val apiService: ApiService) {

    val token: String = "Bearer 104|Zd8Uq4USCYVLr7GS7P9aygT58XlzzxxZmJUgd29N"
    private val _historyLiveData = MutableLiveData<NetworkResult<SoilHistory>>()
    val historyLiveData get() = _historyLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    private val _statusTrackerLiveData = MutableLiveData<NetworkResult<TrackerResponseX>>()
    val statusTrackerLiveData get() = _statusTrackerLiveData

    private val _statusStatusTrackerLiveData =
        MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusStatusTrackerLiveData get() = _statusStatusTrackerLiveData


    suspend fun getAllHistory(userAccount: Int) {
        _historyLiveData.postValue(NetworkResult.Loading())
        val headers = mapOf<String, String>(
            "Accept" to "application/json, text/plain, */*",
            "Authorization" to token
        )
        val response = apiService.getAllHistory(headers, userAccount)


        if (response.isSuccessful && response.body() != null) {
            _historyLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _historyLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _historyLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun getTracker(soil_test_request_id: Int) {
        _statusTrackerLiveData.postValue(NetworkResult.Loading())
        val headers = mapOf<String, String>(
            "Accept" to "application/json, text/plain, */*",
            "Authorization" to token
        )
        val response = apiService.getTracker(headers, soil_test_request_id)


        if (response.isSuccessful && response.body() != null) {
            _statusTrackerLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _statusTrackerLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _statusTrackerLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


}