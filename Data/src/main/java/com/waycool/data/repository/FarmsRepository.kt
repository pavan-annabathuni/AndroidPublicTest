package com.waycool.data.repository

import android.util.Log
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.syncer.MyCropSyncer
import com.waycool.data.Sync.syncer.MyFarmsSyncer
import com.waycool.data.repository.DomainMapper.LanguageMasterDomainMapper
import com.waycool.data.repository.DomainMapper.MyFarmsDomainMapper
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

object FarmsRepository {

    fun addFarm(
        accountId: Int,
        farmName: String,
        farm_center: String,
        farm_area: String,
        farm_json: String,
        plot_ids: String?,
        is_primary: Int?=null,
        farm_water_source: String?,
        farm_pump_hp: String?,
        farm_pump_type: String?,
        farm_pump_depth: String?,
        farm_pump_pipe_size: String?,
        farm_pump_flow_rate: String?
    ): Flow<Resource<ResponseBody?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
            MyFarmsSyncer().invalidateSync()
        }
        return NetworkSource.addFarm(
            accountId,
            farmName,
            farm_center,
            farm_area,
            farm_json,
            plot_ids,
            is_primary,
            farm_water_source,
            farm_pump_hp,
            farm_pump_type,
            farm_pump_depth,
            farm_pump_pipe_size,
            farm_pump_flow_rate
        )
    }


    fun getMyFarms(): Flow<Resource<List<MyFarmsDomain>>> {
        return MyFarmsSyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(MyFarmsDomainMapper().toDomainList(it.data?: emptyList()))
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }
}