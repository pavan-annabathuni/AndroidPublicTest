package com.waycool.iwap.premium

import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain

interface ViewDeviceFlexListener {
    fun viewDevice(data: ViewDeviceData)
}