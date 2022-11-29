package com.example.addcrop.viewmodel

import android.text.Editable
import android.widget.EditText
import androidx.lifecycle.*
import com.waycool.data.Local.LocalSource

import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.Network.NetworkModels.MyCropsModel
import com.waycool.data.Network.NetworkModels.StateModel
import com.waycool.data.Network.NetworkSource
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.AddCropTypeDomain
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain

import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

class AddViewModel :ViewModel() {

    fun getAddCropType (): LiveData<Resource<List<AddCropTypeDomain>?>> {
        return CropsRepository.getAddCropType().asLiveData()
    }
//    fun addCropPassData(
//        crop_id:Int?, account_id:Int?, plot_nickname:String?,
//        is_active:Int?, sowing_date: String?, area: Editable): LiveData<Resource<AddCropResponseDTO?>> =
//        CropsRepository.addCropPassData(crop_id,account_id,plot_nickname,is_active,sowing_date,area).asLiveData()



    fun addCropDataPass(map: MutableMap<String, Any> = mutableMapOf<String,Any>()): LiveData<Resource<AddCropResponseDTO?>> =
        CropsRepository.addCropDataPass(map).asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getMyCrop2(account_id: Int): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2(account_id).asLiveData()

    fun getEditMyCrop(id:Int):LiveData<Resource<Unit?>> {
        return CropsRepository.getEditCrop(id).asLiveData()
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