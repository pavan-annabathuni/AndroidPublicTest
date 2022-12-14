package com.waycool.data.repository

import com.waycool.data.Network.NetworkModels.GetFarmSupport
import com.waycool.data.Network.NetworkModels.NotificationModel
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow

object NotificationRepository {
    fun getNotification(): Flow<Resource<NotificationModel?>> {
        return NetworkSource.getNotification()
    }
}