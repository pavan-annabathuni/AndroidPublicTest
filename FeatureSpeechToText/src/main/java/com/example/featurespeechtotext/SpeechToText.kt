package com.example.featurespeechtotext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.Lang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult as startActivityForResult1


object SpeechToText {
     const val REQUEST_CODE_SPEECH_INPUT = 11019
 //   fun speechToText():String {
//        var langCode:String
//         GlobalScope.launch(Dispatchers.Main) {
//             langCode = getLangCode() ?: ""
//             when (langCode) {
//                 "en" -> langCode = "en-IN"
//                 "hi" -> langCode = "hi-IN"
//                 "ka" -> langCode = "kn-IN"
//                 "mr" -> langCode = "mr-IN"
//                 "ta" -> langCode = "ta-IN"
//                 "te" -> langCode = "te-IN"
//             }
////
//         }
//     return langCode
//        }

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
