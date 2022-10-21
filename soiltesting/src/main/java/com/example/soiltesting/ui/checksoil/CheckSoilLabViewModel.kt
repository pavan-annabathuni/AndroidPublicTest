package com.example.soiltesting.ui.checksoil

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.Repository.DomainModels.CheckSoilTestDomain
import com.waycool.data.Repository.DomainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource

class CheckSoilLabViewModel:ViewModel() {

    fun getCheckSoilTestLab(lat:Double,long:Double): LiveData<Resource<List<CheckSoilTestDomain>?>> {
        return CropsRepository.getSoilTestLab(lat,long).asLiveData()
    }
}