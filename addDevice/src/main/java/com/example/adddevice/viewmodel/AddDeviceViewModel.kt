package com.example.adddevice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.ActivateDeviceDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource



class AddDeviceViewModel : ViewModel()  {
    fun activateDevice(map: MutableMap<String, Any> = mutableMapOf<String,Any>()): LiveData<Resource<ActivateDeviceDTO?>> =
        CropsRepository.activateDevice(map).asLiveData()

    fun verifyQR(deviceNumber: String, isQR: Int)=CropsRepository.verifyQR(deviceNumber, isQR).asLiveData()


    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData(

        )
}