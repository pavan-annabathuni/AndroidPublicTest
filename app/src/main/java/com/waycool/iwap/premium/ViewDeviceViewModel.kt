package com.waycool.iwap.premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.FarmDetailsDTO
import com.waycool.data.Network.NetworkModels.GraphsViewDataDTO
import com.waycool.data.Network.NetworkModels.SoilTestReportMaster
import com.waycool.data.Network.NetworkModels.ViewDeviceDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource

class ViewDeviceViewModel :ViewModel() {

    fun getIotDevice(): LiveData<Resource<ViewDeviceDTO?>> =
        CropsRepository.getIotDevice().asLiveData()
    fun getGraphsViewDevice(serial_no_id:Int?,device_model_id:Int?,value:String?): LiveData<Resource<GraphsViewDataDTO?>> =
        CropsRepository.getGraphsViewDevice(serial_no_id,device_model_id,value).asLiveData()
    fun farmDetailsDelta(): LiveData<Resource<FarmDetailsDTO?>> =
        CropsRepository.farmDetailsDelta().asLiveData()

//    fun getSoilTestHistory(): LiveData<Resource<List<ViewDeviceDTO>?>> {
//        return CropsRepository.getIotDevice().asLiveData()
//    }

}