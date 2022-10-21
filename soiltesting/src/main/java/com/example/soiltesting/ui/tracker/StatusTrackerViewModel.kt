package com.example.soiltesting.ui.tracker

import android.app.Application
import androidx.lifecycle.*
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.network.RetrofitBuilder
import com.example.soiltesting.repository.SoilTestingRepository
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.Repository.DomainModels.CheckSoilTestDomain
import com.waycool.data.Repository.DomainModels.TrackerDemain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch

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