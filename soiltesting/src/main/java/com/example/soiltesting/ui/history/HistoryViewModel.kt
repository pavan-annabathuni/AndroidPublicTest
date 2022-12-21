package com.example.soiltesting.ui.history

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.Network.NetworkModels.CheckSoilTestLabDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource

class HistoryViewModel : ViewModel() {
//    val historyLiveData get() = CropsRepository.getSoilTestHistory() .historyLiveData
//    val statusLiveData get() = repository.statusLiveData
    fun getSoilTestHistory(account_id: Int): LiveData<Resource<List<SoilTestHistoryDomain>?>> {
        return CropsRepository.getSoilTestHistory(account_id).asLiveData()
    }
    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()
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

    fun getCheckSoilTestLab(account_id:Int,lat:String?,long:String?): LiveData<Resource<List<CheckSoilTestDomain>?>> {
        return CropsRepository.getSoilTestLab(account_id,lat,long).asLiveData()
    }

    //Ad Banners
    fun getVansAdsList(): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
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