package com.waycool.data.error

import android.content.Context
import es.dmoral.toasty.Toasty

object ToastStateHandling {

    fun toastSuccess(context: Context, msg: String, duration: Int) {
        Toasty.Config.getInstance()
             .tintIcon(true)
             .setTextSize(14)
            .supportDarkTheme(false)
             .apply()
         Toasty.success(context,msg,duration).show()

    }
    fun toastError(context: Context, msg: String, duration: Int) {
        Toasty.Config.getInstance()
            .tintIcon(true)
            .setTextSize(14)
            .supportDarkTheme(false)
            .apply()
        Toasty.error(context,msg,duration).show()

    }

    fun toastWarning(context: Context, msg: String, duration: Int) {
        Toasty.Config.getInstance()
            .tintIcon(true)
            .setTextSize(14)
            .supportDarkTheme(false)
            .apply()
        Toasty.warning(context,msg,duration).show()

    }

    }


