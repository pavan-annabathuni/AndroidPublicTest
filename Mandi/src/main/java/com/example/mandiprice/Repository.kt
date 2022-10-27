package com.example.mandiprice

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mandiprice.api.apiService
import com.example.mandiprice.api.mandiResponse.Record
import java.util.concurrent.Flow

class Repository
constructor(private val serviceRetrofit: apiService) {
    fun getMandi(lat:String,lon:String,crop_category:String?,state:String?,crop:String?
                 ,sortBy:String,orderBy:String) = Pager(
        config = PagingConfig(pageSize = 50, maxSize = 200),
        pagingSourceFactory = {
            Pagination(
                serviceRetrofit,
                lat,lon,crop_category,state,crop,sortBy,orderBy
            )
        }
    ).liveData
}