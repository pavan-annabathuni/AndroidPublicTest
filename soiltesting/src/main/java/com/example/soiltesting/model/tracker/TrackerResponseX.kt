package com.example.soiltesting.model.tracker

data class TrackerResponseX(
    val `data`: List<DataX>,
    val message: String,
    val status: Boolean
)