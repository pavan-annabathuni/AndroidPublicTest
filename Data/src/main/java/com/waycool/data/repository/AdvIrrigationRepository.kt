package com.waycool.data.repository

import androidx.lifecycle.asLiveData
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow

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
}