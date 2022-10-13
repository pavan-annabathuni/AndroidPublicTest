package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Local.Entity.ModuleMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.LangugeMasterEntityMapper
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ModuleMasterSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.MODULES_MASTER

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<List<ModuleMasterEntity>>> {

        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal(): Flow<Resource<List<ModuleMasterEntity>>> {

//        emit(Resource.Loading())
      return  LocalSource.getModuleMaster()?.map {
            if (it != null) {
                Resource.Success(it)
            } else {
               Resource.Error("")
            }
        }?: emptyFlow()
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            NetworkSource.getModuleMaster()
                .collect {
                    when (it) {
                        is Resource.Success -> {
                            LocalSource.insertModuleMaster(
                                ModuleMasterEntityMapper().toEntityList(
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