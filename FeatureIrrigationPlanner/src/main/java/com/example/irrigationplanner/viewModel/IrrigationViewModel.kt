package com.example.irrigationplanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.AdvIrrigationModel
import com.waycool.data.Network.NetworkModels.HarvestDateModel
import com.waycool.data.Network.NetworkModels.IrrigationPerDay
import com.waycool.data.repository.AdvIrrigationRepository
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.MyCropDataDomain
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



}