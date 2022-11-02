package com.example.mandiprice.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mandiprice.api.MandiApi
import com.example.mandiprice.api.mandiResponse.Record
import com.example.mandiprice.utils.Constants
import kotlinx.coroutines.launch

class SearchViewModel:ViewModel() {
    private val _loading = MutableLiveData<Constants.ApiStatus>()
    val loading: LiveData<Constants.ApiStatus>
        get() = _loading
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _response = MutableLiveData<List<Record>>()
    val response: LiveData<List<Record>>
        get() = _response

    init {
        _loading.value = Constants.ApiStatus.LOADING
    }
             fun getRandomList(sortBy:String,orderBy:String) {
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
        fun getSearch(search: String, sortBy: String, orderBy: String) {
            viewModelScope.launch {
                _loading.value = Constants.ApiStatus.LOADING
                try {
                    val mandiData = MandiApi.retrofitService.searchList(
                        "12.61154271",
                        "77.08181494",
                        search,
                        sortBy, orderBy
                    )
                    _response.value = mandiData.data.records
                    _loading.value = Constants.ApiStatus.DONE
                    _status.value = mandiData.status
                } catch (e: Exception) {

                    _status.value = "Failed"
                    _loading.value = Constants.ApiStatus.ERROR
                }
            }

        }
    }