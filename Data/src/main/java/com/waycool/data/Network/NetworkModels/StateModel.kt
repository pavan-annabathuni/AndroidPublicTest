package com.waycool.data.Network.NetworkModels

data class StateModel(
    val `data`: List<StateData>,
    val message: String,
    val status: Boolean
)

data class StateData(
    val native_name: String,
    val state_code: Int,
    val state_name: String
)
