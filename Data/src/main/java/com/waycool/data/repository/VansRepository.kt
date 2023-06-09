package com.waycool.data.repository

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.waycool.data.Network.NetworkModels.VansSharedData
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.syncer.TagsSyncer
import com.waycool.data.Sync.syncer.VansCategorySyncer
import com.waycool.data.repository.DomainMapper.TagsKeywordsDomainMapper
import com.waycool.data.repository.DomainMapper.VansCategoryDomainMapper
import com.waycool.data.repository.DomainMapper.VansFeederDomainMapper
import com.waycool.data.repository.DomainMapper.VansSharedDataDomainMapper
import com.waycool.data.repository.domainModels.TagsAndKeywordsDomain
import com.waycool.data.repository.domainModels.VansCategoryDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.repository.domainModels.VansSharedDataDomain
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
                VansFeederDomainMapper.VansFeederListDomainMapper().mapToDomain(it)
            }

        }
    }

    fun getVansFeederSinglePage(queryMap: MutableMap<String, String>):Flow<Resource<List<VansFeederListDomain>>>{
        return NetworkSource.getVansFeederSinglePage(queryMap).map {
            when(it){
                is Resource.Success ->{
                    Resource.Success(VansFeederDomainMapper.VansFeederListDomainMapper().toDomainList(it.data?.data?.vansFeederList?: emptyList()))
                }
                is Resource.Loading ->{
                    Resource.Loading()
                }
                is Resource.Error ->{
                    Resource.Error(it.message)
                }
            }
        }
    }

    fun getVansSharedData(vans_id: Int): Flow<Resource<VansSharedDataDomain>> {
        return NetworkSource.getVansSharedData(vans_id).map {
            when (it) {
                is Resource.Success -> {
                    Log.d("NADeepLink","NADeepLinkRepo")
                    Resource.Success(VansSharedDataDomainMapper().mapToDomain(it.data?.data ?: VansSharedData()))

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
}