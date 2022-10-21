package com.example.soiltesting.ui.history

import com.example.soiltesting.model.history.Data
import com.waycool.data.Repository.DomainModels.SoilTestHistoryDomain

interface StatusTrackerListener {
    fun statusTracker(data: SoilTestHistoryDomain)

}