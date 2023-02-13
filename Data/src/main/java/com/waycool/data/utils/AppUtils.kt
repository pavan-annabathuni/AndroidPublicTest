package com.waycool.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

object AppUtils {
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

    fun networkErrorStateTranslations(apiErrorHandlingBinding: com.waycool.uicomponents.databinding.ApiErrorHandlingBinding) {
        TranslationsManager().loadString("txt_internet_problem",apiErrorHandlingBinding.tvInternetProblem,"There is a problem with Internet.")
        TranslationsManager().loadString("txt_check_net",apiErrorHandlingBinding.tvCheckInternetConnection,"Please check your Internet connection")
        TranslationsManager().loadString("txt_tryagain",apiErrorHandlingBinding.tvTryAgainInternet,"TRY AGAIN")
    }

    fun translatedToastServerErrorOccurred(context: Context?) {
        CoroutineScope(Dispatchers.Main).launch {
            val toastServerError = TranslationsManager().getString("server_error")
            if(!toastServerError.isNullOrEmpty()){
                context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                    Toast.LENGTH_SHORT
                ) }}
            else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Server Error Occurred",
                Toast.LENGTH_SHORT
            ) }}}
    }
    fun translatedToastLoading(context: Context?) {
        CoroutineScope(Dispatchers.Main).launch {
            val toastLoading = TranslationsManager().getString("loading")
            if(!toastLoading.isNullOrEmpty()){
                context?.let { it1 -> ToastStateHandling.toastWarning(it1,toastLoading,
                    Toast.LENGTH_SHORT
                ) }}
            else {context?.let { it1 -> ToastStateHandling.toastWarning(it1,"Loading",
                Toast.LENGTH_SHORT
            ) }}}
    }
    fun translatedToastCheckInternet(context: Context?) {
        CoroutineScope(Dispatchers.Main).launch {
            val toastCheckInternet = TranslationsManager().getString("check_your_interent")
            if(!toastCheckInternet.isNullOrEmpty()){
                context?.let { it1 -> ToastStateHandling.toastError(it1,toastCheckInternet,
                    Toast.LENGTH_SHORT
                ) }}
            else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please check your internet connection",
                Toast.LENGTH_SHORT
            ) }}}
    }
}