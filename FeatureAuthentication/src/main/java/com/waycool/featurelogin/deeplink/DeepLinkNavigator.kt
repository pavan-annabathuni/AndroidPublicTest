package com.waycool.featurelogin.deeplink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

object DeepLinkNavigator {
    fun getDeepLinkAndScreenShot(
        context: Context?,
        shareLayout: ConstraintLayout,
        uriString: String,
        title: String,
        description: String,
        onSuccessCallback:(task: Task<ShortDynamicLink>, uri: Uri?)->Unit)
    {
        val now = Date()
        android.text.format.DateFormat.format("", now)
        val path = context?.externalCacheDir?.absolutePath + "/" + now + ".jpg"
        val bitmap =
            Bitmap.createBitmap(shareLayout.width, shareLayout.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        shareLayout.draw(canvas)
        val imageFile = File(path)
        val outputFile = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputFile)
        outputFile.flush()
        outputFile.close()
        val uri = context?.let { FileProvider.getUriForFile(it, "com.example.outgrow", imageFile) }
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(uriString))
            .setDomainUriPrefix("https://outgrowdev.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(title)
                    .setDescription(description)
                    .build()
            )
            .buildShortDynamicLink().addOnCompleteListener { task ->
                onSuccessCallback(task, uri)
            }


    }

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

    const val DOMAIN_URI_PREFIX="https://outgrow.page.link"
    const val DEFAULT_IMAGE_URL="https://admindev.outgrowdigital.com/img/OutgrowLogo500X500.png"

    const val INVITE_SHARE_LINK="https://outgrow.page.link/invite"
    const val WEATHER_SHARE_LINK="https://outgrow.page.link/weathershare"



}