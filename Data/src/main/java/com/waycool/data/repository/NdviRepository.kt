package com.waycool.data.repository

import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.AdvIrrigationModel
import com.waycool.data.Network.NetworkModels.NDVIMean
import com.waycool.data.Network.NetworkModels.NdviData
import com.waycool.data.Network.NetworkModels.NdviModel
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow

object NdviRepository {
     fun getNdvi(farmId:Int): Flow<Resource<NdviModel?>> {
        return NetworkSource.getNdvi(farmId)
    }

    fun getNDVIMean(url:String):Flow<Resource<NDVIMean?>>{
        return NetworkSource.getNdviMean(url)
    }
}