package com.waycool.featurecrophealth.ui.details

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waycool.featurecrophealth.api.RetrofitService
import com.waycool.featurecrophealth.db.DetailsDatabase
import com.waycool.featurecrophealth.model.cropdetails.Data
import com.waycool.featurecrophealth.network.NetworkBuilder
import com.waycool.featurecrophealth.repository.CropDetailsRepo
import kotlinx.coroutines.launch

class CropDetailsViewModel constructor(application: Application): AndroidViewModel(application) {
    private val apiClient: RetrofitService =
        NetworkBuilder.getInstance().create(RetrofitService::class.java)

    val roomDatabase = DetailsDatabase.getDatabase(application)
    val dao=roomDatabase.cropDetailsDao()

    private val repository = CropDetailsRepo(apiClient,dao)
    val detailsLiveData get() = repository.cropDetailsLiveData
    val statusLiveData get() = repository.statusLiveData
    fun getCropDetails(requireContext: Context) {

        viewModelScope.launch {
            repository.getCropDetails(requireContext)
        }
    }
   suspend fun getLocalCropList() :List<Data>{
        return repository.getLocalCropList()
    }
}