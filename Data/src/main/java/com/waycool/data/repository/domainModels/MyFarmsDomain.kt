package com.waycool.data.repository.domainModels

import com.google.android.libraries.maps.model.LatLng


data class MyFarmsDomain (
   var id               : Int?    = null,
   var farmName         : String? = null,
   var farmCenter       : ArrayList<LatLng>?= null,
   var farmArea         : String? = null,
   var farmJson         : ArrayList<LatLng>?= null,
   var farmWaterSource  : String? = null,
   var farmPumpHp       : String? = null,
   var farmPumpType     : String? = null,
   var farmPumpDepth    : String? = null,
   var farmPumpPipeSize : String? = null,
   var farmPumpFlowRate : String? = null,
   var isPrimary        : Int?    = null,
   var updatedAt        : String? = null,
   var accountNoId      : Int?    = null

)

