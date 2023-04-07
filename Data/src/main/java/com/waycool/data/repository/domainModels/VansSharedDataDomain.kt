package com.waycool.data.repository.domainModels

import androidx.annotation.Keep


@Keep
data class VansSharedDataDomain(
    var id: Int? = null,
    var vans_type: String? = null,
    var title: String? = null,
    var desc: String? = null,
    var content_url: String? = null,
    var thumbnail_url: String? = null,
    var audio_url: String? = null,
    var source_name: String? = null,
    var category_id: Int? = null,
    var crop_id: Int? = null,
    var updated_at: String? = null
)