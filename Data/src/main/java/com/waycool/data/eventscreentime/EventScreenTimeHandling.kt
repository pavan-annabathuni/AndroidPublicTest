package com.waycool.data.eventscreentime

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object EventScreenTimeHandling {
    fun calculateScreenTime(screenName:String) {
        var firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }

    }
}
object EventClickHandling{
    fun calculateClickEvent(itemName:String) {
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        }
    }
}

object EventItemClickHandling{
    fun calculateItemClickEvent(itemName:String,bundle: Bundle) {
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(itemName, bundle)
        }
    }
}
