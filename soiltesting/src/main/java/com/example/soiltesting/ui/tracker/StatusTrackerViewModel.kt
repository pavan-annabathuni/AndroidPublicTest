package com.example.soiltesting.ui.tracker

import androidx.lifecycle.*
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.TrackerDemain
import com.waycool.data.utils.Resource

class StatusTrackerViewModel :ViewModel() {
    fun getTracker(soil_test_request_id: Int): LiveData<Resource<List<TrackerDemain>?>> {
        return CropsRepository.getTracker(soil_test_request_id).asLiveData()
    }

//    private val apiService: ApiService =
//        RetrofitBuilder.getInstance().create(ApiService::class.java)
//
//
//    private val repository = SoilTestingRepository(apiService)
//    val statusTrackerLiveData get() = repository.statusTrackerLiveData
//    val statusStatusTrackerLiveData get() = repository.statusStatusTrackerLiveData
//
//    fun getStatusTracker(soil_test_request_id: Int) {
//        viewModelScope.launch {
//            repository.getTracker(soil_test_request_id)
//        }
//    }
}