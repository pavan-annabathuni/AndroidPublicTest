package com.example.addcrop.ui.selectcrop

import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DebouncedClickListener(val debounceInterval: Long, val onClick: () -> Unit) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceInterval) {
            return
        }
        lastClickTime = currentTime

        GlobalScope.launch {
            // Debounce logic
            delay(debounceInterval)
            onClick()
        }
    }
}