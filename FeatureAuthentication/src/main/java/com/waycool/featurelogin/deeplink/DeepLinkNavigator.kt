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
    const val NEWS_LIST="newslist"
    const val RATING = "rating"
    const val CALL = "call"

    const val PROFILE="profile"

    const val MANDI_GRAPH="mandigraph"
    const val MANDI="mandi"

    const val ADD_CROP="addcrop"
    const val ADD_FARM= "addfarm"

    const val SOIL_TESTING_HISTORY_STATUS="soiltestinghistorystatus"
    const val SOIL_TESTING_HISTORY= "soiltestinghistory"
    const val SOIL_TESTING="soiltesting"

    const val  CROP_INFO="cropinfo"

    const val CROP_HEALTH="crophealth"
    const val PEST_DISEASE_DETAIL="pestdiseasedetail"
    const val PEST_DISEASE="pestdisease"

    const val VIDEO_LIST="videoslist"

    const val CHAT="chat"

    const val DOMAIN_URI_PREFIX="https://outgrowdev.page.link"
    const val DEFAULT_IMAGE_URL="https://admindev.outgrowdigital.com/img/OutgrowLogo500X500.png"

    const val INVITE_SHARE_LINK="http://app.outgrowdigital.com/invite"
    const val WEATHER_SHARE_LINK="https://outgrowdev.page.link/weathershare"



}