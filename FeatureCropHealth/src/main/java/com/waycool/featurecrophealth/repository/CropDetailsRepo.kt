package com.waycool.featurecrophealth.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.waycool.data.Local.LocalSource
import com.waycool.data.utils.SharedPreferenceUtility
import com.waycool.featurecrophealth.api.RetrofitService
import com.waycool.featurecrophealth.db.CropDetailsDao
import com.waycool.featurecrophealth.model.cropdetails.CropDetails
import com.waycool.featurecrophealth.model.cropdetails.Data
import com.waycool.featurecrophealth.utils.NetworkResult
import com.waycool.featurecrophealth.utils.NetworkUtils
import org.json.JSONObject

class CropDetailsRepo constructor(private val retrofitService: RetrofitService ,private val detailsDao: CropDetailsDao

) {
    private val _cropDetailsLiveData = MutableLiveData<NetworkResult<CropDetails>>()
    val cropDetailsLiveData get() = _cropDetailsLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData
    val network=NetworkUtils

    suspend fun getCropDetails(context: Context) {
        if (network.isInternetAvailable(context)){
            _cropDetailsLiveData.postValue(NetworkResult.Loading())

            val headers= LocalSource.getHeaderMapSanctum()?: emptyMap()


            val response = retrofitService.getCropDetails(headers)

            if (response.isSuccessful && response.body() != null) {
                detailsDao.addQuotes(response.body()!!.data)
                _cropDetailsLiveData.postValue(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _cropDetailsLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            } else {
                _cropDetailsLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
            }
        }else{
            val response = detailsDao.getQuotes()
            val quoteList =CropDetails (response,"",true)
            _cropDetailsLiveData.postValue(NetworkResult.Success(quoteList))

        }
        }

      suspend fun getLocalCropList():List<Data>{
        return detailsDao.getQuotes()

    }
}