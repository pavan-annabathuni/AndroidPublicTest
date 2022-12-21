package com.example.soiltesting.ui.checksoil

import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.repository.domainModels.CheckSoilTestDomain

interface CheckSoilTestListener {


    fun checkBoxSoilTest(data: CheckSoilTestDomain)
}