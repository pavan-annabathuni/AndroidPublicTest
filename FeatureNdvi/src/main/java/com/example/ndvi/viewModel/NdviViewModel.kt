package com.example.ndvi.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.NdviData
import com.waycool.data.Network.NetworkModels.NdviModel
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.NdviRepository
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource
import retrofit2.Response

class NdviViewModel:ViewModel() {
    fun getNdvi(farmId:Int,account_id: Int): LiveData<Resource<NdviModel?>> =
        NdviRepository.getNdvi(farmId,account_id).asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()
}