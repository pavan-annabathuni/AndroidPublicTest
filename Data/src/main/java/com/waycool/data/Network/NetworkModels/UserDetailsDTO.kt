package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class UserDetailsDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: UserDetailsData? = UserDetailsData()

)

data class UserDetailsData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("contact") var contact: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("approved") var approved: Int? = null,
    @SerializedName("org_code_id") var orgCodeId: String? = null,
    @SerializedName("profile") var profile: ProfileNetwork? = ProfileNetwork(),
    @SerializedName("account") var account: List<AccountNetwork> = arrayListOf()

)

data class AccountNetwork(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("account_no") var accountNo: String? = null,
    @SerializedName("account_type") var accountType: String? = null,
    @SerializedName("is_active") var isActive: Int? = null

)


data class ProfileNetwork(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("village") var village: String? = null,
    @SerializedName("pincode") var pincode: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("long") var long: String? = null,
    @SerializedName("profile_pic") var profilePic: String? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("lang_id") var langId: Int? = null,
    @SerializedName("state") var stateId: String? = null,
    @SerializedName("district") var districtId: String? = null,
    @SerializedName("sub_district_id") var subDistrictId: String? = null

)