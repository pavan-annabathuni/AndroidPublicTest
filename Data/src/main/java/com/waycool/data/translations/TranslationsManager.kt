package com.waycool.data.translations

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.waycool.data.Sync.syncer.AppTranslationsSyncer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TranslationsManager {
    fun init() {
        GlobalScope.launch(Dispatchers.IO) {
            AppTranslationsSyncer().getData("")
        }
    }

    fun refreshTranslations() {
        GlobalScope.launch {
            AppTranslationsSyncer().getData("")
        }
    }

    suspend fun getString(stringId: String): String = withContext(Dispatchers.IO) {
        return@withContext AppTranslationsSyncer().getData(stringId)?.appValue ?: ""
    }


    fun loadString(stringId: String, textview: TextView,defaultValue:String?="") {
        GlobalScope.launch(Dispatchers.Main) {
            textview.text = AppTranslationsSyncer().getData(stringId)?.appValue ?: defaultValue
        }
    }
}