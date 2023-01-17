package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.MyFarmsEntity
import com.waycool.data.Local.Entity.ViewDeviceEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.MyFarmsEntityMapper
import com.waycool.data.Local.mappers.ViewDeviceEntityMapper
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

object ViewDevicesSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> {
        return SyncKey.MY_DEVICES
    }

    override fun getRefreshRate(): Int {
        return SyncRate.getRefreshRate(getSyncKey())
    }

    fun getAllDeviceData(): Flow<Resource<List<ViewDeviceEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getViewDevices()
    }

    private fun getViewDevices(): Flow<Resource<List<ViewDeviceEntity>>> {
        return LocalSource.getAllDevices().map {
            Resource.Success(it)
        }
    }
      fun getDeviceDataByFarm(farmId:Int): Flow<Resource<List<ViewDeviceEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getViewDevicesByFarm(farmId)
    }

    private fun getViewDevicesByFarm(farmId:Int): Flow<Resource<List<ViewDeviceEntity>>> {
        return LocalSource.getDevicesByFarm(farmId).map {
            Resource.Success(it)
        }
    }



    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {

            val accountId= LocalSource.getUserDetailsEntity()?.accountId
            if (accountId != null) {
                NetworkSource.getIotDevice()
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertViewDevice(
                                    ViewDeviceEntityMapper().toEntityList(it.data?.data!!)
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

    fun downloadDevices() {
        makeNetworkCall()
    }

}