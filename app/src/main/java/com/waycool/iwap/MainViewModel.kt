package com.waycool.iwap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.WeatherRepository
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.repository.domainModels.WeatherMasterDomain
import com.waycool.data.utils.Resource

class MainViewModel:ViewModel() {
    fun getUserDetails() = LoginRepository.getUserDetails().asLiveData()
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
    fun getWeather(
        lat: String,
        lon: String,
        lang: String = "en"
    ): LiveData<Resource<WeatherMasterDomain?>> {

        return WeatherRepository.getWeather(lat, lon, lang).asLiveData()
}
    fun getModuleMaster(): LiveData<Resource<List<ModuleMasterDomain>>> = LoginRepository.getModuleMaster().asLiveData()
}