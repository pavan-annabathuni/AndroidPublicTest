package com.waycool.data.Network.PagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.Network.NetworkModels.VansFeederListNetwork
import kotlin.math.ceil

class VansPagingSource(
    private val api: ApiInterface,
    private val queryMap: MutableMap<String, String>,
) : PagingSource<Int, VansFeederListNetwork>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VansFeederListNetwork> {
        return try {

            val headerMap: Map<String, String> = LocalSource.getHeaderMapSanctum()
                ?: throw RuntimeException("Header Map is Null")

            val position = params.key ?: 1
            queryMap["page"] = position.toString()
            queryMap["lang_id"] = "${LocalSource.getLanguageId() ?: 1}"
            val response = api.getVansFeeder(headerMap, queryMap)

            return LoadResult.Page(
                data = response.body()?.data?.vansFeederList!!,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.body()!!.data?.lastPage || response.body()!!.data?.lastPage == 0) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, VansFeederListNetwork>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}