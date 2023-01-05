package com.example.profile.viewModel

import androidx.lifecycle.*
import com.example.profile.apiService.ProfileApi
import com.example.profile.apiService.profilePic.profile_pic
import com.example.profile.apiService.response.profile
import com.example.profile.apiService.userResponse.Data
import com.example.profile.apiService.userResponse.Profile
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.repository.GeocodeRepository
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.ProfileRepository
import com.waycool.data.repository.domainModels.GeocodeDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileViewModel:ViewModel() {
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _response = MutableLiveData<profile>()
    val response: LiveData<profile>
        get() = _response

    private val _response2 = MutableLiveData<Data>()
    val response2: LiveData<Data>
        get() = _response2

    private val _response3 = MutableLiveData<Profile>()
    val response3: LiveData<Profile>
        get() = _response3

    private val _responsePic = MutableLiveData<profile_pic>()
    val responsePic: LiveData<profile_pic>
        get() = _responsePic

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

     suspend fun getProfileRepository(field: Map<String,String>):LiveData<Resource<com.waycool.data.Network.NetworkModels.profile?>> =
        ProfileRepository.updateProfile(field).asLiveData()

    suspend fun getUserProfileDetails():LiveData<Resource<UserDetailsDTO?>> =
        ProfileRepository.getUserProfileDet().asLiveData()

    suspend fun getUserProfilePic(file:MultipartBody.Part):LiveData<Resource<profilePicModel?>> =
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