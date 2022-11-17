package com.waycool.data.repository.domainModels

data class PestDiseaseDomain(
    var cropId: Int? = null,
    var diseaseName: String? = null,
    var diseaseId: Int? = null,
    var thumb: String? = null,
    var imageUrl: List<String>? = null,
    var audioUrl: String? = null,
    var modeOfInfestation: String? = null,
    var symptoms: String? = null,
    var cultural: String? = null,
    var biological: String? = null,
    var chemical: String? = null
)

