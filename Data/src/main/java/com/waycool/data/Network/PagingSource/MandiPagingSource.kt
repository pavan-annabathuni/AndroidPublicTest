package com.waycool.data.Network.PagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.repository.domainModels.MandiRecordDomain
import java.lang.Exception

class MandiPagingSource(
    private val api: ApiInterface,
    private val lat:String?,
    private val lon:String?,private val crop_category:String?,private val state:String?
    ,private val crop:String?
    ,private val sortBy:String?,private val orderBy:String?,private val search:String?,private val accId:Int?)
    : PagingSource<Int,MandiRecordDomain>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,MandiRecordDomain> {
        return try {

            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val position = params.key ?: 1
            val response = api.getMandiList(headerMap,lat,lon,
                crop_category,state, crop, position,
                orderBy,
                sortBy,search)

//            return LoadResult.Page(
//                data = response.body()?.data!!.records,
//                prevKey = if (position == 1) null else position - 1,
//                nextKey = if (position == response.body()!!.data.total_pages || response.body()!!.data.total_pages == 0) null else position + 1
//            )
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MandiRecordDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    }