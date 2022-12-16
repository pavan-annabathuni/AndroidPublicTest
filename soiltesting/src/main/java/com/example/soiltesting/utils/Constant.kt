package com.example.soiltesting.utils

import com.example.soiltesting.model.history.SoilHistory
import com.google.android.material.tabs.TabLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Constant {
    val TAG="ClassName"
    fun loadTabs(tabLayout: TabLayout, tabItems: List<SoilHistory>) {
        tabItems.forEach { tabData ->
            tabLayout.newTab().also { tab ->
                tab.text = tabData.data.toString()
                tabLayout.addTab(tab)
            }
        }
    }
    fun changeDateFormatSpraying(d: String?): String? {
        if (d == null || d.isEmpty()) return null
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        //        SimpleDateFormat outputFormat1 = new SimpleDateFormat("hh:mm aa");
        val outputFormat2 = SimpleDateFormat("hh a", Locale.ENGLISH)
        return try {
            val date = dateFormat.parse(d)
            outputFormat2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            d
        }
    }

}