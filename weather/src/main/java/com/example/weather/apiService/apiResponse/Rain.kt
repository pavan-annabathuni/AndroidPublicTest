package com.example.weather.apiService.apiResponse

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rain(
    val `1h`: Double
):Parcelable