package com.example.soiltesting.ui.history

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.network.RetrofitBuilder
import com.example.soiltesting.repository.SoilTestingRepository
import com.waycool.data.Repository.CropsRepository
import com.waycool.data.Repository.DomainModels.CheckSoilTestDomain
import com.waycool.data.Repository.DomainModels.PestDiseaseDomain
import com.waycool.data.Repository.DomainModels.SoilTestHistoryDomain
import com.waycool.data.Repository.DomainModels.VansFeederListDomain
import com.waycool.data.Repository.VansRepository
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
//    val historyLiveData get() = CropsRepository.getSoilTestHistory() .historyLiveData
//    val statusLiveData get() = repository.statusLiveData
    fun getSoilTestHistory(): LiveData<Resource<List<SoilTestHistoryDomain>?>> {
        return CropsRepository.getSoilTestHistory().asLiveData()
    }
    //Videos
    fun getVansVideosList(
        tags: String? = null,
        categoryId: Int? = null
    ): LiveData<PagingData<VansFeederListDomain>> {

        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "videos"
        queryMap["lang_id"] = "1"
        if (tags != null)
            queryMap["tags"] = tags
        if (categoryId != null)
            queryMap["category_id"] = categoryId.toString()

        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }

    fun getCheckSoilTestLab(lat:Double,long:Double): LiveData<Resource<List<CheckSoilTestDomain>?>> {
        return CropsRepository.getSoilTestLab(lat,long).asLiveData()
    }


//    private val repository = SoilTestingRepository(apiService)
//    val historyLiveData get() = repository.historyLiveData
//    val statusLiveData get() = repository.statusLiveData
//    fun getAllHistory(userAccount: Int) {
//        viewModelScope.launch {
//            repository.getAllHistory(userAccount)
//        }
//    }
}