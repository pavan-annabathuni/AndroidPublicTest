package com.waycool.data.Local.DataStorePref

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object StoreKey {

    //Language Master
    val LANGUAGE_MASTER = stringPreferencesKey("languageMaster")


    //UserPrefs
    val LANGUAGE_CODE = stringPreferencesKey("languageCode")
    val LANGUAGE_ID = intPreferencesKey("languageId")
    val LANGUAGE = stringPreferencesKey("language")
    val IS_FIRST_TIME = booleanPreferencesKey("isFirstTimeUser")
    val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
    val FCM_TOKEN = stringPreferencesKey("fcmToken")
    val DEVICE_MANUFACTURER = stringPreferencesKey("deviceManufacturer")
    val DEVICE_MODEL = stringPreferencesKey("deviceModel")
    val USER_TOKEN = stringPreferencesKey("userToken")
    val MOBILE_NUMBER = stringPreferencesKey("mobileNumber")
    val USER_DETAILS = stringPreferencesKey("userdetails")


    //Vans Category
    val VANS_CATEGORY = stringPreferencesKey("vanscategory")

    //ModuleMaster
    val MODULE_MASTER = stringPreferencesKey("modulemaster")

    //add crop
    //ModuleMaster
    val ADD_CROP_TYPE = stringPreferencesKey("addcroptype")
    //ModuleMaster
    val SOIL_TEST_HISTORY = stringPreferencesKey("soiltesthistory")

    //CropCategory
    val CROP_CATEGORY = stringPreferencesKey("cropcategory")

    //Ai Crop History
    val AI_CROP_HISTORY = stringPreferencesKey("aicrophistory")

    //weather
    val WEATHER = stringPreferencesKey("weather")
}