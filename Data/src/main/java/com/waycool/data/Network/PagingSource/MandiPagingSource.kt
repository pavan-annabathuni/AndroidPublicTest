package com.waycool.data.Network.PagingSource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.Network.NetworkModels.VansFeederListNetwork
import com.waycool.data.repository.domainModels.MandiDomainRecord
import java.lang.Exception

class MandiPagingSource(
    private val api: ApiInterface,
    private val lat:String?,
    private val lon:String?,private val crop_category:String?,private val state:String?
    ,private val crop:String?
    ,private val sortBy:String,private val orderBy:String?,private val search:String?)
    : PagingSource<Int,MandiDomainRecord>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,MandiDomainRecord> {
        return try {

            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val position = params.key ?: 1
            val response = api.getMandiList(headerMap,lat,lon,
                crop_category,state, crop, position,
                orderBy,
                sortBy,search)

            return LoadResult.Page(
                data = response.body()?.data!!.records,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == 3) null
                else position + 1
            )
        } catch (e: Exception) {
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