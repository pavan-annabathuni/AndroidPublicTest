package com.example.soiltesting.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Constant {
    val TAG="ClassName"
    fun changeDateFormatSpraying(d: String?): String? {
        if (d == null || d.isEmpty()) return null
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH", Locale.ENGLISH)
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