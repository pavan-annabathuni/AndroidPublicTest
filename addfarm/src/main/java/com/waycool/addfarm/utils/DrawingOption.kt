
package com.waycool.addfarm.utils

import android.os.Parcel
import android.os.Parcelable

class DrawingOption : Parcelable {
    var locationLatitude: Double?
    var locationLongitude: Double?
    var zoom: Float
    var fillColor: Int
    var strokeColor: Int
    var strokeWidth: Int
    var enableSatelliteView: Boolean?
    var requestGPSEnabling: Boolean?
    var enableCalculateLayout: Boolean?
    var drawingType: DrawingType?

    enum class DrawingType {
        POLYGON, POLYLINE, POINT
    }

    constructor(
        locationLatitude: Double?,
        locationLongitude: Double?,
        zoom: Float,
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Int,
        enableSatelliteView: Boolean?,
        requestGPSEnabling: Boolean?,
        enableCalculateLayout: Boolean?,
        drawingType: DrawingType?
    ) {
        this.locationLatitude = locationLatitude
        this.locationLongitude = locationLongitude
        this.zoom = zoom
        this.fillColor = fillColor
        this.strokeColor = strokeColor
        this.strokeWidth = strokeWidth
        this.enableSatelliteView = enableSatelliteView
        this.requestGPSEnabling = requestGPSEnabling
        this.enableCalculateLayout = enableCalculateLayout
        this.drawingType = drawingType
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(locationLatitude)
        dest.writeValue(locationLongitude)
        dest.writeFloat(zoom)
        dest.writeInt(fillColor)
        dest.writeInt(strokeColor)
        dest.writeInt(strokeWidth)
        dest.writeValue(enableSatelliteView)
        dest.writeValue(requestGPSEnabling)
        dest.writeValue(enableCalculateLayout)
        dest.writeInt(if (drawingType == null) -1 else drawingType!!.ordinal)
    }

    protected constructor(`in`: Parcel) {
        locationLatitude = `in`.readValue(Double::class.java.classLoader) as Double?
        locationLongitude = `in`.readValue(Double::class.java.classLoader) as Double?
        zoom = `in`.readFloat()
        fillColor = `in`.readInt()
        strokeColor = `in`.readInt()
        strokeWidth = `in`.readInt()
        enableSatelliteView = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
        requestGPSEnabling = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
        enableCalculateLayout = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
        val tmpDrawingType = `in`.readInt()
        drawingType = if (tmpDrawingType == -1) null else DrawingType.values()[tmpDrawingType]
    }

    companion object CREATOR : Parcelable.Creator<DrawingOption> {
        override fun createFromParcel(parcel: Parcel): DrawingOption {
            return DrawingOption(parcel)
        }

        override fun newArray(size: Int): Array<DrawingOption?> {
            return arrayOfNulls(size)
        }
    }
}