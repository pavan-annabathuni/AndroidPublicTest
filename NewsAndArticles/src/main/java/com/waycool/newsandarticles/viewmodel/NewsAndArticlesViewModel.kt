package com.waycool.newsandarticles.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.Repository.DomainModels.VansCategoryDomain
import com.waycool.data.Repository.DomainModels.VansFeederDomain
import com.waycool.data.Repository.DomainModels.VansFeederListDomain
import com.waycool.data.Repository.VansRepository
import com.waycool.data.utils.Resource
import java.util.HashMap

class NewsAndArticlesViewModel : ViewModel() {


    //news
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

//        if (categoryId != null)
//            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }


//    fun getVansVideosCat(): LiveData<Resource<List<VansCategoryDomain>>> {
//        return VansRepository.getVansCategoryList().asLiveData()
//    }

}