package com.waycool.uicomponents.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtils {

    fun dateFormatterDevice(mDate: String?): String? {
        if (mDate == null || mDate.isEmpty()) return null
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
        val outputFormat2 = SimpleDateFormat("EEE dd',' MMM hh:mm aa", Locale.ENGLISH)
        return try {
            val date = dateFormat.parse(mDate)
            outputFormat2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            mDate
        }
    }
}