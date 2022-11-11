package com.example.soiltesting.ui.checksoil

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import com.waycool.data.utils.Resource

class CheckSoilLabViewModel:ViewModel() {

//    fun getCheckSoilTestLab(account_id:Int,lat:Double,long:Double): LiveData<Resource<List<CheckSoilTestDomain>?>> {
//        return CropsRepository.getSoilTestLab(account_id,lat,long).asLiveData()
//    }
}