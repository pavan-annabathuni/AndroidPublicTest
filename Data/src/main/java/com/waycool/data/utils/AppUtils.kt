package com.waycool.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
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


}