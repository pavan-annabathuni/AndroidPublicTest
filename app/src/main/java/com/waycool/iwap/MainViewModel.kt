package com.waycool.iwap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.Network.NetworkModels.Notification
import com.waycool.data.Network.NetworkModels.NotificationModel
import com.waycool.data.Network.NetworkModels.UpdateNotification
import com.waycool.data.repository.*
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

class MainViewModel : ViewModel() {
    fun getUserDetails() = LoginRepository.getUserDetails().asLiveData()

    //Videos
    fun getVansVideosList(
        tags: String? = null,
        categoryId: Int? = null
    ): Flow<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "videos"
        queryMap["lang_id"] = "1"
        if (tags != null)
            queryMap["tags"] = tags
        if (categoryId != null)
            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).distinctUntilChanged().cachedIn(viewModelScope)
    }

    fun getVansNewsList(
        vansType: String? = null,
        tags: String? = null
    ): Flow<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["lang_id"] = "1"
        if (vansType == null) {
            queryMap["vans_type"] = "news,articles"
        } else queryMap["vans_type"] = vansType.toString()

        if (tags != null)
            queryMap["tags"] = tags
//        if (categoryId != null)
//            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).distinctUntilChanged().cachedIn(viewModelScope)
    }

    fun getWeather(
        lat: String,
        lon: String,
        lang: String = "en"
    ): LiveData<Resource<WeatherMasterDomain?>> {

        return WeatherRepository.getWeather(lat, lon, lang).asLiveData()
    }

    fun getModuleMaster(): LiveData<Resource<List<ModuleMasterDomain>>> =
        LoginRepository.getModuleMaster().asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    //Ad Banners
    fun getVansAdsList(): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }

    fun getMyFarms(): LiveData<Resource<List<MyFarmsDomain>>> =
        FarmsRepository.getMyFarms().asLiveData()

    fun getNotification():LiveData<Resource<NotificationModel?>>{
        return NotificationRepository.getNotification().asLiveData()
    }
    fun updateNotification(id:String):LiveData<Resource<UpdateNotification?>>{
        return NotificationRepository.updateNotification(id).asLiveData()
    }

    fun getLatestTimeStamp(): LiveData<String> = DevicesRepository.getLatestTimeStamp().asLiveData()
    fun updateDevices() {
        DevicesRepository.refreshDevices()
    }


}