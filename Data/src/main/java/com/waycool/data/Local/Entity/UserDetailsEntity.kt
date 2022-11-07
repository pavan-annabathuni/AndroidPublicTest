package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName

data class UserDetailsEntity(
    var id: Int? = null,
    var name: String? = null,
    var contact: String? = null,
    var encryptedToken: String? = null,
    var email: String? = null,
    var approved: Int? = null,
    var orgCodeId: String? = null,
    var profile: ProfileEntity? = ProfileEntity(),
    var account: ArrayList<AccountEntity> = arrayListOf()
)

data class AccountEntity(

    var id: Int? = null,
    var accountNo: String? = null,
    var accountType: String? = null,
    var isActive: Int? = null

)


data class ProfileEntity(

    var id: Int? = null,
    var gender: String? = null,
    var address: String? = null,
    var village: String? = null,
    var pincode: String? = null,
    var lat: String? = null,
    var long: String? = null,
    var profilePic: String? = null,
    var userId: Int? = null,
    var langId: Int? = null,
    var stateId: String? = null,
    var districtId: String? = null,
    var subDistrictId: String? = null

)
