package com.waycool.data.Sync.syncer.mandiSyncer

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.waycool.data.Local.Entity.MandiKeysEntity
import com.waycool.data.Local.Entity.MandiRecordEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.db.OutgrowDB
import com.waycool.data.Local.mappers.MandiRecordsEntityMapper
import com.waycool.data.Network.ApiInterface.ApiInterface
import java.lang.Exception

@ExperimentalPagingApi
 class MandiRemoteMediator(
    val apiInterface: ApiInterface,
    private val lat: String?,
    private val lon: String?, private val crop_category: String?, private val stateIndia: String?
    , private val crop: String?
    , private val sortBy: String?, private val orderBy: String?, private val search: String?
): RemoteMediator<Int, MandiRecordEntity>() {

    private val outgrowDB=OutgrowDB.getDatabase()
    private val mandiDao = outgrowDB.mandiDao()

    override suspend fun initialize(): InitializeAction {
        return if (!MandiSyncer.isSyncRequired()) {
            // Cached data is up-to-date, so there is no need to re-fetch from network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning LAUNCH_INITIAL_REFRESH here
            // will also block RemoteMediator's APPEND and PREPEND from running until REFRESH
            // succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }


    override suspend fun load(loadType: LoadType, state: PagingState<Int, MandiRecordEntity>): MediatorResult {
        return try {

            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

            val response = apiInterface.getMandiList(headerMap,lat,lon,
                crop_category,stateIndia, crop, currentPage,
                sortBy,orderBy,search).body()
            val endOfPaginationReached = response?.data?.total_pages == currentPage

            val prevPage = if(currentPage == 1) null else currentPage -1
            val nextPage = if(endOfPaginationReached) null else currentPage + 1

            outgrowDB.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    mandiDao.deleteMandiRecords()
                    mandiDao.deleteAllMandiRemoteKeys()
                    MandiSyncer.invalidateSync()
                }

                mandiDao.insertMandiRecords( MandiRecordsEntityMapper().toEntityList(response?.data?.records?: emptyList()) )
                val keys = response?.data?.records?.map { mandi ->
                    mandi.id?.let {
                        MandiKeysEntity(
                            id = it,
                            prevPage = prevPage,
                            nextPage = nextPage
                        )
                    }
                }
                mandiDao.addAllMandiRemoteKeys(keys as List<MandiKeysEntity>)

                MandiSyncer.setSyncStatus(true)
            }
            MediatorResult.Success(endOfPaginationReached)
        }
        catch (e: Exception){
            MandiSyncer.setSyncStatus(false)
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MandiRecordEntity>
    ): MandiKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                mandiDao.getMandiRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MandiRecordEntity>
    ): MandiKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { quote ->
                mandiDao.getMandiRemoteKeys(id = quote.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MandiRecordEntity>
    ): MandiKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { quote ->
                mandiDao.getMandiRemoteKeys(id = quote.id)
            }
    }


}
