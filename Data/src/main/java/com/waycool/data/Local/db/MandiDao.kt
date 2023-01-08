package com.waycool.data.Local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waycool.data.Local.Entity.MandiKeysEntity
import com.waycool.data.Local.Entity.MandiRecordEntity

@Dao
interface MandiDao {

    @Query("SELECT * FROM MandiRecordEntity WHERE crop_category LIKE '%' || :crop_category || '%' AND crop LIKE '%' || :crop || '%' AND state LIKE '%' || :stateIndia || '%' ORDER BY avg_price ASC ")
    fun getMandiRecordsForFiltersOrderByPrice(
        crop_category: String = "",
        crop: String = "",
        stateIndia: String = ""
    ): PagingSource<Int, MandiRecordEntity>

    @Query("SELECT * FROM MandiRecordEntity WHERE crop_category LIKE '%' || :crop_category || '%' AND crop LIKE '%' || :crop || '%' AND state LIKE '%' || :stateIndia || '%' ORDER BY distance ASC ")
    fun getMandiRecordsForFiltersOrderByDistance(
        crop_category: String = "",
        crop: String = "",
        stateIndia: String = ""
    ): PagingSource<Int, MandiRecordEntity>


    @Query("SELECT * FROM MandiRecordEntity WHERE crop LIKE '%' || :search || '%' OR market LIKE '%' || :search || '%' ORDER BY crop ASC")
    fun getMandiRecordsForSearch(
        search: String = ""
    ): PagingSource<Int, MandiRecordEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMandiRecords(quotes: List<MandiRecordEntity>)

    @Query("DELETE FROM MandiRecordEntity")
    suspend fun deleteMandiRecords()

    @Query("SELECT * FROM MandiKeysEntity WHERE id =:id")
    suspend fun getMandiRemoteKeys(id: String): MandiKeysEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllMandiRemoteKeys(remoteKeys: List<MandiKeysEntity>)

    @Query("DELETE FROM mandikeysentity")
    suspend fun deleteAllMandiRemoteKeys()
}