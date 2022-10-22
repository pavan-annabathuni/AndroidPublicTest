package com.example.soiltesting.ui.history

import com.waycool.data.repository.domainModels.SoilTestHistoryDomain

interface StatusTrackerListener {
    fun statusTracker(data: SoilTestHistoryDomain)

}