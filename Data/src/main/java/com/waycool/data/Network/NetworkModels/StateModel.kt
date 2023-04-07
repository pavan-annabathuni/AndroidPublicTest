package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep

@Keep
data class StateModel(
    val `data`: List<StateData>,
    val message: String,
    val status: Boolean
)

@Keep
data class StateData(
    val native_name: String,
    val state_code: Int,
    val state_name: String
)
