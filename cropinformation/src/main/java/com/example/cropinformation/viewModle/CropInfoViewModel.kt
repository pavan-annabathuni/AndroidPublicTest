package com.example.cropinformation.viewModle

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class CropInfoViewModel:ViewModel() {

    fun getCropInformationDetails(crop_id:Int): LiveData<Resource<List<CropInformationDomainData>>> =
        CropsRepository.getCropInformation(crop_id).asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getCropInfoCrops(searchQuery).asLiveData()
    }
    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }
    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getVansVideosList(
        crop_id:String?=null,
        module_id:String?=null,
        tags: String? = null,
        categoryId: Int? = null
    ): Flow<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "videos"
        queryMap["lang_id"] = "1"

        queryMap["crop_id"] = crop_id.toString()
        queryMap["module_id"] = module_id.toString()

        if (tags != null)
            queryMap["tags"] = tags
        if (categoryId != null)
            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).distinctUntilChanged().cachedIn(viewModelScope)
    }

    fun getVansNewsList(
        crop_id:Int?=null,
        module_id:String?=null,
        vansType: String? = null,
        tags: String? = null
    ): Flow<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["lang_id"] = "1"
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


    //Ad Banners
    fun getVansAdsList(moduleId: String): LiveData<Resource<List<VansFeederListDomain>>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        queryMap["module_id"] = moduleId

        return VansRepository.getVansFeederSinglePage(queryMap).asLiveData()
    }

    fun downloadCropInfo() {
        CropsRepository.downloadCropInfo()
    }

}