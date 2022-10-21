package com.waycool.data.Local.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.waycool.data.Local.Entity.*
import com.waycool.data.Network.NetworkModels.CropVarietyModel

object TypeConverter {

    fun convertLanguageMasterToString(language: List<LanguageMasterEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringToLanguageMaster(s: String): List<LanguageMasterEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<LanguageMasterEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertVansCategoryToString(language: List<VansCategoryEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringToVansCategory(s: String): List<VansCategoryEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<VansCategoryEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertModuleMasterToString(language: List<ModuleMasterEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringToModuleMaster(s: String): List<ModuleMasterEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<ModuleMasterEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }


    fun convertCropCategoryToString(language: List<CropCategoryEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringToCropCategory(s: String): List<CropCategoryEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<CropCategoryEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertAiCropHistoryToString(language: List<AiCropHistoryEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringToAiCropHistory(s: String): List<AiCropHistoryEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<AiCropHistoryEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertUserDetailsToString(language: UserDetailsEntity): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringToUserDetails(s: String): UserDetailsEntity? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<UserDetailsEntity?>() {}.type
        return Gson().fromJson(s, listType)
    }
    fun convertStringToCropVariety(s: String): CropVarietyModel? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<UserDetailsEntity?>() {}.type
        return Gson().fromJson(s, listType)
    }
}