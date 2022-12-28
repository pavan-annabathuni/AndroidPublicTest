package com.waycool.featurecrophealth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import okhttp3.MultipartBody

class CropHealthViewModel : ViewModel() {

    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getAiCrops(searchQuery).asLiveData()
    }

    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()


    fun getAiCropHistory(): LiveData<Resource<List<AiCropHistoryDomain>>> {
        return CropsRepository.getAiCropHistory().asLiveData()
    }

    fun postAiImage(
        cropId: Int,
        cropName: String,
        image: MultipartBody.Part
    ): LiveData<Resource<AiCropDetectionDomain>> {
        return CropsRepository.postAiCropImage(cropId, cropName, image).asLiveData()
    }

    //Videos
    fun getVansVideosList(
        module_id:String?=null,
        tags: String? = null,
        categoryId: Int? = null
    ): LiveData<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "videos"
            queryMap["module_id"] = module_id.toString()

        if (tags != null)
            queryMap["tags"] = tags
        if (categoryId != null)
            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }
}