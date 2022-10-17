package com.waycool.data.Sync

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

@SuppressLint("StaticFieldLeak")
object SyncManager {

    private var context: Context? = null
    const val TAG = "Sync"
    private const val SYNC_IT = true
    private const val DONT_SYNC = false

    private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "Sync")


    fun init(context: Context) {
        this.context = context.applicationContext
    }


    suspend fun shouldSync(
        key: Preferences.Key<String>,
        windowInMinutes: Int = 30
    ): Boolean {

        performPrefsSanityCheck()

        val currentSavedValue: String = context?.syncDataStore?.data?.first().let {
            it?.get(key) ?: ""
        }

        if (currentSavedValue.isEmpty())
            return SYNC_IT

        val syncedTime = DateTime.parse(currentSavedValue)
        val syncBlock = syncedTime.plusMinutes(windowInMinutes)

        //Is the current time past the sync block window?
        return if (DateTime.now() >= syncBlock) SYNC_IT else DONT_SYNC
    }


    suspend fun syncSuccess(key: Preferences.Key<String>) {
        performPrefsSanityCheck()
        saveSyncTime(key)
    }


    suspend fun syncFailure(key: Preferences.Key<String>) {
        performPrefsSanityCheck()

        context?.syncDataStore?.edit {
            it.remove(key)
        }
    }

    suspend fun invalidateSync(key: Preferences.Key<String>) {
        performPrefsSanityCheck()
        context?.syncDataStore?.edit {
            it.remove(key)
        }
    }

    suspend fun invalidateAll() {
        performPrefsSanityCheck()

        context?.syncDataStore?.edit {
            it.clear()
        }
    }


    private fun performPrefsSanityCheck() {
        if (context == null)
            throw IllegalStateException("Make sure to init Sync")
    }

    private suspend fun saveSyncTime(
        key: Preferences.Key<String>,
        dateTime: DateTime = DateTime.now()
    ) {
        context?.syncDataStore?.edit {
            it[key] = ISODateTimeFormat.dateTime().print(dateTime)
        }
    }
}