package com.waycool.iwap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.CheckTokenResponseDTO
import com.waycool.data.Network.NetworkModels.SoilTestResponseDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource

class TokenViewModel : ViewModel() {
    fun checkToken(user_id: Int,token: String): LiveData<Resource<CheckTokenResponseDTO?>> =
        CropsRepository.checkToken(user_id,token).asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    suspend fun getUserToken():String = LoginRepository.getUserToken()!!

//    fun getSelectedLanguageCode() = LoginRepository.getSelectedLanguageCode()
}