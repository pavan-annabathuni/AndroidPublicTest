package com.example.mandiprice.viewModel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mandiprice.api.MandiApi
import com.example.mandiprice.api.mandiResponse.Record
import com.example.mandiprice.api.stateRespond.IndianStates
import com.example.mandiprice.utils.Constants
import com.waycool.data.Network.NetworkModels.StateModel
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.MandiRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.*
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch

class MandiViewModel : ViewModel() {
    private val _loading = MutableLiveData<Constants.ApiStatus>()
    val loading: LiveData<Constants.ApiStatus>
        get() = _loading
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _state = MutableLiveData<IndianStates>()
    val states: LiveData<IndianStates>
        get() = _state

    private val _response = MutableLiveData<List<Record>>()
    val response: LiveData<List<Record>>
        get() = _response

    private val _paging = MutableLiveData<PagingData<Record>>()
    val paging: LiveData<PagingData<Record>>
        get() = _paging

    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }
    fun getAllCrops():LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getAllCrops().asLiveData()
    }

    suspend fun getState():LiveData<Resource<StateModel?>> {
        return CropsRepository.getState().asLiveData()
    }


    suspend fun getMandiDetails(lat:String,long:String,crop_category:String?,state:String?,crop:String?,
                                sortBy: String?, orderBy: String?,search:String?,accountId:Int
    ): LiveData<PagingData<MandiDomainRecord>> =
        MandiRepository.getMandiList(lat,long,crop_category,
            state,crop,sortBy,orderBy,search,accountId).cachedIn(viewModelScope).asLiveData()


    suspend fun getMandiHistoryDetails(crop_master_id:Int?,mandi_master_id:Int?):
            LiveData<Resource<MandiHistoryDomain?>> =
        MandiRepository.getMandiHistory(crop_master_id,mandi_master_id).asLiveData()


    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()


    init {
        _loading.value = Constants.ApiStatus.LOADING
    }

    fun getMandiRecord(
        crop_category: String?, state: String?, crop: String?, sortBy: String, orderBy: String?
    ) {
        viewModelScope.launch {
            _loading.value = Constants.ApiStatus.LOADING
            try {
                val mandiData = MandiApi.retrofitService.getList(
                    "12.61154271",
                    "77.08181494",
                    crop_category,
                    state,
                    crop, 1,
                    sortBy,
                    orderBy
                )
                _response.value = mandiData.data.records
                _status.value = mandiData.status
                _loading.value = Constants.ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = "Failed"
                _loading.value = Constants.ApiStatus.ERROR
            }
        }
    }

    fun getRandomList(sortBy: String, orderBy: String?) {
        viewModelScope.launch {
            _loading.value = Constants.ApiStatus.LOADING
            try {
                val mandiData = MandiApi.retrofitService.randomList(
                    "12.61154271",
                    "77.08181494",
                    1,
                    sortBy,
                    orderBy
                )
                _response.value = mandiData.data.records
                _status.value = mandiData.status
                _loading.value = Constants.ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = "Failed"
            }
        }
    }


    fun getStates() {
        viewModelScope.launch {
            try {
                val statesData = MandiApi.retrofitService.stateList()
                _state.value = statesData
                _status.value = "SUCCESS"

            } catch (e: Exception) {
                _status.value = "Failed $e"
            }
        }
    }

    //Ad Banners
    fun getVansAdsList(): LiveData<PagingData<VansFeederListDomain>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        return VansRepository.getVansFeeder(queryMap).cachedIn(viewModelScope).asLiveData()
    }
}