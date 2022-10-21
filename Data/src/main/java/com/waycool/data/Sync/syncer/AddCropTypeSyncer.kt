package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.AddCropTypeEntity
import com.waycool.data.Local.Entity.ModuleMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.AddCropTypeEntityMapper
import com.waycool.data.Local.mappers.ModuleMasterEntityMapper
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

class AddCropTypeSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.ADD_CROP_TYPE

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<List<AddCropTypeEntity>>> {

        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal(): Flow<Resource<List<AddCropTypeEntity>>> {

//        emit(Resource.Loading())
        return LocalSource.getAddCropType()?.map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        } ?: emptyFlow()
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null) {
                NetworkSource.
                getAddCropType(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertAddCropType(
                                    AddCropTypeEntityMapper().toEntityList(
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