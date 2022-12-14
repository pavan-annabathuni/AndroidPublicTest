package com.waycool.iwap.premium

import com.waycool.data.repository.domainModels.MyFarmsDomain

interface farmdetailslistener {
    fun farmDetails(data: MyFarmsDomain)
}