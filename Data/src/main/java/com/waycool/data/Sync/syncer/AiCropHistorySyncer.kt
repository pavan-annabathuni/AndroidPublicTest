package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.AiCropHistoryEntityMapper
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.Sync.SyncRate
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AiCropHistorySyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.AI_CROP_HISTORY

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

//    fun getData(): Flow<Resource<List<AiCropHistoryEntity>>> {
//
//        GlobalScope.launch(Dispatchers.IO) {
//            if (isSyncRequired()) {
//                makeNetworkCall()
//            }
//        }
//        return getDataFromLocal()
//    }

//    private fun getDataFromLocal(): Flow<Resource<List<AiCropHistoryEntity>>> {
//        return LocalSource.getAiCropHistory()?.map {
//            if (it != null) {
//                (Resource.Success(it))
//            } else {
//                (Resource.Error(""))
//            }
//        } ?: emptyFlow()
//    }
    fun getDataFromAiHistoryData(searchQuery: String? = ""): Flow<Resource<List<AiCropHistoryEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromAiHistory(searchQuery)
    }

    private fun getDataFromAiHistory(searchQuery: String? = ""): Flow<Resource<List<AiCropHistoryEntity>>> {
        return LocalSource.getAiHistory(searchQuery).map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        }
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {


            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null) {
                setSyncStatus(true)
                NetworkSource.getAiCropHistory(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertHistory(
                                    AiCropHistoryEntityMapper().toEntityList(
                                        it.data?.data!!
                                    )
                                )
                                if (it.data.data.isNotEmpty())
                                    setSyncStatus(true)
                                else setSyncStatus(false)
                            }

                            is Resource.Loading -> {

                            }
                            is Resource.Error -> {
                                setSyncStatus(false)
                            }
                        }
                    }
            }
        }
    }
}