package com.example.addcrop.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.addcrop.api.ApiService
import com.example.addcrop.model.addcroppost.AddCropRequest
import com.example.addcrop.network.RetrofitBuilder
import com.example.addcrop.repository.AddCropRepository
import com.waycool.data.Network.NetworkModels.AddCropRequestDTO
import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.Repository.DomainModels.AddCropRequestDomain
import com.waycool.data.Repository.DomainModels.AddCropTypeDomain
import com.waycool.data.Repository.DomainModels.RegisterDomain
import com.waycool.data.Repository.LoginRepository
import com.waycool.data.utils.Resource

import kotlinx.coroutines.launch

class AddViewModel :ViewModel() {

    fun getAddCropType (): LiveData<Resource<List<AddCropTypeDomain>?>> {
        return CropsRepository.getAddCropType().asLiveData()
    }
    fun addCropPassData(requestDTO: AddCropRequestDomain): LiveData<Resource<AddCropResponseDTO?>> =
        CropsRepository.addCropPassData(requestDTO).asLiveData()

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