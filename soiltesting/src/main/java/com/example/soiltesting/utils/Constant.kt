package com.example.soiltesting.utils

import com.example.soiltesting.model.history.SoilHistory
import com.google.android.material.tabs.TabLayout

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
}