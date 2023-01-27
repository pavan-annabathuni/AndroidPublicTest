package com.example.irrigationplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class IrrigationPlannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_irrigation_planner)
    }

    override fun onResume() {
            super.onResume()
            EventScreenTimeHandling.calculateScreenTime("IrrigationPlannerActivity")

    }
}