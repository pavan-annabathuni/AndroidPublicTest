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

    @Query("SELECT * FROM crop_master ORDER BY crop_name Asc")
    fun getCropMaster(): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE pest_disease_info = 1 AND crop_name LIKE '%' || :search || '%' ORDER BY crop_name Asc")
    fun getCropsPestDiseases(search: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE ai_crop_health = 1 AND crop_name LIKE '%' || :searchQuery || '%' ORDER BY crop_name Asc")
    fun getCropsAiCrop(searchQuery: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("SELECT * FROM crop_master WHERE crop_info = 1 AND crop_name LIKE '%' || :searchQuery || '%' ORDER BY crop_name Asc")
    fun getCropsInfo(searchQuery: String? = ""): Flow<List<CropMasterEntity>?>

    @Query("DELETE FROM crop_master")
    fun deleteCropMaster()
    //Pest Diseases
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPestDiseases(crops: List<PestDiseaseEntity>)

    @Query("SELECT * FROM pest_disease WHERE crop_id = :cropId ORDER BY disease_name Asc")
    fun getDiseasesForCrop(cropId: Int): Flow<List<PestDiseaseEntity>>

    @Query("SELECT * FROM pest_disease WHERE disease_id = :diseaseId ORDER BY disease_name Asc")
    fun getSelectedDisease(diseaseId: Int): Flow<PestDiseaseEntity>

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

}