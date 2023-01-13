package com.waycool.data.Sync

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Sync.SyncManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

interface SyncInterface {

    fun getSyncKey(): Preferences.Key<String>
    fun getRefreshRate(): Int


    suspend fun isSyncRequired(): Boolean {
        return SyncManager.shouldSync(getSyncKey(), getRefreshRate())

    }

    suspend fun setSyncStatus(boolean: Boolean) {
        if (boolean) {
            SyncManager.syncSuccess(getSyncKey())
        } else {
            SyncManager.syncFailure(getSyncKey())
        }
    }

    suspend fun invalidateSync() {
        SyncManager.invalidateSync(getSyncKey())
    }

    fun getLastUpdated(): Flow<String> {
        return SyncManager.getLastUpdated(getSyncKey())
    }
}