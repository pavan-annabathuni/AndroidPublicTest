package com.waycool.iwap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.CheckTokenResponseDTO
import com.waycool.data.Network.NetworkModels.DashBoardDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.catch

class TokenViewModel : ViewModel() {
    fun checkToken(user_id: Int,token: String): LiveData<Resource<CheckTokenResponseDTO?>> =
        CropsRepository.checkToken(user_id,token).asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getDasBoard(): LiveData<Resource<DashboardDomain?>> =
     CropsRepository.getDashBoard().asLiveData()

    suspend fun getUserToken():String = LoginRepository.getUserToken().toString()

//    fun getSelectedLanguageCode() = LoginRepository.getSelectedLanguageCode()
}