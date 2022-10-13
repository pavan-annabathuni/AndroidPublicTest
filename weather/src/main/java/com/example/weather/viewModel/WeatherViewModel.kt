package com.example.weather.viewModel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.apiService.api
import com.example.weather.apiService.apiResponse.Current
import com.example.weather.apiService.apiResponse.Daily
import com.example.weather.apiService.apiResponse.Hourly
import com.example.weather.apiService.apiResponse.WeatherResponse
import com.example.weather.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class WeatherViewModel:ViewModel() {
    private val _status = MutableLiveData<Constants.ApiStatus>()
    val status: LiveData<Constants.ApiStatus>
        get() = _status

    private val _response = MutableLiveData<Current>()
    val response: LiveData<Current>
        get() = _response

    private val _responseList = MutableLiveData<List<Daily>>()
    val responseList: LiveData<List<Daily>>
        get() = _responseList

    private val _responseHourly = MutableLiveData<List<Hourly>>()
    val responseHourly: LiveData<List<Hourly>>
        get() = _responseHourly


    private val _navigateToSelectedProperty = MutableLiveData<Daily>()
    val navigateToSelectedProperty: LiveData<Daily?>
        get() = _navigateToSelectedProperty

    private val _navigateToSelectedHourly = MutableLiveData<Hourly>()
    val navigateToSelectedHourly: LiveData<Hourly>
        get() = _navigateToSelectedHourly
       init {
           _status.value = Constants.ApiStatus.LOADING
       }
    fun getCurrentWeather(){
        viewModelScope.launch {
            try {
                Constants.ApiStatus.LOADING
                val data3 = api.weatherApi.retrofitService.getWeather(12.92, 77.52,Constants.APP_KEY,"en")
                _status.value = Constants.ApiStatus.DONE
                _response.value = data3.current
            }catch (e:Exception){
                _status.value = Constants.ApiStatus.ERROR
            }

        }
    }
    fun getWeekWeather() {
        viewModelScope.launch {
            try {
                val data2 =
                    api.weatherApi.retrofitService.getWeather(16.92, 82.52, Constants.APP_KEY,"en")
                //_status.value = "SUCCESS"
                _responseList.value = data2.daily
            } catch (e: Exception) {

            }
        }
    }
        fun getHourlyWeather() {
            viewModelScope.launch {
                try {
                    val data2 =
                        api.weatherApi.retrofitService.getWeather(12.92, 77.52, Constants.APP_KEY,"en")
                   // _status.value = "SUCCESS"
                    _responseHourly.value = data2.hourly
                } catch (e: Exception) {

                }
            }
        }

        fun displayPropertyDaily(id: Daily) {
            _navigateToSelectedProperty.value = id
        }

        fun displayPropertyDetailsCompleteDaily() {
            _navigateToSelectedProperty.value = null
        }

    fun displayPropertyHourly(id: Hourly) {
        _navigateToSelectedHourly.value = id
    }

    fun displayPropertyDetailsCompleteHourly() {
        _navigateToSelectedHourly.value = null
    }
    }