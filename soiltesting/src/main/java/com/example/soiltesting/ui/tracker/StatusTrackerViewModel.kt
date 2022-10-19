package com.example.soiltesting.ui.tracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.network.RetrofitBuilder
import com.example.soiltesting.repository.SoilTestingRepository
import kotlinx.coroutines.launch

class StatusTrackerViewModel constructor(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService =
        RetrofitBuilder.getInstance().create(ApiService::class.java)


    private val repository = SoilTestingRepository(apiService)
    val statusTrackerLiveData get() = repository.statusTrackerLiveData
    val statusStatusTrackerLiveData get() = repository.statusStatusTrackerLiveData

    fun getStatusTracker(soil_test_request_id: Int) {
        viewModelScope.launch {
            repository.getTracker(soil_test_request_id)
        }
    }
}