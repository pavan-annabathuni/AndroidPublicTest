package com.waycool.data.repository

import com.waycool.data.Network.NetworkModels.GetFarmSupport
import com.waycool.data.Network.NetworkModels.NotificationModel
import com.waycool.data.Network.NetworkModels.UpdateNotification
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow

object NotificationRepository {
    fun getNotification(): Flow<Resource<NotificationModel?>> {
        return NetworkSource.getNotification()
    }

    fun updateNotification(id:String): Flow<Resource<UpdateNotification?>> {
        return NetworkSource.updateNotification(id)
    }
}