package com.waycool.data.Sync.syncer

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.LangugeMasterEntityMapper
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

class LanguageSyncer : SyncInterface {


    override fun getSyncKey(): Preferences.Key<String> = SyncKey.LANGUAGE_MASTER

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getData(): Flow<Resource<List<LanguageMasterEntity>>> {

        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal() : Flow<Resource<List<LanguageMasterEntity>>>
    {

       return LocalSource.getLanguageMaster()?.map {
            if (it != null) {
                Log.d("LanguageSync","Success: $it.toString())")
                (Resource.Success(it))
            } else {
                Log.d("LanguageSync","Fail: $it.toString())")
                (Resource.Error(""))
            }
        }?: emptyFlow()
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            NetworkSource.getLanguageMaster()
                .collect {
                    when (it) {
                        is Resource.Success -> {
                            LocalSource.insertLanguageMaster(
                                LangugeMasterEntityMapper().toEntityList(
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