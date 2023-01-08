package com.waycool.data.Sync.syncer.mandiSyncer

import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.waycool.data.Local.Entity.MandiRecordEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.Sync.SyncRate
import kotlinx.coroutines.flow.Flow

object MandiSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> {
        return SyncKey.MANDI
    }

    override fun getRefreshRate(): Int {
        return SyncRate.getRefreshRate(getSyncKey())
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getMandiData(
        lat: String?,
        lon: String?,
        crop_category: String = "",
        stateIndia: String = "",
        crop: String = "",
        sortBy: String = "",
        orderBy: String = "",
        search: String = "",
    ): Flow<PagingData<MandiRecordEntity>> {


        return Pager(
            config = PagingConfig(
                pageSize = 25,
                prefetchDistance = 2, initialLoadSize = 2
            ),
            remoteMediator = MandiRemoteMediator(
                NetworkSource.apiInterface, lat, lon,
                crop_category, stateIndia, crop,
                orderBy,
                sortBy, search,
            ),
            pagingSourceFactory = { LocalSource.getMandiRecords(crop_category, crop, stateIndia, orderBy, sortBy, search, search!="") }
        ).flow
    }

}