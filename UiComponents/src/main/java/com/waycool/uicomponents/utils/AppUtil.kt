package com.waycool.uicomponents.utils

import android.content.Context
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.waycool.uicomponents.R
import java.text.ParseException
import java.text.SimpleDateFormat

object AppUtil {
    fun showUrlImage(context: Context?, imageUrl: String?, image: ImageView?) {
        Glide.with(context!!)
            .load("https://outgrow.blob.core.windows.net/outgrowstorage/Prod/CropLogo/Apple.svg")
            .fitCenter()
            .placeholder(R.drawable.ic_outgrow)
            .error(R.drawable.ic_outgrow)
            .into(image!!)
    }

    fun showBannerImage(context: Context?, imageUrl: String?, image: ImageView?) {
        Picasso.get()
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.outgrow_logo_new)
            .into(image)
    }

    fun showImageDrawable(context: Context?, imageUrl: Int, image: ImageView?) {
        Picasso.get().load(imageUrl).into(image)
    }

    fun showFitPicasoo(context: Context?, imageUrl: String?, image: ImageView?) {
        Picasso.get()
            .load(imageUrl)
            .fit()
            .placeholder(R.drawable.outgrow_logo_new)
            .into(image)
    }

    fun showLoadImage(context: Context?, image: ImageView?) {
        try {
            Glide.with(context!!).asGif().load(R.raw.loading).into(image!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun Toast(context: Context?, msg: String?) {
        try {
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProgressVisible(context: Context?, loadingScreen: View, loadingImage: ImageView?) {
        try {
            loadingScreen.visibility = View.VISIBLE
            showLoadImage(context, loadingImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProgressGone(context: Context?, loadingScreen: View) {
        try {
            loadingScreen.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun changeDateFormat(givenDate: String): String {
        var convertedDate = ""
        val input = SimpleDateFormat("yyyy-MM-dd")
        val output = SimpleDateFormat("dd MMM yy")
        try {
            val inputDate = input.parse(givenDate) // parse input
            convertedDate = output.format(inputDate) //get output
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.d("dateCon", convertedDate)
        return convertedDate
    }

    fun handlerSet(handler: Handler, runnable: Runnable?, delayTime: Int) {
        handler.removeCallbacks(runnable!!)
        handler.postDelayed(runnable, delayTime.toLong())
    }
}