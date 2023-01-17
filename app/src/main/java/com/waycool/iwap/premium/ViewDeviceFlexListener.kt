package com.waycool.iwap.premium

import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.repository.domainModels.ViewDeviceDomain

interface ViewDeviceFlexListener {
    fun viewDevice(data: ViewDeviceDomain)
}