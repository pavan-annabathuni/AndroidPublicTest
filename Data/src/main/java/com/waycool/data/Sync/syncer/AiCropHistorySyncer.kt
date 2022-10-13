package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.Local.Entity.CropCategoryEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.AiCropHistoryEntityMapper
import com.waycool.data.Local.mappers.CropCategoryEnitiyMapper
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.Sync.SyncRate
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AiCropHistorySyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.AI_CROP_HISTORY

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<List<AiCropHistoryEntity>>> {

        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal() = flow<Resource<List<AiCropHistoryEntity>>> {

        emit(Resource.Loading())
        LocalSource.getAiCropHistory()?.map {
            if (it != null) {
                emit(Resource.Success(it))
            } else {
                emit(Resource.Error(""))
            }
        }
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null) {
                NetworkSource.getAiCropHistory(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertAiCropHistory(
                                    AiCropHistoryEntityMapper().toEntityList(
                                        it.data?.data!!
                                    )
                                )
                                setSyncStatus(true)
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