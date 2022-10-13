package com.waycool.featurecrophealth.db

import androidx.room.*
import com.waycool.featurecrophealth.model.cropdetails.Data

@Dao
interface CropDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuotes(quotes: List<Data>)

    @Query("SELECT * FROM details WHERE crop_category_id =crop_category_id ")
    suspend fun getQuotes() :List<Data>

//    @Query("SELECT * WHERE crop_category_id=4  details")
//    fun getAllDetails(categoryid: Int): LiveData<List<Data>>


//
//    @Delete
//    suspend fun deleteDetails(article: Data)
}