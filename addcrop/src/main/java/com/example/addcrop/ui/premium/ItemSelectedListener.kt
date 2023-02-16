package com.example.addcrop.ui.premium

import com.waycool.data.repository.domainModels.VarietyCropDomain

interface ItemSelectedListener {
    fun clickOnCategory(name: VarietyCropDomain)
}