package com.example.daggerimp.first

import android.util.Log
import javax.inject.Inject


//DI without Dagger
/*class EmailService {
    fun send(to: String, from: String, subject: String) {
        Log.d("msg", "send")
    }
}*/


//DI with Dagger
class EmailServices @Inject constructor(){
    fun send(to: String, from: String, subject: String) {
        Log.d("msg", "send")
    }
}