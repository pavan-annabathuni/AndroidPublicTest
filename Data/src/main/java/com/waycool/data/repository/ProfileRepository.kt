package com.waycool.data.repository

import android.util.Log
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.syncer.UserDetailsSyncer
import com.waycool.data.repository.DomainMapper.UserDetailsDomainMapper
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody

object ProfileRepository {

   suspend fun updateProfile(field:Map<String,String>): Flow<Resource<profile?>> {
       val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
       return NetworkSource.updateProfile(map,field)
    }

    fun getUserProfileDet(): Flow<Resource<UserDetailsDomain?>> {
        return UserDetailsSyncer.getData().map {
            when (it) {
                is Resource.Success -> {
                    Log.d("TAG", "getUserDetailsAccountID:${it.data} ")
                    Resource.Success(UserDetailsDomainMapper().mapToDomain(it.data!!))
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }

    suspend fun getUserProfilePic(file: MultipartBody.Part): Flow<Resource<profilePicModel?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getUserProfilePic(map,file)
    }
    fun updateFarmSupport(account_id: Int,name: String,contact:Long,
                          lat:Double,lon:Double, roleId:Int,
                          pincode:Int,village: String,address: String,
                          state: String,district: String):Flow<Resource<FarmSupportModel?>>{
        return NetworkSource.updateFarmSupport(account_id,name,contact,lat,lon,roleId,pincode,
            village,address,state,district)
    }
    fun getFarmSupport(accountId:Int): Flow<Resource<GetFarmSupport?>> {
        return NetworkSource.getFarmSupport(accountId)
    }
    fun deleteFarmSupport(userId:Int):Flow<Resource<DeleteFarmSupport?>>{
        return NetworkSource.deleteFarmSupport(userId)
    }
}