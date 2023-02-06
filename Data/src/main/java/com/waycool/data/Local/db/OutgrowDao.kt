package com.waycool.data.Local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waycool.data.Local.Entity.*
import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.Entity.TagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutgrowDao {

    //Tags
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTags(word: List<TagsEntity>)

    @Query("SELECT * FROM tags")
    fun getTags(): Flow<List<TagsEntity>?>

    @Query("DELETE FROM tags")
    fun deleteTags()

    //Crop Master
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCropMaster(crops: List<CropMasterEntity>)

    //AI Crop History

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(crops: List<AiCropHistoryEntity>)

    @Query("SELECT * FROM ai_history WHERE crop_name LIKE '%' || :search || '%' ORDER BY id Desc")
    fun getAiHistory(search: String? = ""): Flow<List<AiCropHistoryEntity>?>

    //Soil Testing History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSoilTestHistory(history: List<SoilTestHistoryEntity>)

    @Query("SELECT * FROM soil_test_history WHERE plot_no LIKE '%' || :search || '%' ORDER BY id Desc")
    fun getSoilTestHistory(search: String? = ""): Flow<List<SoilTestHistoryEntity>?>


    @Query("SELECT * FROM crop_master WHERE crop_name LIKE '%' || :search || '%' ORDER BY crop_name Asc")
    fun getCropMaster(search: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE pest_disease_info = 1 AND crop_name LIKE '%' || :search || '%' ORDER BY crop_name Asc")
    fun getCropsPestDiseases(search: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE ai_crop_health = 1 AND crop_name LIKE '%' || :searchQuery || '%' ORDER BY crop_name Asc")
    fun getCropsAiCrop(searchQuery: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE crop_info = 1 AND crop_name LIKE '%' || :searchQuery || '%' ORDER BY crop_name Asc")
    fun getCropsInfo(searchQuery: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE water_model = 1 AND crop_name LIKE '%' || :searchQuery || '%' ORDER BY crop_name Asc")
    fun getIrrigationCrops(searchQuery: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("DELETE FROM crop_master")
    fun deleteCropMaster()

    //Pest Diseases
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPestDiseases(crops: List<PestDiseaseEntity>)

    @Query("SELECT * FROM pest_disease WHERE crop_id = :cropId ORDER BY disease_name Asc")
    fun getDiseasesForCrop(cropId: Int): Flow<List<PestDiseaseEntity>>

    @Query("SELECT * FROM pest_disease WHERE disease_id = :diseaseId ORDER BY disease_name Asc")
    fun getSelectedDisease(diseaseId: Int): Flow<PestDiseaseEntity>

    @Query("SELECT * FROM pest_disease WHERE disease_id = :diseaseId ORDER BY disease_name Asc")
    suspend fun getEntityDisease(diseaseId: Int): PestDiseaseEntity?

    @Query("DELETE FROM pest_disease")
    fun deletePestDiseases()

    //cropInformation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCropInformation(crops_information: List<CropInformationEntityData>)

    @Query("SELECT * FROM crop_information WHERE crop_id = :cropId")
    fun getCropInformation(cropId: Int): Flow<List<CropInformationEntityData>>

    @Query("DELETE FROM crop_information")
    fun deleteCropInformation()

    //MyCrops
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyCrops(My_crop: List<MyCropDataEntity>)

    @Query("SELECT * FROM My_crop")
    fun getMyCrops(): Flow<List<MyCropDataEntity>>

    @Query("DELETE FROM My_crop WHERE id=:id")
    fun getDeleteMyCrops(id: Int)

    @Query("DELETE FROM My_crop")
    fun getDeleteAllMyCrops()

    //Translations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTranslations(translations: List<AppTranslationsEntity>)

    @Query("SELECT * FROM app_translations WHERE appKey = :appkey")
    suspend fun getTranslation(appkey: String): AppTranslationsEntity?

    @Query("SELECT * FROM app_translations WHERE appKey = :appkey")
    fun getTranslationFlow(appkey: String): Flow<AppTranslationsEntity>?


    //My Farms
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyFarms(farms: List<MyFarmsEntity>)

    @Query("SELECT * FROM my_farms")
    fun getMyFarms(): Flow<List<MyFarmsEntity>>

    @Query("DELETE FROM my_farms")
    fun deleteAllMyFarms()

    //View Device

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertViewDevice(farms: List<ViewDeviceEntity>)

    @Query("SELECT * FROM my_devices")
    fun getViewDevices(): Flow<List<ViewDeviceEntity>>

    @Query("SELECT * FROM my_devices WHERE farm_id = :farmId ORDER BY model_series DESC")
    fun getViewDevicesByFarm(farmId: Int): Flow<List<ViewDeviceEntity>>

    @Query("DELETE FROM my_devices")
    fun deleteAllDevices()
}