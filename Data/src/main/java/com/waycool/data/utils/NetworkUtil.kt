package com.waycool.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtil {
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0

    private fun isInternetAvailable(context: Context?): Int {
        var result = TYPE_NOT_CONNECTED
        var connectivityManager =
            context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE)
        if (connectivityManager != null) {
            connectivityManager = connectivityManager as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return TYPE_MOBILE
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities)
                    ?: return TYPE_NOT_CONNECTED
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> TYPE_WIFI
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> TYPE_MOBILE
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> TYPE_MOBILE
                else -> TYPE_NOT_CONNECTED
            }
        }
        return result
    }

//    private fun getConnectivityStatus(context: Context?): Int {
//        val cm = context
//            ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork = cm.activeNetworkInfo
//        if (null != activeNetwork && activeNetwork.isConnectedOrConnecting) {
//            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
//            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
//        }
//        return TYPE_NOT_CONNECTED
//    }

    fun getConnectivityStatusString(context: Context?): Int {
        val conn = isInternetAvailable(context)
        var status = 0
        if (conn == TYPE_WIFI) {
            status = TYPE_WIFI
        } else if (conn == TYPE_MOBILE) {
            status = TYPE_MOBILE
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = TYPE_NOT_CONNECTED
        }
        return status
    }
}