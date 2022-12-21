package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName


data class ModuleMasterEntity(
   var id                 : Int?    = null,
   var moduleType         : String? = null,
   var moduleIcon         : String? = null,
   var moduleDesc         : String? = null,
   var subscription       : Int?    = null,
   var title              : String? = null,
   var audioUrl           : String? = null,
   var mobileDisplay      : Int?    = null,
   var translation        : String? = null,
   var updatedAt          : String? = null

)


