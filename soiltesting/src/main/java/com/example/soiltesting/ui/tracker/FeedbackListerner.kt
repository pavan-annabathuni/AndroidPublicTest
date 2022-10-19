package com.example.soiltesting.ui.tracker

import com.example.soiltesting.model.tracker.DataX

interface FeedbackListerner {
    fun feedbackApiListener(dataX: DataX)
}