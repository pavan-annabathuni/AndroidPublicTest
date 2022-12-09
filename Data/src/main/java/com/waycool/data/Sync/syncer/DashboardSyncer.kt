package com.waycool.data.Sync.syncer

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.SoilTestHistoryEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.SoilTestHistoryMapper
import com.waycool.data.Network.NetworkModels.DashBoardModel
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DashboardSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.DASH_BOARD

    override fun getRefreshRate(): Int {
        TODO("Not yet implemented")
    }
    fun getData(account_id: Int): Flow<Resource<List<DashBoardModel>>> {

        GlobalScope.launch(Dispatchers.IO) {

            Log.d("SoilTestSyncer","Sync Status: ${isSyncRequired()}")

            if (isSyncRequired()) {
//                makeNetworkCall(account_id)
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal(): Flow<Resource<List<DashBoardModel>>> {

//        emit(Resource.Loading())
        return LocalSource.getDashBoard()?.map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        } ?: emptyFlow()
    }

//    private fun makeNetworkCall() {
//        GlobalScope.launch(Dispatchers.IO) {
//            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
////            val account =
////                LocalSource.getUserDetailsEntity()?.account
////                    ?.firstOrNull { it.accountType == "outgrow" }
//
//
//            if (headerMap != null ) {
//                NetworkSource.dashBoard(headerMap)
//                    .collect {
//                        when (it) {
//                            is Resource.Success -> {
////                                LocalSource.insertDashboard(
////                                    SoilTestHistoryMapper().toEntityList(
////                                        it.data?.data!!
////                                    )
////                                )
////                                setSyncStatus(true)
//                            }
//
//                            is Resource.Loading -> {
//
//                            }
//                            is Resource.Error -> {
//                                setSyncStatus(false)
//                            }
//                        }
//                    }
//            }
//        }
//    }
}