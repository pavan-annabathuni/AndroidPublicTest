package com.waycool.data.Repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.waycool.core.retrofit.OutgrowClient.retrofit
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Local.Entity.TagsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.TagsAndKeywordsDTO
import com.waycool.data.Network.NetworkModels.VansNetwork
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Repository.DomainMapper.LanguageMasterDomainMapper
import com.waycool.data.Repository.DomainMapper.TagsKeywordsDomainMapper
import com.waycool.data.Repository.DomainMapper.VansCategoryDomainMapper
import com.waycool.data.Repository.DomainMapper.VansFeederDomainMapper
import com.waycool.data.Repository.DomainModels.TagsAndKeywordsDomain
import com.waycool.data.Repository.DomainModels.VansCategoryDomain
import com.waycool.data.Repository.DomainModels.VansFeederDomain
import com.waycool.data.Repository.DomainModels.VansFeederListDomain
import com.waycool.data.Sync.syncer.TagsSyncer
import com.waycool.data.Sync.syncer.VansCategorySyncer
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception
import java.lang.RuntimeException

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