package com.waycool.featurecropprotect

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.repository.VansRepository
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

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
        crop_id:Int?=null,
        module_id:String?=null,
        tags: String? = null,
        categoryId: Int? = null
    ): Flow<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "videos"
        if(crop_id!=null) {
            queryMap["crop_id"] = crop_id.toString()
        }
        if(module_id!=null){
            queryMap["module_id"] = module_id.toString()

        }
        if (tags != null)
            queryMap["tags"] = tags
        if (categoryId != null)
            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).distinctUntilChanged().cachedIn(viewModelScope)
    }


    //Ad Banners
    fun getVansAdsList(): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getVansNewsList(
        crop_id:Int?=null,
        module_id:String?=null,
        vansType: String? = null,
        tags: String? = null
    ): Flow<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        if(crop_id!=null) {
            queryMap["crop_id"] = crop_id.toString()
        }

            queryMap["module_id"] = module_id.toString()


        if (vansType == null) {
            queryMap["vans_type"] = "news,articles"
        } else queryMap["vans_type"] = vansType.toString()

        if (tags != null)
            queryMap["tags"] = tags

//        if (categoryId != null)
//            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).distinctUntilChanged().cachedIn(viewModelScope)
    }



}