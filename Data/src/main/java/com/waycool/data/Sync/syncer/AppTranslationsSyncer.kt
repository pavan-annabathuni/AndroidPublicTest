package com.waycool.data.Sync.syncer

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.AppTranslationsEntity
import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.AppTranslationsEntityMapper
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

object AppTranslationsSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.APP_TRANSLATIONS

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    suspend fun getData(appKey: String): AppTranslationsEntity? {
        if (isSyncRequired()) {
            makeNetworkCall()
        }
        return getDataFromLocal(appKey)
    }

  private  suspend fun getDataFromLocal(appKey: String): AppTranslationsEntity? {
        return LocalSource.getTranslationForString(appKey)
    }

   fun getDataAsFlow(appKey: String): Flow<AppTranslationsEntity>? {
        return getDataFromLocalAsFlow(appKey)
    }

   private  fun getDataFromLocalAsFlow(appKey: String): Flow<AppTranslationsEntity>? {
        return LocalSource.getTranslationForStringInFlow(appKey)
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            setSyncStatus(true)
            NetworkSource.getAppTranslations()
                .collect {
                    when (it) {
                        is Resource.Success -> {
                            LocalSource.insertTranslations(
                                AppTranslationsEntityMapper().toEntityList(
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