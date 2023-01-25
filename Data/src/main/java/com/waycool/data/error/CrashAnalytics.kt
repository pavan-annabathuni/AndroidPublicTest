package com.waycool.data.error

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

object CrashAnalytics {
    fun crashAnalyticsError(message:String){
        Firebase.crashlytics.log(message)
    }
}