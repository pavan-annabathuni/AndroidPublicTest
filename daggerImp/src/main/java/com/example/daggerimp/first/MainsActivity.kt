package com.example.daggerimp.first

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daggerimp.DaggerUserRegistrationComponent
import com.example.daggerimp.R
import javax.inject.Inject

class MainsActivity : AppCompatActivity() {

    @Inject lateinit var userRegistrationService: UserRegistrationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mains)

        //No DI
        /**val userRegistrationService=UserRegistrationService()
        userRegistrationService.registerUser("xyz@outgrow.com","123456" )*/

        //Manual dependency injection
        //Where ever I will need the UserRegistrationService object I have to create object of UserRepository and EmailService
        //i.e Repetition of code will be happening
        /**
        val userRepository=UserRepository()
        val emailService=EmailService()
        val userRegistrationService=UserRegistrationService(userRepository,emailService)
        userRegistrationService.registerUser("xyz@outgrow.com","123456" )
         */

        //Now we will ask Dagger to provide us the objects so the required dependency to provide that object will be managed by Dagger itself
        //As a developer we tell 2 things to dagger i.e. how to create an object and where to consume it
        /**
        val component = DaggerUserRegistrationComponent.builder().build()
        val userRegistrationService = DaggerUserRegistrationComponent.builder().build().userRegistrationService()
        userRegistrationService.registerUser("xyz@outgrow.com","123456" )
         */


        //field injection
        val component = DaggerUserRegistrationComponent.builder().build()
        component.inject(this)
        userRegistrationService.registerUser("xyz@outgrow.com","123456" )
    }
}