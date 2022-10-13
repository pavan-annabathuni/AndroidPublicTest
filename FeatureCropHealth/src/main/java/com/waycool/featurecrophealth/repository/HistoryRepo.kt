package com.waycool.featurecrophealth.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.waycool.data.Local.LocalSource
import com.waycool.data.utils.SharedPreferenceUtility
import com.waycool.featurecrophealth.api.RetrofitService
import com.waycool.featurecrophealth.model.apicroppost.AiCropPostResponse
import com.waycool.featurecrophealth.model.historydata.HistoryResponse
import com.waycool.featurecrophealth.utils.NetworkResult
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response

class HistoryRepo constructor(private val retrofitService: RetrofitService) {
    private val _historyLiveData = MutableLiveData<NetworkResult<HistoryResponse>>()
    val historyLiveData get() = _historyLiveData


    private val _postLiveData = MutableLiveData<NetworkResult<AiCropPostResponse>>()
    val postLiveData get() = _postLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    suspend fun getAllHistory(userInt: Int, context: Context) {
        _historyLiveData.postValue(NetworkResult.Loading())


        val headers = LocalSource.getHeaderMapSanctum()?: emptyMap()

        val response = retrofitService.getAllHistory(headers, userInt)


        if (response.isSuccessful && response.body() != null) {
            _historyLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _historyLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _historyLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun aiPassData(
        context: Context,
        user_id: Int,
        crop_id: Int,
        crop_name: String,
        image: MultipartBody.Part
    ) {
        _postLiveData.postValue(NetworkResult.Loading())
        val headers = LocalSource.getHeaderMapSanctum()?: emptyMap()

        val response = retrofitService.postAiCrop(headers, user_id, crop_id, crop_name, image)
        handleResponse(response)
    }


    private fun handleResponse(response: Response<AiCropPostResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _postLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _postLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _postLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}