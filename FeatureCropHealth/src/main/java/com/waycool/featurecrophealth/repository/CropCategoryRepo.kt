package com.waycool.featurecrophealth.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.waycool.data.Local.LocalSource
import com.waycool.data.utils.SharedPreferenceUtility
import com.waycool.featurecrophealth.api.RetrofitService
import com.waycool.featurecrophealth.model.cropcate.CropCategory
import com.waycool.featurecrophealth.utils.NetworkResult
import org.json.JSONObject

class CropCategoryRepo constructor(private val retrofitService: RetrofitService
) {

    private val _cropLiveData = MutableLiveData<NetworkResult<CropCategory>>()
    val cropCategoryLiveData get() = _cropLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    suspend fun getCropCategory(context: Context) {
        _cropLiveData.postValue(NetworkResult.Loading())
        val headers= LocalSource.getHeaderMapSanctum()?: emptyMap()

        val response = retrofitService.getCropCategory(headers)

        if (response.isSuccessful && response.body() != null) {
            _cropLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _cropLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _cropLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}