package com.waycool.weather.viewModel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.Network.NetworkModels.UserDetailsDTO
import com.waycool.data.repository.domainModels.weather.HourlyDomain
import com.waycool.data.repository.domainModels.WeatherMasterDomain
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.ProfileRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.WeatherRepository
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.repository.domainModels.weather.DailyDomain
import com.waycool.data.utils.Resource
import com.waycool.weather.utils.Constants

class WeatherViewModel : ViewModel() {
    private val _status = MutableLiveData<Constants.ApiStatus>()
    val status: LiveData<Constants.ApiStatus>
        get() = _status

//    private val _response = MutableLiveData<CurrentDomain>()
//    val response: LiveData<CurrentDomain>
//        get() = _response
//
//    private val _responseList = MutableLiveData<List<DailyDomain>>()
//    val responseList: LiveData<List<DailyDomain>>
//        get() = _responseList
//
//    private val _responseHourly = MutableLiveData<List<HourlyDomain>>()
//    val responseHourly: LiveData<List<HourlyDomain>>
//        get() = _responseHourly


    private val _navigateToSelectedProperty = MutableLiveData<DailyDomain?>()
    val navigateToSelectedProperty: LiveData<DailyDomain?>
        get() = _navigateToSelectedProperty

    private val _navigateToSelectedHourly = MutableLiveData<HourlyDomain?>()
    val navigateToSelectedHourly: MutableLiveData<HourlyDomain?>
        get() = _navigateToSelectedHourly

    init {
        _status.value = Constants.ApiStatus.LOADING
    }

    fun getUserDetails() = LoginRepository.getUserDetails().asLiveData()
    suspend fun getUserProfileDetails():LiveData<Resource<UserDetailsDTO?>> =
        ProfileRepository.getUserProfileDet().asLiveData()

     fun getWeather(
        lat: String,
        lon: String,
        lang: String = "en"
    ): LiveData<Resource<WeatherMasterDomain?>> {

        return WeatherRepository.getWeather(lat, lon, lang).asLiveData()
//        viewModelScope.launch {
//            try {
//                Constants.ApiStatus.LOADING
//                val data3 = WeatherRepository.getWeather(lat, lon, lang)
//                _status.value = Constants.ApiStatus.DONE
//                _response.value = data3.
//            } catch (e: Exception) {
//                _status.value = Constants.ApiStatus.ERROR
//            }
//
//        }
    }

//    fun getWeekWeather(lat: String, lon: String, lang: String = "en") {
//        viewModelScope.launch {
//            try {
//                val data2 =
//                    api.weatherApi.retrofitService.getWeather(16.92, 82.52, Constants.APP_KEY, "en")
//                //_status.value = "SUCCESS"
//                _responseList.value = data2.daily
//            } catch (e: Exception) {
//
//            }
//        }
//    }
//


    fun displayPropertyDaily(id: DailyDomain) {
        _navigateToSelectedProperty.value = id
    }

    fun displayPropertyDetailsCompleteDaily() {
        _navigateToSelectedProperty.value = null
    }

    fun displayPropertyHourly(id: HourlyDomain) {
        _navigateToSelectedHourly.value = id
    }

    fun displayPropertyDetailsCompleteHourly() {
        _navigateToSelectedHourly.value = null
    }

    //Ad Banners
    fun getVansAdsList(): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }
}