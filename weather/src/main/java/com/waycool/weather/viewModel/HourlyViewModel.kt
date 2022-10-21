package com.waycool.weather.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.waycool.data.repository.domainModels.weather.HourlyDomain

class HourlyViewModel(data: HourlyDomain, app: Application): AndroidViewModel(app) {
    private val _selectedProperty = MutableLiveData<HourlyDomain>()
    val selectedProperty: LiveData<HourlyDomain>
        get() = _selectedProperty

    init {
        _selectedProperty.value = data
    }
}


class HourlyViewModelFactory (
    private val data: HourlyDomain,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HourlyViewModel::class.java)) {
            return HourlyViewModel(data,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }}
