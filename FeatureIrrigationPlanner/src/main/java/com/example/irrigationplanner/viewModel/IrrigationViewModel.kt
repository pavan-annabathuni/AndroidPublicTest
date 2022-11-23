package com.example.irrigationplanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.utils.Resource

class IrrigationViewModel:ViewModel() {
    fun getMyCrop2(account_id: Int): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2(account_id).asLiveData()
}