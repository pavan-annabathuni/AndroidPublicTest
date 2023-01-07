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

    @Query("SELECT * FROM MandiRecordEntity WHERE CASE WHEN :isSearch = 1 THEN crop_category = :crop_category AND crop = :crop AND state = :stateIndia ELSE crop LIKE '%' || :search || '%' OR market LIKE '%' || :search || '%' END  ORDER BY CASE WHEN :sortBy = 'desc' then :orderBy END DESC, CASE WHEN :sortBy = 'desc' then :orderBy END ASC ")
    fun getMandiRecords(
        crop_category: String = "",
        crop: String = "",
        stateIndia: String = "",
        orderBy: String = "",
        sortBy: String = "",
        search: String = "",
        isSearch:Boolean
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