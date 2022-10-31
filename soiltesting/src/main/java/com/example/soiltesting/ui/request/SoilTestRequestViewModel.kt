package com.example.soiltesting.ui.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.SoilTestResponseDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.utils.Resource

class SoilTestRequestViewModel :ViewModel() {

    fun postNewSoil(org_id:Int,plot_no:String,pincode:String,address:String,number:String): LiveData<Resource<SoilTestResponseDTO?>> =
        CropsRepository.postNewSoil(org_id,plot_no,pincode,address,number).asLiveData()

}