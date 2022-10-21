package com.example.soiltesting.ui.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.Network.NetworkModels.SoilTestResponseDTO
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.utils.Resource
import java.util.*

class SoilTestRequestViewModel :ViewModel() {

    fun postNewSoil(plot_no:String,pincode:String,address:String,number:String): LiveData<Resource<SoilTestResponseDTO?>> =
        CropsRepository.postNewSoil(plot_no,pincode,address,number).asLiveData()

}