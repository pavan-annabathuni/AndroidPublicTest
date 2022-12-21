package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName

data class DashboardEntity(
    var id: Int? = null,
    var name: String? = null,
    var contact: String? = null,
    var email: String? = null,
    var emailVerifiedAt: String? = null,
    var approved: Int? = null,
    var settings: String? = null,
    var subscription: SubscriptionEntity? = SubscriptionEntity()
)

data class SubscriptionEntity(

    var iot: Boolean? = null

)
