package com.waycool.featurelogin

import com.waycool.data.Repository.LoginRepository

object FeatureLogin {

    suspend fun getLoginStatus(): Boolean = LoginRepository.getIsLoggedIn()
}