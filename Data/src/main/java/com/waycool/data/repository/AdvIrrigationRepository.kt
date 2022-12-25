package com.waycool.data.repository

import androidx.lifecycle.asLiveData
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.*

object AdvIrrigationRepository {
    suspend fun getAdvIrrigation(account_id: Int,plot_id:Int): Flow<Resource<AdvIrrigationModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getAdvIrrigation(map,account_id,plot_id)
    }
     fun updateHarvest(plot_id: Int,harvest_date:String,actual_yield:Int):Flow<Resource<HarvestDateModel?>>{
        return NetworkSource.updateHarvest(plot_id,harvest_date,actual_yield)
    }
    fun updateIrrigation(irrigation_id: Int,irrigation:Int):Flow<Resource<IrrigationPerDay?>>{
        return NetworkSource.updateIrrigation(irrigation_id,irrigation)
    }
    fun updateCropStage(
        id:Int,farmId:Int,plotId:Int,value1:String?,value2:String?,value3:String?,value4:String?,value5:String?,value6:String?,
        value7:String?,value8:String?,value9:String?,value10:String?,value11:String?,value12:String?,value13:String?,value14:String?,
        value15:String?):Flow<Resource<CropStageModel?>>{
        return  NetworkSource.updateCropStage(id,farmId,plotId,value1,value2,value3,value4,value5,value7,value7,value8,value9,
            value10,value11,value12,value13,value14,value15)
    }
    fun getCropStage(account_id: Int,plot_id: Int):Flow<Resource<CropStageModel?>>{
        return NetworkSource.getCropStage(account_id,plot_id)
    }

    fun updateCropStage(account_id: Int,cropStageId:Int,plot_id: Int,date:String):Flow<Resource<UpdateCropStage?>>{
        return NetworkSource.updateCropStage(account_id,cropStageId,plot_id,date)
    }
}