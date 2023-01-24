package com.waycool.data.Network.PagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.error.CrashAnalytics
import com.waycool.data.repository.domainModels.MandiDomainRecord

class MandiPagingSource(
    private val api: ApiInterface,
    private val lat:String?,
    private val lon:String?,private val crop_category:String?,private val state:String?
    ,private val crop:String?
    ,private val sortBy:String?,private val orderBy:String?,private val search:String?,private val accId:Int?)
    : PagingSource<Int,MandiDomainRecord>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,MandiDomainRecord> {
        return try {

            val langCode = LocalSource.getLanguageCode() ?: "en"
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val position = params.key ?: 1
            val response = api.getMandiList(headerMap,lat,lon,
                crop_category,state, crop, position,
                orderBy,
                sortBy,search, lang = langCode)

            return LoadResult.Page(
                data = response.body()?.data!!.records,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.body()!!.data?.total_pages || response.body()!!.data?.total_pages == 0) null else position + 1
            )
        } catch (e: Exception) {
            CrashAnalytics.crashAnalyticsError("getMandiList-MandiPagingSource Exception--${e.message}")

            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MandiDomainRecord>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    }