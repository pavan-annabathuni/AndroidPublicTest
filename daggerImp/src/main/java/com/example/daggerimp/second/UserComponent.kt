package com.example.daggerimp.second

import com.example.daggerimp.first.MainsActivity
import dagger.Component

@Component(modules = [UserModule::class, NotificationService::class])
interface UserComponent {
    fun inject(mainsActivity: MainsActivity)
}