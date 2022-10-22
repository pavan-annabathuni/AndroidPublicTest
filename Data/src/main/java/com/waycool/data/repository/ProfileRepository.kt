package com.waycool.data.repository

import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.UserDetailsDTO
import com.waycool.data.Network.NetworkModels.profile
import com.waycool.data.Network.NetworkModels.profilePicModel
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

object ProfileRepository {

   suspend fun updateProfile(name:String,address:String,village:String,pincode:String,state:String,district:String): Flow<Resource<profile?>> {
       val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
       return NetworkSource.updateProfile(name,address,village,pincode,state,district,map)
    }

    suspend fun getUserProfileDet(): Flow<Resource<UserDetailsDTO?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getUserProfile(map)
    }

    suspend fun getUserProfilePic(file: MultipartBody.Part): Flow<Resource<profilePicModel?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getUserProfilePic(map,file)
    }
}