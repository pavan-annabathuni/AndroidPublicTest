package com.waycool.iwap.premium

import com.waycool.data.repository.domainModels.MyFarmsDomain

interface Farmdetailslistener {
    fun farmDetails(data: MyFarmsDomain)
}