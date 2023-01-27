package com.waycool.newsandarticles.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.utils.Resource
import kotlin.collections.List
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class NewsAndArticlesViewModel : ViewModel() {
    fun getVansNewsList(
        vansType: String? = null,
        tags: String? = null
    ): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["lang_id"] = "1"
        if (vansType == null) {
            queryMap["vans_type"] = "news,articles"
        } else queryMap["vans_type"] = vansType.toString()
        if (tags != null)
            queryMap["tags"] = tags
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }
    fun getVansAdsList(moduleId: String): LiveData<Resource<List<VansFeederListDomain>>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        queryMap["module_id"] = moduleId
        return VansRepository.getVansFeederSinglePage(queryMap).asLiveData()
    }
}