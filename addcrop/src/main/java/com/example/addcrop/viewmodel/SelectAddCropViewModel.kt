package com.example.addcrop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.repository.domainModels.CropMasterDomain
import com.waycool.data.utils.Resource

class SelectAddCropViewModel : ViewModel() {
    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getPestDiseaseCrops(searchQuery).asLiveData()
    }
    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }
}