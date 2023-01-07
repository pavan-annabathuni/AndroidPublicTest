package com.waycool.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.syncer.mandiSyncer.MandiSyncer
import com.waycool.data.repository.DomainMapper.MandiRecordDomainMapper
import com.waycool.data.repository.domainModels.MandiRecordDomain
import com.waycool.data.repository.domainModels.MandiHistoryDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object MandiRepository {
    fun getMandiList(lat: String, long: String, crop_category: String = "", state: String = "", crop: String = "",
        sortBy: String = "", orderBy: String = "", search: String = ""): Flow<PagingData<MandiRecordDomain>> {

       return MandiSyncer.getMandiData(lat, long, crop_category, state, crop, sortBy, orderBy, search).map {
           it.map { mandi->
               MandiRecordDomainMapper().mapToDomain(mandi)
           }
       }

//        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
//
//        Log.d("HeaderMap", "getMandiList: $map")
//        return NetworkSource.getMandiList(lat,long,crop_category,
//            state,crop,sortBy,orderBy,search)
    }

    suspend fun getMandiHistory(crop_master_id: Int?, mandi_master_id: Int?): Flow<Resource<MandiHistoryDomain?>> {
        val map = LocalSource.getHeaderMapSanctum() ?: emptyMap()
        return NetworkSource.getMandiHistory(map, crop_master_id, mandi_master_id)
    }
}

