package com.waycool.data.repository

import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.AdvIrrigationModel
import com.waycool.data.Network.NetworkModels.StateModel
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow

object AdvIrrigationRepository {
    suspend fun getAdvIrrigation(account_id: Int,plot_id:Int): Flow<Resource<AdvIrrigationModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getAdvIrrigation(map,account_id,plot_id)
    }
}