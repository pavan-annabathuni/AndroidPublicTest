package com.waycool.data.Sync.syncer

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.DashboardEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.DashboardEntityMapper
import com.waycool.data.Network.NetworkModels.DashBoardDTO
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

object DashboardSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.DASH_BOARD

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<DashboardEntity>> {

        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal(): Flow<Resource<DashboardEntity>> {

//        emit(Resource.Loading())
        return LocalSource.getDashBoard()?.map {
            Resource.Success(it)
        } ?: emptyFlow()
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            setSyncStatus(true)

            NetworkSource.dashBoard()
                .collect {
                    when (it) {
                        is Resource.Success -> {
                                LocalSource.insertDashboard(
                                    DashboardEntityMapper().mapToEntity(
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