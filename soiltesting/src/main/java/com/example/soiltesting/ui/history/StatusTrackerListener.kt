package com.example.soiltesting.ui.history

import com.example.soiltesting.model.history.Data

interface StatusTrackerListener {
    fun statusTracker(data: Data)
}