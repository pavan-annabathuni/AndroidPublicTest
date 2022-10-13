package com.waycool.data.Repository.DomainModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TagsAndKeywordsDomain(
    var id: Int = 0,
    var tag_name: String? = null,
    var translation: String? = null
):Parcelable{

}

