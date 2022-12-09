package com.waycool.data.Sync

import androidx.datastore.preferences.core.stringPreferencesKey

object SyncKey {

    //Language Master
    val LANGUAGE_MASTER = stringPreferencesKey("language_master")

    //Tags
    val TAGS_MASTER = stringPreferencesKey("tags")

    //Crops Master
    val CROPS_MASTER = stringPreferencesKey("crops")

    //Vans Category
    val VANS_CATEGORY_MASTER = stringPreferencesKey("vans_category")

    //User Details
    val USER_DETAILS = stringPreferencesKey("user_details")

    //Modules Master
    val MODULES_MASTER = stringPreferencesKey("modules")

    //Modules Master
    val ADD_CROP_TYPE = stringPreferencesKey("add_crop_type")

    //Modules Master
    val SOIL_TEST_HISTORY = stringPreferencesKey("soil_type_history")

    //Modules Master
    val DASH_BOARD = stringPreferencesKey("dash_board")

    //AI crop Health History
    val AI_CROP_HISTORY = stringPreferencesKey("ai_crop_history")

    //Weather
    val WEATHER = stringPreferencesKey("weather")

    //Crops Category Master
    val CROPS_CATEGORY_MASTER = stringPreferencesKey("crops_category")

    //Pest Disease Master
    val PEST_DISEASE_MASTER = stringPreferencesKey("pest_disease")

    val CROP_INFORMATION_MASTER = stringPreferencesKey("crop_information")

    val MY_CROP_MASTER = stringPreferencesKey("my_crop")

    val MANDI_PRICE = stringPreferencesKey("mandi_price")

    val APP_TRANSLATIONS = stringPreferencesKey("app_translations")

}