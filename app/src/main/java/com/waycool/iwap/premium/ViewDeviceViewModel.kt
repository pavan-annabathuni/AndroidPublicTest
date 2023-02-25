package com.waycool.iwap.premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.DeltaTDTO
import com.waycool.data.Network.NetworkModels.GraphsViewDataDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.DevicesRepository
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.utils.Resource

class ViewDeviceViewModel :ViewModel() {

    fun getIotDevice(): LiveData<Resource<List<ViewDeviceDomain>?>> =
        DevicesRepository.getAllIotDevice().asLiveData()

    fun getIotDeviceByFarm(farmId: Int): LiveData<Resource<List<ViewDeviceDomain>?>> =
        DevicesRepository.getIotDeviceByFarm(farmId).asLiveData()

    fun getGraphsViewDevice(serial_no_id:Int?,device_model_id:Int?,value:String?): LiveData<Resource<GraphsViewDataDTO?>> =
        CropsRepository.getGraphsViewDevice(serial_no_id,device_model_id,value).asLiveData()

    fun farmDetailsDelta(farmId:Int): LiveData<Resource<DeltaTDTO?>> =
        CropsRepository.farmDetailsDelta(farmId =farmId).asLiveData()

//    fun getSoilTestHistory(): LiveData<Resource<List<ViewDeviceDTO>?>> {
//        return CropsRepository.getIotDevice().asLiveData()
//    }

}