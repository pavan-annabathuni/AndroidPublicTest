package com.example.soiltesting.model.feedback

data class FeedbackRequest(
    val user_id: Int,
    val module: String,
    val screen: String,
    val feedback: String,
    val desc: String

)