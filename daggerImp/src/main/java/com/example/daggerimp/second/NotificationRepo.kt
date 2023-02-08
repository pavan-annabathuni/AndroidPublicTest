package com.example.daggerimp.second

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
abstract class NotificationRepo {

    @Named("message")
    @Provides
    fun getMessage(): NotificationService {
        return MessageService()
    }

    @Named("email")
    @Provides
    fun getEmail(emailService: EmailService): NotificationService {
       return emailService
    }

}
