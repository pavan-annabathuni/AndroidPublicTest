package com.example.irrigationplanner.viewModel

import androidx.lifecycle.*
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Sync.syncer.MyCropSyncer
import com.waycool.data.repository.AdvIrrigationRepository
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch
import java.util.*

class IrrigationViewModel:ViewModel() {

    val cropHarvestedLiveData:MutableLiveData<Boolean> = MutableLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCropIrrigation().asLiveData()

     fun getIrrigationHis(plot_id: Int): LiveData<Resource<AdvIrrigationModel?>> =
        AdvIrrigationRepository.getAdvIrrigation(plot_id).asLiveData()

    fun updateHarvest(plot_id: Int,account_id: Int,cropId:Int,harvestDate:String,Yield:Int):LiveData<Resource<HarvestDateModel?>> =
        AdvIrrigationRepository.updateHarvest(plot_id,account_id,cropId, harvestDate, Yield).asLiveData()

    fun updateIrrigation(irrigation_id: Int,irrigation:Int):LiveData<Resource<IrrigationPerDay?>> =
        AdvIrrigationRepository.updateIrrigation(irrigation_id, irrigation).asLiveData()

    fun getEditMyCrop(id:Int):LiveData<Resource<Unit?>> {
        return CropsRepository.getEditCrop(id).asLiveData()
    }
//    fun updateCropStage(id:Int,farmId:Int,plotId:Int,value1:String?,value2:String?,value3:String?,value4:String?,value5:String?,value6:String?,
//                        value7:String?,value8:String?,value9:String?,value10:String?,value11:String?,value12:String?,value13:String?,value14:String?,
//                        value15:String?):LiveData<Resource<CropStageModel?>>{
//        return AdvIrrigationRepository.updateCropStage(id,farmId,plotId,value1,value2,value3,value4,value5,value7,value7,value8,value9,
//            value10,value11,value12,value13,value14,value15).asLiveData()
//    }
    fun getCropStage(account_id: Int,plot_id: Int):LiveData<Resource<CropStageModel?>> {
        return AdvIrrigationRepository.getCropStage(account_id,plot_id).asLiveData()
    }

    fun updateCropStage(account_id: Int,cropStageId:Int,plot_id: Int,date:String):LiveData<Resource<UpdateCropStage?>> {
        return AdvIrrigationRepository.updateCropStage(account_id,cropStageId,plot_id,date).asLiveData()
    }

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getDisease(plot_id: Int):LiveData<Resource<PestAndDiseaseModel?>> {
        return AdvIrrigationRepository.getDisease(plot_id).asLiveData()
    }

    fun refreshMyCrops(){

        viewModelScope.launch {
            MyCropSyncer().invalidateSync()
            LocalSource.deleteAllMyCrops()
            MyCropSyncer().getMyCrop()
        }
    }

    fun setCropHarvested(){
        cropHarvestedLiveData.postValue(true)
    }
}