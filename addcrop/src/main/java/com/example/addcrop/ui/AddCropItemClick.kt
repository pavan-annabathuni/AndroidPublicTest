package com.example.addcrop.ui

import com.example.addcrop.model.Data
import com.waycool.data.Repository.DomainModels.AddCropTypeDomain

interface AddCropItemClick {
    fun clickOnCategory(name:AddCropTypeDomain)
}