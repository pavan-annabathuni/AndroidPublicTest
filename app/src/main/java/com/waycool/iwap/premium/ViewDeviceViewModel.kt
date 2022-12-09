package com.waycool.iwap.premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.SoilTestReportMaster
import com.waycool.data.Network.NetworkModels.ViewDeviceDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource

class ViewDeviceViewModel :ViewModel() {
    fun getIotDevice(map: MutableMap<String, Any> = mutableMapOf<String,Any>()): LiveData<Resource<ViewDeviceDTO?>> =
        CropsRepository.getIotDevice(map).asLiveData()

//    fun getSoilTestHistory(): LiveData<Resource<List<ViewDeviceDTO>?>> {
//        return CropsRepository.getIotDevice().asLiveData()
//    }

}