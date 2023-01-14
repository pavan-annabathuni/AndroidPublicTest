package com.waycool.data.Local

import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Local.Entity.*
import com.waycool.data.Local.db.OutgrowDB

import kotlinx.coroutines.flow.Flow


object LocalSource {

    private val outgrowDao = OutgrowDB.getDatabase().outgrowDao()

    fun insertTags(tags: List<TagsEntity>) {
        outgrowDao.insertTags(tags)
    }

    fun getTags(): Flow<List<TagsEntity>?> = outgrowDao.getTags()


    suspend fun setIsFirst(first: Boolean) = DataStoreManager.saveIsFirstTime(first)

    suspend fun getIsFirst(): Boolean = DataStoreManager.isFirstTime()

    suspend fun saveFcmToken(token: String) = DataStoreManager.saveFcmToken(token)


    suspend fun getFcmToken() = DataStoreManager.getFcmToken()

    suspend fun saveDeviceDetails(deviceManufacturer: String, deviceName: String) =
        DataStoreManager.saveDeviceDetails(deviceManufacturer, deviceName)


    suspend fun getDeviceManufacturer() = DataStoreManager.getDeviceManufacturer()
    suspend fun getDeviceModel() = DataStoreManager.getDeviceModel()

    suspend fun setIsLoggedIn(isLoggedIn: Boolean) = DataStoreManager.saveIsLoggedIn(isLoggedIn)


    suspend fun setUserToken(userToken: String?) = DataStoreManager.saveUserToken(userToken)

    suspend fun getIsLoggedIn() = DataStoreManager.isLoggedIn()

    suspend fun getUserToken() = DataStoreManager.getUserToken()

    suspend fun getHeaderMapSanctum(): Map<String, String>? {
        if (getUserToken() != null) {
            return mapOf(
                "Authorization" to "Bearer ${getUserToken()}",
                "Accept" to "application/json"
            )
        }
        return null
    }

    suspend fun insertLanguageMaster(languageList: List<LanguageMasterEntity>) {
        DataStoreManager.insertLanguageMaster(languageList)
    }

    fun getLanguageMaster(): Flow<List<LanguageMasterEntity>?>? =
        DataStoreManager.getLanguageMaster()

    fun insertCropMaster(cropMasterList: List<CropMasterEntity>) {
        outgrowDao.insertCropMaster(cropMasterList)
    }
    fun insertHistory(aiCropHistory: List<AiCropHistoryEntity>) {
        outgrowDao.insertHistory(aiCropHistory)
    }

    fun getCropMaster(searchQuery: String? = ""): Flow<List<CropMasterEntity>?> {
        return outgrowDao.getCropMaster(searchQuery)
    }
    fun getAiHistory(searchQuery: String? = ""): Flow<List<AiCropHistoryEntity>?> {
        return outgrowDao.getAiHistory(searchQuery)
    }

    fun getCropsPestDiseases(searchQuery: String? = ""): Flow<List<CropMasterEntity>?> {
        return outgrowDao.getCropsPestDiseases(searchQuery)
    }

    fun getCropsAiCropHealth(searchQuery: String? = ""): Flow<List<CropMasterEntity>?> {
        return outgrowDao.getCropsAiCrop(searchQuery)
    }

    fun getCropsInfo(searchQuery: String? = ""): Flow<List<CropMasterEntity>?> {
        return outgrowDao.getCropsInfo(searchQuery)
    }
    fun getIrrigationCrops(searchQuery: String? = ""): Flow<List<CropMasterEntity>?> {
        return outgrowDao.getIrrigationCrops(searchQuery)
    }

    suspend fun insertVansCategory(vansCategory: List<VansCategoryEntity>) {
        DataStoreManager.insertVansCategory(vansCategory)
    }

    fun getVansCategory(): Flow<List<VansCategoryEntity>>? =
        DataStoreManager.getVansCategory()

    suspend fun insertModuleMaster(moduleMaster: List<ModuleMasterEntity>) {
        DataStoreManager.insertModuleMaster(moduleMaster)
    }

    suspend fun insertAddCropType(moduleMaster: List<AddCropTypeEntity>) {
        DataStoreManager.insertAddCropType(moduleMaster)
    }

    suspend fun insertSoilTestHistory(moduleMaster: List<SoilTestHistoryEntity>) {
        DataStoreManager.insertSoilTestHistory(moduleMaster)
    }

    suspend fun insertDashboard(moduleMaster: DashboardEntity) {
        DataStoreManager.insertDashboard(moduleMaster)
    }

    fun getModuleMaster(): Flow<List<ModuleMasterEntity>>? {
        return DataStoreManager.getModuleMaster()
    }

    fun getAddCropType(): Flow<List<AddCropTypeEntity>>? {
        return DataStoreManager.getAddCropType()
    }

    fun getSoilTestHistory(): Flow<List<SoilTestHistoryEntity>>? {
        return DataStoreManager.getSoilTestHistory()
    }

    fun getDashBoard(): Flow<DashboardEntity>? {
        return DataStoreManager.getDashBoard()
    }


    suspend fun insertCropCategoryMaster(cropCategory: List<CropCategoryEntity>) {
        DataStoreManager.insertCropCategory(cropCategory)
    }

    fun getCropCategoryMaster(): Flow<List<CropCategoryEntity>>? {
        return DataStoreManager.getCropCategory()
    }

    suspend fun insertAiCropHistory(aiCropHistory: List<AiCropHistoryEntity>) {
        DataStoreManager.insertAiCropHistory(aiCropHistory)
    }

    fun getAiCropHistory(): Flow<List<AiCropHistoryEntity>>? {
        return DataStoreManager.getAiCropHistory()
    }

    suspend fun saveSelectedLanguage(langCode: String, langId: Int, language: String) {
        DataStoreManager.saveSelectedLanguage(langCode, langId, language)
    }

    suspend fun getLanguageCode() = DataStoreManager.getSelectedLanguageCode()

    suspend fun getLanguage() = DataStoreManager.getSelectedLanguage()
    suspend fun getLanguageId() = DataStoreManager.getSelectedLanguageID()

    suspend fun insertUserDetails(userDetails: UserDetailsEntity) {
        DataStoreManager.insertUserDetails(userDetails)
    }

    fun getUserDetails(): Flow<UserDetailsEntity>? {
        return DataStoreManager.getUserDetails()
    }

    suspend fun getUserDetailsEntity(): UserDetailsEntity? {
        return DataStoreManager.getUserDetailsEntity()
    }

    fun insertPestDiseases(pestList: List<PestDiseaseEntity>) {
        outgrowDao.insertPestDiseases(pestList)
    }

    fun getDiseasesForCrop(cropId: Int) = outgrowDao.getDiseasesForCrop(cropId)

    fun getSelectedDiseasesForCrop(diseaseId: Int) = outgrowDao.getSelectedDisease(diseaseId)

    suspend fun getSelectedDiseaseEntity(diseaseId: Int) = outgrowDao.getEntityDisease(diseaseId)


    suspend fun insertWeatherData(weather: WeatherMasterEntity, lat: String, lon: String) {
        DataStoreManager.insertWeather(weather, lat, lon)
    }

    fun getWeather(lat: String, lon: String) = DataStoreManager.getWeather(lat, lon)

    fun insertCropInformation(CropInformation: List<CropInformationEntityData>) {
        outgrowDao.insertCropInformation(CropInformation)
    }

    fun getCropInformation(crop_id: Int) = outgrowDao.getCropInformation(crop_id)

    fun insertMyCrop(MyCrops: List<MyCropDataEntity>) {
        outgrowDao.getDeleteAllMyCrops()
        outgrowDao.insertMyCrops(MyCrops)
    }

    fun getMyCrop() = outgrowDao.getMyCrops()

    suspend fun deleteMyCrop(id: Int) = outgrowDao.getDeleteMyCrops(id)

    fun insertTranslations(translations: List<AppTranslationsEntity>) {
        outgrowDao.insertTranslations(translations)
    }

    suspend fun getTranslationForString(appKey: String): AppTranslationsEntity? {
        return outgrowDao.getTranslation(appKey)
    }


    fun insertMyFarms(farms: List<MyFarmsEntity>) {
        outgrowDao.deleteAllMyFarms()
        outgrowDao.insertMyFarms(farms)
    }

    fun getMyFarms(): Flow<List<MyFarmsEntity>> {
        return outgrowDao.getMyFarms()
    }
    fun deleteAllMyCrops() = outgrowDao.getDeleteAllMyCrops()
    fun deleteTags() = outgrowDao.deleteTags()
    fun deleteCropMaster() = outgrowDao.deleteCropMaster()
    fun deletePestDisease() = outgrowDao.deletePestDiseases()
    fun deleteCropInformation() = outgrowDao.deleteCropInformation()
    fun deleteMyFarms()= outgrowDao.deleteAllMyFarms()

}