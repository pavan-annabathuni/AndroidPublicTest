package com.waycool.squarecamera

import android.app.Activity
import android.content.Context
import android.content.Intent

class SquareCamera(builder: Builder) {

    companion object {
        const val REQUEST_CODE = 11214
    }

    init {
        val intent = Intent(builder.context, SquareCameraActivity::class.java)
        (builder.context as Activity).startActivityForResult(intent, REQUEST_CODE)
    }
    class Builder {
        var context: Context? = null
        fun with(context: Context) = apply { this.context = context as Activity }
        fun launch() = SquareCamera(this)
    }
}