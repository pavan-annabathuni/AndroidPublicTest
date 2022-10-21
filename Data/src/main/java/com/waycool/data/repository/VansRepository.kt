package com.waycool.data.repository

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.waycool.data.Network.NetworkSource
import com.waycool.data.repository.DomainMapper.TagsKeywordsDomainMapper
import com.waycool.data.repository.DomainMapper.VansCategoryDomainMapper
import com.waycool.data.repository.DomainMapper.VansFeederDomainMapper
import com.waycool.data.repository.domainModels.TagsAndKeywordsDomain
import com.waycool.data.repository.domainModels.VansCategoryDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.Sync.syncer.TagsSyncer
import com.waycool.data.Sync.syncer.VansCategorySyncer
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object VansRepository {


    fun getTagsAndKeywords(): Flow<Resource<List<TagsAndKeywordsDomain>?>> {
        return TagsSyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        TagsKeywordsDomainMapper().toDomainList(it.data ?: emptyList())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }


    fun getVansCategoryList(): Flow<Resource<List<VansCategoryDomain>>> {
        return VansCategorySyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        VansCategoryDomainMapper().toDomainList(it.data ?: emptyList())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }


    fun getVansFeeder(queryMap: MutableMap<String, String>): Flow<PagingData<VansFeederListDomain>> {
        return NetworkSource.getVansFeeder(queryMap).map {
            it.map {
                Log.d("Vans", it.title ?: "VansDefault")
                VansFeederDomainMapper.VansFeederListDomainMapper().mapToDomain(it)
            }
//            when (it) {
//                is Resource.Success -> {
//                    Resource.Success(
//                        VansFeederDomainMapper().mapToDomain(it.data?.data ?: VansNetwork())
//                    )
//                }
//                is Resource.Loading -> {
//                    Resource.Loading()
//                }
//                is Resource.Error -> {
//                    Resource.Error(it.message)
//                }
//            }
        }
    }

}