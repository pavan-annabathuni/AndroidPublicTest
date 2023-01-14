package com.waycool.data.repository.domainModels

import android.os.Parcelable
import com.google.android.libraries.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyFarmsDomain (
   var id               : Int?    = null,
   var farmName         : String? = null,
   var farmLocation     : String? = null,
   var farmCenter       : ArrayList<LatLng>?= null,
   var farmArea         : String? = null,
   var farmJson         : ArrayList<LatLng>?= null,
   var farmWaterSource  : ArrayList<String>? = null,
   var farmPumpHp       : String? = null,
   var farmPumpType     : String? = null,
   var farmPumpDepth    : String? = null,
   var farmPumpPipeSize : String? = null,
   var farmPumpFlowRate : String? = null,
   var isPrimary        : Int?    = null,
   var updatedAt        : String? = null,
   var accountNoId      : Int?    = null

):Parcelable

