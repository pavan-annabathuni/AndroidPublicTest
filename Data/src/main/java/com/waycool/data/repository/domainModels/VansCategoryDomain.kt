package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class VansCategoryDomain(

    var id: Int? = null,
    var categoryName: String? = null,
    var vansType: String? = null,
    var translation: String? = null,
    )
