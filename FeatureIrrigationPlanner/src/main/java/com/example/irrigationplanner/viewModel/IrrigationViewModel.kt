package com.example.irrigationplanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.repository.AdvIrrigationRepository
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource

class IrrigationViewModel:ViewModel() {
    fun getMyCrop2(account_id: Int): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2(account_id).asLiveData()

    suspend fun getIrrigationHis(account_id: Int,plot_id: Int): LiveData<Resource<AdvIrrigationModel?>> =
        AdvIrrigationRepository.getAdvIrrigation(account_id,plot_id).asLiveData()

    fun updateHarvest(plot_id: Int,harvestDate:String,Yield:Int):LiveData<Resource<HarvestDateModel?>> =
        AdvIrrigationRepository.updateHarvest(plot_id, harvestDate, Yield).asLiveData()

    fun updateIrrigation(irrigation_id: Int,irrigation:Int):LiveData<Resource<IrrigationPerDay?>> =
        AdvIrrigationRepository.updateIrrigation(irrigation_id, irrigation).asLiveData()

    fun getEditMyCrop(id:Int):LiveData<Resource<Unit?>> {
        return CropsRepository.getEditCrop(id).asLiveData()
    }
    fun updateCropStage(id:Int,farmId:Int,plotId:Int,value1:String?,value2:String?,value3:String?,value4:String?,value5:String?,value6:String?,
                        value7:String?,value8:String?,value9:String?,value10:String?,value11:String?,value12:String?,value13:String?,value14:String?,
                        value15:String?):LiveData<Resource<CropStageModel?>>{
        return AdvIrrigationRepository.updateCropStage(id,farmId,plotId,value1,value2,value3,value4,value5,value7,value7,value8,value9,
            value10,value11,value12,value13,value14,value15).asLiveData()
    }
    fun getCropStage():LiveData<Resource<GetCropStage?>> {
        return AdvIrrigationRepository.getCropStage().asLiveData()
    }

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

}