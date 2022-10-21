package com.example.addcrop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.Repository.DomainModels.CropCategoryMasterDomain
import com.waycool.data.Repository.DomainModels.CropMasterDomain
import com.waycool.data.utils.Resource

class SelectAddCropViewModel : ViewModel() {
    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getPestDiseaseCrops(searchQuery).asLiveData()
    }
    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }
}