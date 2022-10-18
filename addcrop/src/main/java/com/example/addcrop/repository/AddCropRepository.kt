package com.example.addcrop.repository

import androidx.lifecycle.MutableLiveData
import com.example.addcrop.api.ApiService
import com.example.addcrop.model.AddCropResponse
import com.example.addcrop.model.addcroppost.AddCropRequest
import com.example.addcrop.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response

class AddCropRepository constructor(private val apiService: ApiService) {
    private val _historyLiveData = MutableLiveData<NetworkResult<AddCropResponse>>()
    val historyLiveData get() = _historyLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData


    suspend fun getAllHistory() {
        _historyLiveData.postValue(NetworkResult.Loading())
        val headers= mapOf<String,String>("Accept" to "application/json, text/plain, */*",
            "Authorization" to "Bearer 4|aK1y1QqYgApnVkNHCqeDMY3XLoj95guj26UKq7jE")

        val response = apiService.getAllHistory(headers)

        if (response.isSuccessful && response.body() != null) {
            _historyLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _historyLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _historyLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
    suspend fun addCropPassData(addCropRequest: AddCropRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = apiService.addCropPassData(addCropRequest)
        handleResponse(response, "Note Created")
    }
    private fun handleResponse(response: Response<AddCropRequest>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(Pair(true, message)))
        } else {
            _statusLiveData.postValue(NetworkResult.Success(Pair(false, "Something went wrong")))
        }
    }

}