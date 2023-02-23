package com.waycool.data.utils

import android.content.Context
import android.widget.Toast
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppUtils {

    fun networkErrorStateTranslations(apiErrorHandlingBinding: com.waycool.uicomponents.databinding.ApiErrorHandlingBinding) {
        TranslationsManager().loadString("txt_internet_problem",apiErrorHandlingBinding.tvInternetProblem,"There is a problem with Internet.")
        TranslationsManager().loadString("txt_check_net",apiErrorHandlingBinding.tvCheckInternetConnection,"Please check your Internet connection")
        TranslationsManager().loadString("txt_tryagain",apiErrorHandlingBinding.tvTryAgainInternet,"TRY AGAIN")
    }

    fun translatedToastServerErrorOccurred(context: Context?) {
        CoroutineScope(Dispatchers.Main).launch {
            val toastServerError = TranslationsManager().getString("server_error")
            if(!toastServerError.isNullOrEmpty()){
                context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                    Toast.LENGTH_SHORT
                ) }}
            else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Server Error Occurred",
                Toast.LENGTH_SHORT
            ) }}}
    }
    fun translatedToastLoading(context: Context?) {
        CoroutineScope(Dispatchers.Main).launch {
            val toastLoading = TranslationsManager().getString("loading")
            if(!toastLoading.isNullOrEmpty()){
                context?.let { it1 -> ToastStateHandling.toastWarning(it1,toastLoading,
                    Toast.LENGTH_SHORT
                ) }}
            else {context?.let { it1 -> ToastStateHandling.toastWarning(it1,"Loading",
                Toast.LENGTH_SHORT
            ) }}}
    }
    fun translatedToastCheckInternet(context: Context?) {
        CoroutineScope(Dispatchers.Main).launch {
            val toastCheckInternet = TranslationsManager().getString("check_your_interent")
            if(!toastCheckInternet.isNullOrEmpty()){
                context?.let { it1 -> ToastStateHandling.toastError(it1,toastCheckInternet,
                    Toast.LENGTH_SHORT
                ) }}
            else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please check your internet connection",
                Toast.LENGTH_SHORT
            ) }}}
    }
}