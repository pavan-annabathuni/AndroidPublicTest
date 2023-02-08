package com.example.daggerimp.second

import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    fun registerUser(email: String, password: String) {
        userService.saveUser(email, password)
        notificationService .send(email, "abc@waycool.in", "User registered")
    }
}