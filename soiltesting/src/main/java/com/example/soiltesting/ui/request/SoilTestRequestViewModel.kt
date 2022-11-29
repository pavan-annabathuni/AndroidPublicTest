package com.example.soiltesting.ui.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.SoilTestResponseDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.GeocodeRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.ProfileRepository
import com.waycool.data.repository.domainModels.GeocodeDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource

class SoilTestRequestViewModel :ViewModel() {

    fun postNewSoil(account_id: Int,lat: Double,long: Double,org_id:Int,plot_no:String,pincode:String,address:String,
                    state:String,district:String ,number:String): LiveData<Resource<SoilTestResponseDTO?>> =
        CropsRepository.postNewSoil(account_id,lat,long,org_id,plot_no,pincode,address,state,district,number).asLiveData()
//    suspend fun getUserProfileDetails():LiveData<Resource<UserDetailsDTO?>> =
//        ProfileRepository.getUserProfileDet().asLiveData()
fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
    LoginRepository.getUserDetails().asLiveData()


    fun getReverseGeocode(latlon:String):LiveData<GeocodeDomain> = GeocodeRepository.getReverseGeocode(latlon).asLiveData()



}