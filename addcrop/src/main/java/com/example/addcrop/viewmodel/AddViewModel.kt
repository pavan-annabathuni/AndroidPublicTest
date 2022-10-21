package com.example.addcrop.viewmodel

import android.app.Application
import androidx.lifecycle.*

import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.Repository.DomainModels.AddCropRequestDomain
import com.waycool.data.Repository.DomainModels.AddCropTypeDomain

import com.waycool.data.utils.Resource
import java.text.DateFormat
import java.util.*

class AddViewModel :ViewModel() {

    fun getAddCropType (): LiveData<Resource<List<AddCropTypeDomain>?>> {
        return CropsRepository.getAddCropType().asLiveData()
    }
    fun addCropPassData(crop_id:Int,account_id:Int,plot_nickname:String,
                        is_active:Int,sowing_date: String
    ): LiveData<Resource<AddCropResponseDTO?>> =
        CropsRepository.addCropPassData(crop_id,account_id,plot_nickname,is_active,sowing_date).asLiveData()

//    private val apiClient: ApiService = RetrofitBuilder.getInstance().create(ApiService::class.java)
//
//    private val repository = AddCropRepository(apiClient)
//    val detailsLiveData get() = repository.historyLiveData
//    val statusLiveData get() = repository.statusLiveData
//    fun getAllHistory() {
//        viewModelScope.launch {
//            repository.getAllHistory()
//        }
//    }
//    fun addCropPassData(addCropRequest: AddCropRequest) {
//        viewModelScope.launch {
//            repository.addCropPassData(addCropRequest)
//        }
//    }

}