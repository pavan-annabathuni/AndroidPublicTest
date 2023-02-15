package com.example.addcrop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource

class SelectAddCropViewModel : ViewModel() {
    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getAllCrops(searchQuery).asLiveData()
    }
    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }
    fun getDasBoard(): LiveData<Resource<DashboardDomain?>> =
        CropsRepository.getDashBoard().asLiveData()
    //crop variety
    fun getCropVariety(crop_id:Int): LiveData<Resource<List<VarietyCropDomain>?>> =
        CropsRepository.cropVariety(crop_id).asLiveData()


}