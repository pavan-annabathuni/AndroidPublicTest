package com.waycool.videos

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.TagsAndKeywordsDomain
import com.waycool.data.repository.domainModels.VansCategoryDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.utils.Resource


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