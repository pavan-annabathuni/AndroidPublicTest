package com.example.soiltesting.ui.checksoil

import com.example.soiltesting.model.checksoil.Data
import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.Repository.DomainModels.CheckSoilTestDomain

interface CheckSoilTestListener {


    fun checkBoxSoilTest(data: CheckSoilTestDomain)
}