package com.example.mandiprice

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mandiprice.api.apiService
import com.example.mandiprice.api.mandiResponse.Record
import java.lang.Exception

class Pagination(private val serviceRetrofit:apiService,
                 private val lat:String,
                 private val lon:String,private val crop_category:String?,private val state:String?
                 ,private val crop:String?
                 ,private val sortBy:String,private val orderBy:String)
    : PagingSource<Int,Record>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Record> {
        return try {
            val position= params.key ?: 1
            val response = serviceRetrofit.getList(lat,lon,
                crop_category,state, crop, position,
                orderBy,
            sortBy)

            return LoadResult.Page(
                data = response.data.records,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.data.total_results) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    @OptIn(ExperimentalPagingApi::class)
    override fun getRefreshKey(state: PagingState<Int, Record>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }




}