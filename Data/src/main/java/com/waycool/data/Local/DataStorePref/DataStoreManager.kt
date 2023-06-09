package com.waycool.data.Local.DataStorePref

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.waycool.data.Local.Entity.*
import com.waycool.data.Local.utils.TypeConverter
import kotlinx.coroutines.flow.*

@SuppressLint("StaticFieldLeak")
object DataStoreManager {

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
//        this.context ?: synchronized(this) {
//            context.applicationContext
        Log.d("DataStore Init", "DataStore Initialized")
//        }

    }

    private fun performPrefsSanityCheck() {
        if (context == null)
            throw IllegalStateException("Make sure to init DataStoreManager")
    }

    private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = StoreName.LANGUAGE)
    private val Context.userPreferences: DataStore<Preferences> by preferencesDataStore(name = StoreName.USER_PREFS)
    private val Context.vansCategory: DataStore<Preferences> by preferencesDataStore(name = StoreName.VANS_CATEGORY)
    private val Context.moduleMaster: DataStore<Preferences> by preferencesDataStore(name = StoreName.MODULE_MASTER)
    private val Context.addCropType: DataStore<Preferences> by preferencesDataStore(name = StoreName.ADD_CROP_TYPE)
    private val Context.soilTestHistory: DataStore<Preferences> by preferencesDataStore(name = StoreName.SOIL_TEST_HISTORY)
    private val Context.dashBoard: DataStore<Preferences> by preferencesDataStore(name = StoreName.DASH_BOARD)
    private val Context.cropCategory: DataStore<Preferences> by preferencesDataStore(name = StoreName.CROP_CATEGORY)
    private val Context.aiCropHistory: DataStore<Preferences> by preferencesDataStore(name = StoreName.AI_CROP_HISTORY)
    private val Context.weather: DataStore<Preferences> by preferencesDataStore(name = StoreName.WEATHER)
    private val Context.spotLight: DataStore<Preferences> by preferencesDataStore(name = StoreName.SPOTLIGHT)


    suspend fun insertLanguageMaster(language: List<LanguageMasterEntity>) {
        performPrefsSanityCheck()
        context?.languageDataStore?.edit {
            it[StoreKey.LANGUAGE_MASTER] = TypeConverter.convertLanguageMasterToString(language)
        }

    }

    fun getLanguageMaster(): Flow<List<LanguageMasterEntity>>? {
        performPrefsSanityCheck()
        return context?.languageDataStore?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.LANGUAGE_MASTER]
                Log.d("lang123", string ?: "null str")
                string?.let { TypeConverter.convertStringToLanguageMaster(string) } ?: emptyList()
            }
    }


    suspend fun saveSelectedLanguage(languageCode: String?, langId: Int?,language:String?) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.LANGUAGE_CODE] = languageCode ?: ""
            it[StoreKey.LANGUAGE_ID] = langId ?: -1
            it[StoreKey.LANGUAGE]=language ?: ""
        }
    }

    suspend fun getSelectedLanguageCode(): String? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.LANGUAGE_CODE)
            }
    }

    suspend fun getSelectedLanguage(): String? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.LANGUAGE)
            }
    }

    suspend fun getSelectedLanguageID(): Int? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.LANGUAGE_ID)
            }
    }

    suspend fun saveIsFirstTime(isfirst: Boolean) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.IS_FIRST_TIME] = isfirst
        }
    }

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun isFirstTime(): Boolean {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.IS_FIRST_TIME) ?: true
            }
    }

    suspend fun isLoggedIn(): Boolean {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.IS_LOGGED_IN) ?: false
            }
    }

    suspend fun saveFcmToken(token: String) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.FCM_TOKEN] = token
        }
    }

    suspend fun getFcmToken(): String {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.FCM_TOKEN) ?: ""
            }
    }

    suspend fun saveDeviceDetails(deviceManufacturer: String, deviceName: String) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.DEVICE_MANUFACTURER] = deviceManufacturer
            it[StoreKey.DEVICE_MODEL] = deviceName
        }
    }

    suspend fun getDeviceManufacturer(): String {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.DEVICE_MANUFACTURER) ?: ""
            }
    }

    suspend fun getDeviceModel(): String {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.DEVICE_MODEL) ?: ""
            }
    }

    suspend fun saveUserToken(userToken: String?) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            if (userToken == null)
                it.remove(StoreKey.USER_TOKEN)
            else
                it[StoreKey.USER_TOKEN] = userToken
        }
    }

    suspend fun getUserToken(): String? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.USER_TOKEN)
            }
    }

    suspend fun getUserTokenFlow(): Flow<String?>? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.map{
                it[StoreKey.USER_TOKEN]
            }
    }

    suspend fun setMobileNumber(mobileNo: String) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.MOBILE_NUMBER] = mobileNo
        }
    }

    suspend fun getMobileNumber(): String? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("UserPref: ", exception.toString())
            }
            ?.first().let {
                it?.get(StoreKey.MOBILE_NUMBER)
            }
    }

    suspend fun insertVansCategory(vansCategoryEntity: List<VansCategoryEntity>) {
        performPrefsSanityCheck()
        context?.vansCategory?.edit {
            it[StoreKey.VANS_CATEGORY] =
                TypeConverter.convertVansCategoryToString(vansCategoryEntity)
            Log.d("Languaga", "SavedLanguaga")
        }
    }

    fun getVansCategory(): Flow<List<VansCategoryEntity>>? {
        performPrefsSanityCheck()
        return context?.vansCategory?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.VANS_CATEGORY]
                string?.let { TypeConverter.convertStringToVansCategory(string) } ?: emptyList()
            }
    }

    suspend fun insertModuleMaster(moduleMaster: List<ModuleMasterEntity>) {
        performPrefsSanityCheck()
        context?.moduleMaster?.edit {
            it[StoreKey.MODULE_MASTER] =
                TypeConverter.convertModuleMasterToString(moduleMaster)
            Log.d("Languaga", "SavedLanguaga")
        }
    }
    suspend fun insertAddCropType(moduleMaster: List<AddCropTypeEntity>) {
        performPrefsSanityCheck()
        context?.addCropType?.edit {
            it[StoreKey.ADD_CROP_TYPE] =
                TypeConverter.convertStringToAddCropTypeString(moduleMaster)
            Log.d("Languaga", "SavedLanguaga")
        }
    }
    suspend fun insertSoilTestHistory(moduleMaster: List<SoilTestHistoryEntity>) {
        performPrefsSanityCheck()
        context?.soilTestHistory?.edit {
            it[StoreKey.SOIL_TEST_HISTORY] =
                TypeConverter.convertSoilTestHistoryString(moduleMaster)
            Log.d("Languaga", "SavedLanguaga")
        }
    }
    suspend fun insertDashboard(moduleMaster: DashboardEntity) {
        performPrefsSanityCheck()
        context?.dashBoard?.edit {
            it[StoreKey.DASH_BOARD] =
                TypeConverter.convertDashBoardString(moduleMaster)
            Log.d("Languaga", "SavedLanguaga")
        }
    }

    fun getModuleMaster(): Flow<List<ModuleMasterEntity>>? {
        performPrefsSanityCheck()
        return context?.moduleMaster?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.MODULE_MASTER]
                string?.let { TypeConverter.convertStringToModuleMaster(string) } ?: emptyList()
            }

    }
    fun getAddCropType(): Flow<List<AddCropTypeEntity>>? {
        performPrefsSanityCheck()
        return context?.addCropType?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.ADD_CROP_TYPE]
                string?.let {
                    TypeConverter.convertStringToAddCropType(string) } ?: emptyList()
            }

    }
    fun getSoilTestHistory(): Flow<List<SoilTestHistoryEntity>>? {
        performPrefsSanityCheck()
        return context?.soilTestHistory?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.SOIL_TEST_HISTORY]
                string?.let {
                    TypeConverter.convertStringSoilTestHistory(string) } ?: emptyList()
            }

    }
    fun getDashBoard(): Flow<DashboardEntity>? {
        performPrefsSanityCheck()
        return context?.dashBoard?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.DASH_BOARD]
                string?.let {
                    TypeConverter.convertStringDashboard(string) } ?: DashboardEntity()
            }

    }

    suspend fun insertCropCategory(cropCategory: List<CropCategoryEntity>) {
        performPrefsSanityCheck()
        context?.cropCategory?.edit {
            it[StoreKey.CROP_CATEGORY] =
                TypeConverter.convertCropCategoryToString(cropCategory)
            Log.d("Languaga", "SavedLanguaga")
        }
    }

    fun getCropCategory(): Flow<List<CropCategoryEntity>>? {
        performPrefsSanityCheck()
        return context?.cropCategory?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.CROP_CATEGORY]
                string?.let { TypeConverter.convertStringToCropCategory(string) } ?: emptyList()
            }

    }

    suspend fun insertAiCropHistory(cropCategory: List<AiCropHistoryEntity>) {
        performPrefsSanityCheck()
        context?.aiCropHistory?.edit {
            it[StoreKey.AI_CROP_HISTORY] =
                TypeConverter.convertAiCropHistoryToString(cropCategory)
            Log.d("Languaga", "SavedLanguaga")
        }
    }

    fun getAiCropHistory(): Flow<List<AiCropHistoryEntity>>? {
        performPrefsSanityCheck()
        return context?.aiCropHistory?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.AI_CROP_HISTORY]
                string?.let { TypeConverter.convertStringToAiCropHistory(string) } ?: emptyList()
            }

    }


    suspend fun insertUserDetails(userDetailsEntity: UserDetailsEntity) {
        performPrefsSanityCheck()
        context?.userPreferences?.edit {
            it[StoreKey.USER_DETAILS] =
                TypeConverter.convertUserDetailsToString(userDetailsEntity)
            Log.d("Languaga", "SavedLanguaga")
        }
    }

    fun getUserDetails(): Flow<UserDetailsEntity>? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[StoreKey.USER_DETAILS]
                string?.let { TypeConverter.convertStringToUserDetails(string) } ?: UserDetailsEntity()
            }

    }

    suspend fun getUserDetailsEntity(): UserDetailsEntity? {
        performPrefsSanityCheck()
        return context?.userPreferences?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.first().let {
                val string = it?.get(StoreKey.USER_DETAILS)
                string?.let { TypeConverter.convertStringToUserDetails(string) }
                    ?: UserDetailsEntity()
            }

    }

    suspend fun insertWeather(weather: WeatherMasterEntity,lat:String,lon:String) {
        performPrefsSanityCheck()
        context?.weather?.edit {
            it[stringPreferencesKey("weather_$lat$lon")] =
                TypeConverter.convertWeatherToString(weather)
            Log.d("Languaga", "SavedLanguaga")
        }
    }

     fun getWeather(lat:String,lon:String): Flow<WeatherMasterEntity>? {
        performPrefsSanityCheck()
        return context?.weather?.data
            ?.catch { exception ->
                Log.d("languageDataStore: ", exception.toString())
            }
            ?.map {
                val string = it[stringPreferencesKey("weather_$lat$lon")]
                string?.let { TypeConverter.convertStringToWeather(string) }
                    ?: WeatherMasterEntity()
            }
    }
    suspend fun save(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context?.spotLight?.edit { firstTime ->
            firstTime[dataStoreKey] = value
        }
    }
    suspend fun read(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context?.spotLight?.data?.first()
        return preferences?.get(dataStoreKey)
    }

    suspend fun clearData(){
         context?.languageDataStore?.edit{
             it.clear()
         }
        context?.userPreferences?.edit {
            it.remove(StoreKey.USER_DETAILS)
            it.remove(StoreKey.USER_TOKEN)
            it.remove(StoreKey.MOBILE_NUMBER)
            it.remove(StoreKey.IS_LOGGED_IN)
        }
        context?.vansCategory?.edit {
            it.clear()
        }
        context?.moduleMaster?.edit {
            it.clear()
        }
        context?.addCropType?.edit {
            it.clear()
        }
        context?.soilTestHistory?.edit {
            it.clear()
        }
        context?.aiCropHistory?.edit {
            it.clear()
        }
        context?.weather?.edit {
            it.clear()
        }
        context?.cropCategory?.edit {
            it.clear()
        }
    }
}