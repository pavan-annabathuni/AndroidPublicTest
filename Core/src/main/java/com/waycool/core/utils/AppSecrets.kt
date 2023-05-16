package com.waycool.core.utils

import com.waycool.core.BuildConfig

object AppSecrets {

    init {
        System.loadLibrary("native-lib")
    }

    fun getBASEURLDebug(): String = BuildConfig.BASE_URL
    external fun getOTPBaseURL(): String
    fun getApiKey(): String = BuildConfig.API_KEY

    external fun getWeatherBaseUrl(): String
    external fun getWeatherApiKey(): String

    external fun getOTPKey(): String
    external fun getOTPTemplateId(): String

    external fun getGeoBaseUrl(): String
    external fun getYoutubeKey(): String
    external fun getChatAppId(): String
    external fun getChatChannelKey(): String
    external fun getAccountKey(): String
    external fun getMapsKey(): String


    fun getHeaderPublic(): Map<String, String> =
        mapOf("x-api-key" to getApiKey(), "Accept" to "application/json, text/plain, */*")


}