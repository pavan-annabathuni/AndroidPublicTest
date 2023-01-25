package com.waycool.data.Local.utils

import android.util.Log
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.android.libraries.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.waycool.data.Local.Entity.*
import com.waycool.data.Network.NetworkModels.CheckTokenResponseDTO
import com.waycool.data.Network.NetworkModels.DashBoardDTO
import com.waycool.data.repository.domainModels.CropVarityDomain

@ProvidedTypeConverter
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

    fun convertStringToAddCropType(s: String): List<AddCropTypeEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<AddCropTypeEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertStringToAddCropTypeString(language: List<AddCropTypeEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringSoilTestHistory(s: String): List<SoilTestHistoryEntity>? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<List<SoilTestHistoryEntity>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertSoilTestHistoryString(language: List<SoilTestHistoryEntity>): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
    }

    fun convertStringDashboard(s: String): DashboardEntity? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<DashboardEntity?>() {}.type
        return Gson().fromJson(s, listType)
    }

    fun convertDashBoardString(language: DashboardEntity): String {
        Log.d("TypeConverterTO", language.toString())
        val gson = Gson()
        return gson.toJson(language)
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

    @Throws(JsonParseException::class)
    fun convertStringToCropVariety(s: String): List<CropVarityDomain>?{
        Log.d("TypeConverterFrom", s)
        return try {
            val listType = object : TypeToken<List<CropVarityDomain>?>() {}.type
            Gson().fromJson(s, listType)
        } catch (e: JsonParseException) {
            throw e
    //            Log.d("cropVariety", "convertStringToCropVariety: $e")
        }

    }

    fun convertWeatherToString(w: WeatherMasterEntity): String {
        Log.d("TypeConverterTO", w.toString())
        val gson = Gson()
        return gson.toJson(w)
    }

    fun convertStringToWeather(s: String): WeatherMasterEntity? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<WeatherMasterEntity?>() {}.type
        return Gson().fromJson(s, listType)
    }

    @TypeConverter
    fun convertMyCropToString(w: MyCropDataEntity): String {
        Log.d("TypeConverterTO", w.toString())
        val gson = Gson()
        return gson.toJson(w)
    }

    @TypeConverter
    fun convertStringToMyCrop(s: String): MyCropDataEntity? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<MyCropDataEntity?>() {}.type
        return Gson().fromJson(s, listType)
    }

    @TypeConverter
    fun convertLatLngListToString(latlngs: ArrayList<LatLng>): String {
        val gson = Gson()
        return gson.toJson(latlngs)
    }

    @TypeConverter
    fun convertStringToLatLng(s: String): ArrayList<LatLng> {
        val listType = object : TypeToken<List<LatLng>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    @TypeConverter
    fun convertStringToStringList(s: String?): ArrayList<String>? {
        if (s == null)
            return arrayListOf()
        val listType = object : TypeToken<ArrayList<String>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    @TypeConverter
    fun convertStringListToString(latlngs: ArrayList<String>?): String? {
        val gson = Gson()
        return gson.toJson(latlngs)
    }

    @TypeConverter
    fun convertStringToRangeEntity(s: String?): RangesEntity? {
        if (s == null)
            return RangesEntity()
        val listType = object : TypeToken<RangesEntity?>() {}.type
        return Gson().fromJson(s, listType)
    }

    @TypeConverter
    fun convertRangeEntityToString(range: RangesEntity?): String? {
        val gson = Gson()
        return gson.toJson(range)
    }


    fun convertStringToCheckToken(s: String): CheckTokenResponseDTO? {
        Log.d("TypeConverterFrom", s)
        val listType = object : TypeToken<CheckTokenResponseDTO?>() {}.type
        return Gson().fromJson(s, listType)
    }
}