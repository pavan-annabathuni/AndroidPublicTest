package com.waycool.data.Repository.DomainModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class VansFeederDomain(

    var currentPage: Int? = null,
    var vansFeederList: List<VansFeederListDomain> = arrayListOf(),
    var total: Int? = null

)

@Parcelize
data class VansFeederListDomain(

     var id            : Int?            = null,
     var vansType      : String?         = null,
     var title         : String?         = null,
     var desc          : String?         = null,
     var contentUrl    : String?         = null,
     var thumbnailUrl  : String?         = null,
     var isActive      : Int?            = null,
     var audioUrl      : String?         = null,
     var startDate     : String?         = null,
     var endDate       : String?         = null,
     var priorityOrder : String?         = null,
     var sourceName    : String?         = null,
     var platform      : String?         = null,
     var platformType  : String?         = null,
     var createdAt     : String?         = null,
     var updatedAt     : String?         = null,
     var deletedAt     : String?         = null,
     var langId        : Int?            = null,
     var categoryId    : Int?            = null,
     var cropId        : Int?            = null,
     var tags          : List<TagsAndKeywordsDomain> = emptyList()

):Parcelable{

}

//data class TagsDomain(
//    var id: Int? = null,
//    var tagName: String? = null,
//    var translation: String? = null
//
//)
