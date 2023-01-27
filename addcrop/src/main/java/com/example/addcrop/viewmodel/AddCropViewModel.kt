package com.example.addcrop.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.*

import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.FarmsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.SoilTypeDomain
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain

import com.waycool.data.utils.Resource

class AddCropViewModel : ViewModel() {
    companion object {
        const val SUBMIT_BUTTON = "SUBMIT_BUTTON"
    }

    val saveButtonPassData = MutableLiveData<String>()
    fun saveButtonClicked() {
        saveButtonPassData.value = SUBMIT_BUTTON
    }
    private val _onButtonClicked = MutableLiveData<Boolean>()
    val onButtonClicked: LiveData<Boolean> = _onButtonClicked
    fun onButtonClicked(){
        _onButtonClicked.value = true
    }


    fun getAddCropType(): LiveData<Resource<List<SoilTypeDomain>?>> {
        return CropsRepository.getSoilType().asLiveData()
    }

    fun addCropDataPass(map: MutableMap<String, Any> = mutableMapOf<String, Any>()): LiveData<Resource<AddCropResponseDTO?>> =
        CropsRepository.addCropDataPass(map).asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    fun getEditMyCrop(id: Int): LiveData<Resource<Unit?>> {
        return CropsRepository.getEditCrop(id).asLiveData()
    }

    fun getMyFarms(): LiveData<Resource<List<MyFarmsDomain>>> =
        FarmsRepository.getMyFarms().asLiveData()


    fun checkInputFields(nickName: String, area: String, date: String): Boolean {
        if (nickName.isEmpty()) {
            return false
        } else if (area.isEmpty()) {
            return false
        } else if (date.isEmpty()) {
            return false
        }
        return true
    }

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