package com.waycool.weather.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.waycool.data.repository.domainModels.weather.DailyDomain

class DailyvViewModel(data: DailyDomain?, app: Application): AndroidViewModel(app) {
    private val _selectedProperty = MutableLiveData<DailyDomain>()
    val selectedProperty: LiveData<DailyDomain?>
        get() = _selectedProperty

    init {
        _selectedProperty.value = data!!
    }
}


class DetailViewModelFactory (
    private val data: DailyDomain,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyvViewModel::class.java)) {
            return DailyvViewModel(data,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }}