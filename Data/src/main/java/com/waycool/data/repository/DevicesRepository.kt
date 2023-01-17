package com.waycool.data.repository

import android.provider.MediaStore.Video
import com.waycool.data.Sync.syncer.ViewDevicesSyncer
import com.waycool.data.repository.DomainMapper.ViewDeviceDomainMapper
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DevicesRepository {

    fun getAllIotDevice(): Flow<Resource<List<ViewDeviceDomain>?>> {
        return ViewDevicesSyncer.getAllDeviceData().map {
            when(it){
                is Resource.Success->{
                    Resource.Success(
                        ViewDeviceDomainMapper().toDomainList(it.data?: emptyList())
                    )
                }
                is Resource.Loading->{
                    Resource.Loading()
                }
                is Resource.Error->{
                    Resource.Error(it.message)
                }
            }
        }
    }


    fun getIotDeviceByFarm(farmId:Int): Flow<Resource<List<ViewDeviceDomain>?>> {
        return ViewDevicesSyncer.getDeviceDataByFarm(farmId).map {
            when(it){
                is Resource.Success->{
                    Resource.Success(
                        ViewDeviceDomainMapper().toDomainList(it.data?: emptyList())
                    )
                }
                is Resource.Loading->{
                    Resource.Loading()
                }
                is Resource.Error->{
                    Resource.Error(it.message)
                }
            }
        }
    }

    fun getLatestTimeStamp() =
        ViewDevicesSyncer.getLastUpdated()

    fun refreshDevices() {
        ViewDevicesSyncer.downloadDevices()
    }


}