package com.waycool.iwap.premium

import com.waycool.data.repository.domainModels.MyFarmsDomain

interface FarmSelectedListener {
    fun onFarmSelected(data: MyFarmsDomain)
}