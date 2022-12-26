package com.waycool.data.utils

import com.waycool.data.Local.LocalSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SpeechToText {
    suspend fun getLangCode(): String? {
        var langCode:String
        GlobalScope.launch(Dispatchers.Main) {
            langCode = getLangCode() ?: ""
            when (langCode) {
                "en" -> langCode = "en-IN"
                "hi" -> langCode = "hi-IN"
                "ka" -> langCode = "kn-IN"
                "mr" -> langCode = "mr-IN"
                "ta" -> langCode = "ta-IN"
                "te" -> langCode = "te-IN"
            }
//
        }
        return LocalSource.getUserDetailsEntity()?.profile?.langCode

    }
}