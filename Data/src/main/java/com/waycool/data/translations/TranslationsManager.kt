package com.waycool.data.translations

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.waycool.data.Local.Entity.AppTranslationsEntity
import com.waycool.data.Sync.syncer.AppTranslationsSyncer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TranslationsManager {
    fun init() {
        GlobalScope.launch(Dispatchers.IO) {
            AppTranslationsSyncer.getData("")
        }
    }

    fun refreshTranslations() {
        GlobalScope.launch {
            AppTranslationsSyncer.getData("")
        }
    }

    fun getStringAsLiveData(stringId: String):LiveData<AppTranslationsEntity>?{
        return AppTranslationsSyncer().getDataAsFlow(stringId)?.asLiveData()
    }

    suspend fun getString(stringId: String): String = withContext(Dispatchers.IO) {
        return@withContext AppTranslationsSyncer.getData(stringId)?.appValue ?: ""
    }


    fun loadString(stringId: String, textview: TextView,defaultValue:String?="") {
        GlobalScope.launch(Dispatchers.Main) {
            textview.text = AppTranslationsSyncer.getData(stringId)?.appValue ?: defaultValue
        }
    }

}