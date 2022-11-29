package com.waycool.core.utils

object AppSecrets {

    init {
        System.loadLibrary("native-lib")
    }

    external fun getBASEURLDebug(): String
    external fun getOTPBaseURL(): String
    external fun getApiKey(): String

    external fun getWeatherBaseUrl(): String
    external fun getWeatherApiKey(): String

    external fun getOTPKey(): String
    external fun getOTPTemplateId(): String

    external fun getGeoBaseUrl():String
    external fun getYoutubeKey(): String
    external fun getChatAppId(): String
    external fun getChatChannelKey():String
    external fun getAccountKey(): String
    external fun getMapsKey():String


    fun getHeaderPublic(): Map<String, String> =
        mapOf("x-api-key" to getApiKey(), "Accept" to "application/json, text/plain, */*")


}