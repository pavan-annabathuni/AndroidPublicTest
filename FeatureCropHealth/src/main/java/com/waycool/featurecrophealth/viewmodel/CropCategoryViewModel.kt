package com.waycool.featurecrophealth.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waycool.featurecrophealth.api.RetrofitService
import com.waycool.featurecrophealth.network.NetworkBuilder
import com.waycool.featurecrophealth.repository.CropCategoryRepo
import kotlinx.coroutines.launch

class CropCategoryViewModel constructor(application: Application): AndroidViewModel(application) {
    private val apiClient: RetrofitService =
        NetworkBuilder.getInstance().create(RetrofitService::class.java)
    private val repository = CropCategoryRepo(apiClient)
    val categoryLiveData get() = repository.cropCategoryLiveData
    val statusLiveData get() = repository.statusLiveData


    fun getAllCategory(context: Context) {
        viewModelScope.launch {
            repository.getCropCategory(context)
        }
    }
    fun getDataByCateGoryID(){


    }
}
//val temp:
// List<MainAllList>
//click oncread get id
//temp list clear

//all list for each cropid==getid-> temp.add

//adapter.setlist(list)
//notify




