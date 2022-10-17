package com.waycool.data.Sync

import androidx.datastore.preferences.core.Preferences
import java.lang.RuntimeException

object SyncRate {

    //Map of Sync Key to Refresh Rate in Minutes
    private val syncRate: Map<Preferences.Key<String>, Int> = mapOf(
        SyncKey.LANGUAGE_MASTER to 30,
        SyncKey.CROPS_MASTER to 60,
        SyncKey.VANS_CATEGORY_MASTER to 60,
        SyncKey.TAGS_MASTER to 60,
        SyncKey.USER_DETAILS to 0,
        SyncKey.MODULES_MASTER to 60,
        SyncKey.AI_CROP_HISTORY to 30,
        SyncKey.WEATHER to 30,
        SyncKey.CROPS_CATEGORY_MASTER to 30,
        SyncKey.PEST_DISEASE_MASTER to 30
    )


    fun getRefreshRate(key: Preferences.Key<String>): Int =
        syncRate[key] ?: throw(RuntimeException("Sync Key Not found"))
}