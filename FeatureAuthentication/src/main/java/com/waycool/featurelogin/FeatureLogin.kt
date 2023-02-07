package com.waycool.featurelogin

import com.waycool.data.repository.LoginRepository

object FeatureLogin {
    suspend fun getLoginStatus(): Boolean = LoginRepository.getIsLoggedIn()
}