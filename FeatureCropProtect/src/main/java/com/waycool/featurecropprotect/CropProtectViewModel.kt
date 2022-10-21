package com.waycool.featurecropprotect

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.repository.VansRepository
import com.waycool.data.utils.Resource

class CropProtectViewModel : ViewModel() {

    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getPestDiseaseCrops(searchQuery).asLiveData()
    }

    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }

    fun getPestDiseaseListForCrop(cropId: Int): LiveData<Resource<List<PestDiseaseDomain>>> {
        return CropsRepository.getPestAndDiseasesForCrop(cropId).asLiveData()
    }

    fun getSelectedDisease(diseaseId: Int): LiveData<Resource<PestDiseaseDomain>> {
        return CropsRepository.getPestDiseaseForSelectedDisease(diseaseId).asLiveData()
    }

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


//    fun getVansFeeder(
//        map: Map<String, String>
//    ): LiveData<Resource<VansFeederDomain>> {
//        return VansRepository.getVansFeeder(map).asLiveData()
//    }

//    fun getVansNewsList(
//        context: Context?,
//        accessToken: String?,
//        queryMap: HashMap<String?, String?>?
//    ): LiveData<VANSMain> {
//        return cropProtectViewModel.getVansNewsList(context, accessToken, queryMap)
//    }

//    fun setFeedback(
//        context: Context?,
//        accessToken: String?,
//        queryMap: HashMap<String?, String?>?
//    ): LiveData<FeedbackMaster> {
//        return cropProtectViewModel.setFeedback(context, accessToken, queryMap)
//    }

}