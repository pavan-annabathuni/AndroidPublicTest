package com.example.weather.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.weather.apiService.apiResponse.Daily
import com.example.weather.apiService.apiResponse.Hourly

class HourlyViewModel(data: Hourly, app: Application): AndroidViewModel(app) {
    private val _selectedProperty = MutableLiveData<Hourly>()
    val selectedProperty: LiveData<Hourly>
        get() = _selectedProperty

    init {
        _selectedProperty.value = data
    }
}


class HourlyViewModelFactory (
    private val data: Hourly,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HourlyViewModel::class.java)) {
            return HourlyViewModel(data,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }}
