package com.waycool.videos

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.Repository.DomainModels.TagsAndKeywordsDomain
import com.waycool.data.Repository.DomainModels.VansCategoryDomain
import com.waycool.data.Repository.DomainModels.VansFeederDomain
import com.waycool.data.Repository.DomainModels.VansFeederListDomain
import com.waycool.data.Repository.VansRepository
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.HashMap


class VideoViewModel : ViewModel() {


    //Videos
    fun getVansVideosList(
        tags: String? = null,
        categoryId: Int? = null
    ): LiveData<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "videos"
        queryMap["lang_id"] = "1"
        if (tags != null)
            queryMap["tags"] = tags

        if (categoryId != null)
            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }


    fun getVansVideosCat(): LiveData<Resource<List<VansCategoryDomain>>> {
        return VansRepository.getVansCategoryList().asLiveData()
    }

    fun getTagsAndKeywords(): LiveData<Resource<List<TagsAndKeywordsDomain>?>> {
        return VansRepository.getTagsAndKeywords().asLiveData()
    }


}