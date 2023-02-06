package com.waycool.featurelogin.deeplink

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DeepLinkNavigator {

    fun navigateFromDeeplink(activity: Activity, onSuccessCallback : (pendingDynamicLinkData: PendingDynamicLinkData?) -> Unit){
        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }else{
                Firebase.dynamicLinks
                    .getDynamicLink(activity.intent)
                    .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                        onSuccessCallback(pendingDynamicLinkData)
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "getDynamicLink:onFailure", e)
                    }
            }
        }
    }


    const val NEWS_ARTICLE = "newsandarticlesfullscreen"

}