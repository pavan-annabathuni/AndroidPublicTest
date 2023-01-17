package com.example.profile.viewModel

import androidx.lifecycle.*
import com.example.profile.apiService.profilePic.profile_pic
import com.example.profile.apiService.response.profile
import com.example.profile.apiService.userResponse.Data
import com.example.profile.apiService.userResponse.Profile
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.repository.GeocodeRepository
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.ProfileRepository
import com.waycool.data.repository.domainModels.GeocodeDomain
import com.waycool.data.utils.Resource
import okhttp3.MultipartBody

class EditProfileViewModel:ViewModel() {

    private val _response3 = MutableLiveData<Profile>()
    val response3: LiveData<Profile>
        get() = _response3


    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

      fun getProfileRepository(field: Map<String,String>):LiveData<Resource<com.waycool.data.Network.NetworkModels.ProfileUpdateResponseDTO?>> =
        ProfileRepository.updateProfile(field).asLiveData()

     fun getUserProfileDetails():LiveData<Resource<UserDetailsDomain?>> =
        ProfileRepository.getUserProfileDet().asLiveData()

     fun getUserProfilePic(file:MultipartBody.Part):LiveData<Resource<profilePicModel?>> =
        ProfileRepository.getUserProfilePic(file).asLiveData()

    fun getReverseGeocode(latlon:String):LiveData<GeocodeDomain> = GeocodeRepository.getReverseGeocode(latlon).asLiveData()

    fun updateFarmSupport(account_id: Int,name: String,contact:Long,
    lat:Double,lon:Double, roleId:Int,
    pincode:Int,village: String,address: String,
    state: String,district: String):LiveData<Resource<FarmSupportModel?>> =
        ProfileRepository.updateFarmSupport(account_id,name,contact,lat,lon,roleId,pincode,
            village,address,state,district).asLiveData()

    fun getFarmSupport(accountId:Int): LiveData<Resource<GetFarmSupport?>> =
         ProfileRepository.getFarmSupport(accountId).asLiveData()

    fun deleteFarmSupport(userId:Int):LiveData<Resource<DeleteFarmSupport?>> =
        ProfileRepository.deleteFarmSupport(userId).asLiveData()

    fun setSelectedLanguage(langCode: String?, langId: Int?,language:String?) {
        LoginRepository.setSelectedLanguageCode(langCode, langId,language)
    }

}