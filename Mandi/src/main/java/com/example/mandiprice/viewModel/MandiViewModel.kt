package com.example.mandiprice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waycool.data.Network.NetworkModels.MandiMasterModel
import com.waycool.data.Network.NetworkModels.StateModel
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.MandiRepository
import com.waycool.data.repository.VansRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import kotlinx.coroutines.GlobalScope
import kotlin.collections.List
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class MandiViewModel : ViewModel() {


    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }
    fun getAllCrops():LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getAllCrops().asLiveData()
    }

    suspend fun getState():LiveData<Resource<StateModel?>> {
        return CropsRepository.getState().asLiveData()
    }


     fun getMandiDetails(lat:String,long:String,crop_category:String?,state:String?,crop:String?,
                                sortBy: String?, orderBy: String?,search:String?,accountId:Int?=null
    ): LiveData<PagingData<MandiDomainRecord>> =
        MandiRepository.getMandiList(lat,long,crop_category,
            state,crop,sortBy,orderBy,search,accountId).cachedIn(viewModelScope).asLiveData()

    fun getMandiSinglePage(lat: String,long: String):LiveData<Resource<MandiDomain?>>{
        return MandiRepository.getMandiSinglePage(lat, long).asLiveData()
    }

    suspend fun getMandiHistoryDetails(crop_master_id:Int?,mandi_master_id:Int?,sub_record_id:String?):
            LiveData<Resource<MandiHistoryDomain?>> =
        MandiRepository.getMandiHistory(crop_master_id,mandi_master_id, sub_record_id).asLiveData()


    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getMandiMaster(): LiveData<Resource<MandiMasterModel?>> =
        MandiRepository.getMandiMaster().asLiveData()


    //Ad Banners
    fun getVansAdsList(moduleId: String): LiveData<Resource<List<VansFeederListDomain>>> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["vans_type"] = "banners"
        queryMap["module_id"] = moduleId

        return VansRepository.getVansFeederSinglePage(queryMap).asLiveData()
    }
}