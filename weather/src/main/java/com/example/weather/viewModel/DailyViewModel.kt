package com.example.weather.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.weather.apiService.apiResponse.Daily

class DailyvViewModel(data: Daily, app: Application): AndroidViewModel(app) {
    private val _selectedProperty = MutableLiveData<Daily>()
    val selectedProperty: LiveData<Daily>
        get() = _selectedProperty

    init {
        _selectedProperty.value = data
    }
}


class DetailViewModelFactory (
    private val data: Daily,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyvViewModel::class.java)) {
            return DailyvViewModel(data,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }}