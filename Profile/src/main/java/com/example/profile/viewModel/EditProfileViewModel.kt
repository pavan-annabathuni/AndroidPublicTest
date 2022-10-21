package com.example.profile.viewModel

import androidx.lifecycle.*
import com.example.profile.apiService.ProfileApi
import com.example.profile.apiService.profilePic.profile_pic
import com.example.profile.apiService.response.profile
import com.example.profile.apiService.userResponse.Data
import com.example.profile.apiService.userResponse.Profile
import com.waycool.data.Network.NetworkModels.UserDetailsDTO
import com.waycool.data.Network.NetworkModels.profilePicModel
import com.waycool.data.Repository.DomainModels.UserDetailsDomain
import com.waycool.data.Repository.LoginRepository
import com.waycool.data.Repository.ProfileRepository
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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

     suspend fun getProfileRepository(name:String,address:String, village:String, pincode:String, state:String,
                                      district:String):LiveData<Resource<com.waycool.data.Network.NetworkModels.profile?>> =
        ProfileRepository.updateProfile(name,address,village,pincode,state,district).asLiveData()

    suspend fun getUserProfileDetails():LiveData<Resource<UserDetailsDTO?>> =
        ProfileRepository.getUserProfileDet().asLiveData()

    suspend fun getUserProfilePic(file:MultipartBody.Part):LiveData<Resource<profilePicModel?>> =

        ProfileRepository.getUserProfilePic(file).asLiveData()

    fun getProfile(name:String,
        address:String,village:String,pincode:String,state:String,district:String){
        val filter = HashMap<String,String>()
        filter.put("name",name)
        filter.put("address",address)
        filter.put(("village"),village)
        filter.put("pincode",pincode)
        filter.put("state",state)
        filter.put("district",district)
        filter.put("_method","PUT")
       // filter.put(("profile_pic"),data.toString())

//        val file = File(data)
//        val request:RequestBody = RequestBody.create(
//        ("multipart/form-data".toMediaTypeOrNull()),file)
//        val body: MultipartBody.Part = MultipartBody.Part.createFormData(
//            "profile_pic",file.name,request)
        viewModelScope.launch {

            try {
                val videoData = ProfileApi.retrofitService.updateProfile(filter)
                _response.value = videoData
              //  _status.value = "SUCCESS"
            } catch (e: Exception) {
                _status.value = "FAILED$e"
            }
        }


    }

//    fun getUsers(){
//        viewModelScope.launch {
//            val userData = ProfileApi.retrofitService.getProfile()
//            _response2.value = userData?.data
//            //_status.value = "SUCCESS"
//        }
//    }

    fun getUserProfile(){
        viewModelScope.launch {
            val userData = ProfileApi.retrofitService.getProfile()
            _response3.value = userData?.data?.profile
            _status.value = "SUCCESS"
        }
    }

//    fun getUserProfilePic2(file:File){
//
//                val file = File(data)
//        val request:RequestBody = RequestBody.create(
//        ("multipart/form-data".toMediaTypeOrNull()),file)
//        val body: MultipartBody.Part = MultipartBody.Part.createFormData(
//            "profile_pic",file.name,request)
//        viewModelScope.launch {
//            val userData = ProfileApi.retrofitService.getProfilePic(body)
//            _responsePic.value = userData
//            _status.value = "SUCCESS"
//        }
//    }

}