package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.waycool.data.Local.Entity.SubscriptionEntity

@Keep
data class DashboardDomain(
    var id: Int? = null,
    var name: String? = null,
    var contact: String? = null,
    var email: String? = null,
    var emailVerifiedAt: String? = null,
    var approved: Int? = null,
    var settings: String? = null,
    var subscription: SubscriptionDomain? = SubscriptionDomain()
)
@Keep
data class SubscriptionDomain(

    var iot: Boolean? = null

)