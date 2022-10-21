package com.example.profile.apiService.userResponse

data class Data(
    val account: List<Account>?,
    val approved: Int?,
    val contact: String?,
    val email: String?,
    val email_verified_at: Any?,
    val id: Int?,
    val name: String?,
    val org_code_id: Any?,
    val profile: Profile?,
    val settings: Any?
)