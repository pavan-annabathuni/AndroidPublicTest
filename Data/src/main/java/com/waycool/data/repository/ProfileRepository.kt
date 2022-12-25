package com.waycool.data.repository

import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

object ProfileRepository {

   suspend fun updateProfile(field:Map<String,String>): Flow<Resource<profile?>> {
       val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
       return NetworkSource.updateProfile(map,field)
    }

    suspend fun getUserProfileDet(): Flow<Resource<UserDetailsDTO?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getUserProfile(map)
    }

    suspend fun getUserProfilePic(file: MultipartBody.Part): Flow<Resource<profilePicModel?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getUserProfilePic(map,file)
    }
    fun updateFarmSupport(name: String,contact:Long,
                          lat:Double,lon:Double, roleId:Int,
                          pincode:Int,village: String,address: String,
                          state: String,district: String):Flow<Resource<FarmSupportModel?>>{
        return NetworkSource.updateFarmSupport(name,contact,lat,lon,roleId,pincode,
            village,address,state,district)
    }
    fun getFarmSupport(accountId:Int): Flow<Resource<GetFarmSupport?>> {
        return NetworkSource.getFarmSupport(accountId)
    }
    fun deleteFarmSupport(userId:Int):Flow<Resource<DeleteFarmSupport?>>{
        return NetworkSource.deleteFarmSupport(userId)
    }
}