package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class VansFeederDTO(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: VansNetwork? = VansNetwork()
)

data class VansNetwork(

    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("data") var vansFeederList: List<VansFeederListNetwork> = arrayListOf(),
    @SerializedName("first_page_url") var firstPageUrl: String? = null,
    @SerializedName("from") var from: Int? = null,
    @SerializedName("last_page") var lastPage: Int? = null,
    @SerializedName("last_page_url") var lastPageUrl: String? = null,
    @SerializedName("links") var links: List<LinksNetwork> = arrayListOf(),
    @SerializedName("next_page_url") var nextPageUrl: String? = null,
    @SerializedName("path") var path: String? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("prev_page_url") var prevPageUrl: String? = null,
    @SerializedName("to") var to: Int? = null,
    @SerializedName("total") var total: Int? = null
)

data class LinksNetwork(

    @SerializedName("url") var url: String? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("active") var active: Boolean? = null

)


data class VansFeederListNetwork(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("vans_type") var vansType: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("content_url") var contentUrl: String? = null,
    @SerializedName("thumbnail_url") var thumbnailUrl: String? = null,
    @SerializedName("is_active") var isActive: Int? = null,
    @SerializedName("audio_url") var audioUrl: String? = null,
    @SerializedName("start_date") var startDate: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("priority_order") var priorityOrder: String? = null,
    @SerializedName("source_name") var sourceName: String? = null,
    @SerializedName("platform") var platform: String? = null,
    @SerializedName("platform_type") var platformType: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("lang_id") var langId: Int? = null,
    @SerializedName("category_id") var categoryId: Int? = null,
    @SerializedName("crop_id") var cropId: Int? = null,
    @SerializedName("tags") var tags: List<TagsData> = emptyList()

)

//data class Tags(
//
//    @SerializedName("id") var id: Int? = null,
//    @SerializedName("tag_name") var tagName: String? = null,
//    @SerializedName("translation") var translation: String? = null
//
//)
