package com.example.soiltesting.ui.tracker

import com.example.soiltesting.model.tracker.DataX
import com.waycool.data.Repository.DomainModels.TrackerDemain

interface FeedbackListerner {
    fun feedbackApiListener(dataX: TrackerDemain)
}