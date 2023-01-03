package com.example.cropinformation.viewModle

import androidx.lifecycle.LiveData
import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import com.example.cropinformation.apiservice.response.DataX
import com.example.cropinformation.apiservice.videoApi
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.repository.domainModels.CropMasterDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TabViewModel:ViewModel {
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _response = MutableLiveData<List<DataX>>()
    val response: LiveData<List<DataX>>
        get() = _response

    private val _response2 = MutableLiveData<List<DataX>>()
    val response2: LiveData<List<DataX>>
        get() = _response2

    private val _response3 = MutableLiveData<List<com.example.cropinformation.apiservice.response.cropAdvisory.Data>>()
    val response3: LiveData<List<com.example.cropinformation.apiservice.response.cropAdvisory.Data>>
        get() = _response3

    constructor()

    fun getCropInformationDetails(crop_id:Int): LiveData<Resource<List<CropInformationDomainData>>> =
        CropsRepository.getCropInformation(crop_id).asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getCropInfoCrops(searchQuery).asLiveData()
    }
    fun getIrrigationCrops(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getIrrigationCrops(searchQuery).asLiveData()
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

    fun getTabItem(){
          viewModelScope.launch {

              try {
                  val VideoData = videoApi.retrofitService.getVideo("videos", "Apple")
                  _response.value = VideoData.data.data
                  _status.value = "SUCCESS"
              } catch (e: Exception) {
                      _status.value = "FAILED$e"
              }
          }
    }
    fun getNewsItem(){
        viewModelScope.launch {

            try {
                val VideoData = videoApi.retrofitService.getVideo("news", "tomato")
                _response2.value = VideoData.data.data
                _status.value = "SUCCESS"
            } catch (e: Exception) {
                _status.value = "FAILED$e"
            }
        }
    }
    fun cropAdvisory(){
        viewModelScope.launch {

            try {
                val VideoData = videoApi.retrofitService.getInfromation()
                _response3.value = VideoData.data
                _status.value = "SUCCESS"
            } catch (e: Exception) {
                _status.value = "FAILED$e"
            }
        }
    }

    //Ad Banners
    fun getVansAdsList(): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }
}