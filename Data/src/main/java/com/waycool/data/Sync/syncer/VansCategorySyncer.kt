package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.VansCategoryEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.VansCategoryEntityMapper
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

class VansCategorySyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.VANS_CATEGORY_MASTER

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<List<VansCategoryEntity>?>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal(): Flow<Resource<List<VansCategoryEntity>?>> {
        return LocalSource.getVansCategory()?.map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        }?: emptyFlow()
    }

    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {

            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null) {
                setSyncStatus(true)
                NetworkSource.getVansCategory(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertVansCategory(
                                    VansCategoryEntityMapper().toEntityList(
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