package com.waycool.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.MandiMasterModel
import com.waycool.data.Network.NetworkModels.MandiModel
import com.waycool.data.Network.NetworkModels.profile
import com.waycool.data.Network.NetworkSource
import com.waycool.data.repository.domainModels.MandiDomain
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.domainModels.MandiHistoryDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow

object MandiRepository {
    suspend fun getMandiList(lat:String,long:String,crop_category:String?,state:String?,crop:String?,
                             sortBy: String?, orderBy: String?,search:String?,accountId:Int?):
            Flow<PagingData<MandiDomainRecord>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        Log.d("HeaderMap", "getMandiList: $map")
        return NetworkSource.getMandiList(lat,long,crop_category,
            state,crop,sortBy,orderBy,search,accountId)
    }

    suspend fun getMandiHistory(crop_master_id:Int?,mandi_master_id:Int?,sub_record_id:String?): Flow<Resource<MandiHistoryDomain?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getMandiHistory(map,crop_master_id,mandi_master_id,sub_record_id)
    }

     fun getMandiMaster(): Flow<Resource<MandiMasterModel?>> {
        return NetworkSource.getMandiMaster()
    }
}

