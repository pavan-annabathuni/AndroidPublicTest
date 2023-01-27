package com.waycool.data.repository.domainModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagsAndKeywordsDomain(
    var id: Int = 0,
    var tag_name: String? = null,
    var translation: String? = null
):Parcelable{

}

