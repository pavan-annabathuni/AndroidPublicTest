package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.MyCropDataEntity
import com.waycool.data.Local.Entity.MyFarmsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.MyCropEntityMapper
import com.waycool.data.Local.mappers.MyFarmsEntityMapper
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

class MyFarmsSyncer:SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> =SyncKey.MY_FARMS

    override fun getRefreshRate(): Int =SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<List<MyFarmsEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getMyFarms()
    }

   private fun getMyFarms(): Flow<Resource<List<MyFarmsEntity>>> {
        return LocalSource.getMyFarms().map {
            Resource.Success(it)
        }
    }

    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {
            val accountId=LocalSource.getUserDetailsEntity()?.accountId
            if (accountId != null)
                NetworkSource.getMyFarms(accountId)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertMyFarms(
                                    MyFarmsEntityMapper().toEntityList(it.data?.data!!)
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