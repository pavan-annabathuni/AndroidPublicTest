package com.waycool.data.Sync.syncer

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.MyCropDataEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.MyCropEntityMapper
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.Sync.SyncRate
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MyCropSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.MY_CROPS
    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getMyCrop(): Flow<Resource<List<MyCropDataEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getSelectedMyCrop()
    }

    private fun getSelectedMyCrop(): Flow<Resource<List<MyCropDataEntity>>> {
        return LocalSource.getMyCrop().map {
            Resource.Success(it)
        }
    }

    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val accountId: Int? = LocalSource.getUserDetailsEntity()?.accountId
            if (headerMap != null)
                if (accountId != null) {
                    setSyncStatus(true)
                    NetworkSource.getMyCrop2(headerMap, accountId)
                        .collect {
                            when (it) {
                                is Resource.Success -> {
                                    Log.d("MyCrops", " ${it.data}")
                                    LocalSource.insertMyCrop(
                                        MyCropEntityMapper().toEntityList(it.data?.data!!)
                                        //  CropInformationEntityMapper().toEntityList(it.data?.data!!)
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