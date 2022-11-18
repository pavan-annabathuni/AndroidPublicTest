package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.Local.Entity.MyCropDataEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.CropInformationEntityMapper
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

class MyCropSyncer:SyncInterface {
    override fun getSyncKey():  Preferences.Key<String> = SyncKey.MY_CROP_MASTER
    override fun getRefreshRate():Int = SyncRate.getRefreshRate(getSyncKey())

    fun getMyCrop(account_id: Int): Flow<Resource<List<MyCropDataEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall(account_id)
            }
        }
        return getSelectedMyCrop()
    }

    fun getSelectedMyCrop(): Flow<Resource<List<MyCropDataEntity>>> {
        return LocalSource.getMyCrop().map {
            Resource.Success(it)
        }
    }
    private fun makeNetworkCall(account_id:Int) {

        GlobalScope.launch(Dispatchers.IO) {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null)
                NetworkSource.getMyCrop2(headerMap,account_id)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertMyCrop(
                                    MyCropEntityMapper().toEntityList(it.data?.data!!)
                                  //  CropInformationEntityMapper().toEntityList(it.data?.data!!)
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