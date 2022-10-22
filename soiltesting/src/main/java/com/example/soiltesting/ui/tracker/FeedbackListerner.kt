package com.example.soiltesting.ui.tracker

import com.waycool.data.repository.domainModels.TrackerDemain

interface FeedbackListerner {
    fun feedbackApiListener(dataX: TrackerDemain)
}