package com.example.daggerimp.second

import android.util.Log
import javax.inject.Inject

interface NotificationService{
    fun send(to: String, from: String, subject: String) {
    }
}

class EmailService @Inject constructor():NotificationService{
    override fun send(to: String, from: String, subject: String) {
        Log.d("msg","sent in email")

    }
}

class MessageService:NotificationService{
    override fun send(to: String, from: String, subject: String) {
        Log.d("msg","sent in message ")

    }
}