package com.example.daggerimp.first

import javax.inject.Inject


//Unit Testing
//Single Responsibility
// Lifetime of objects
//Extensible
/*No Dependency Injection */
/**class UserRegistrationService  {
    private val userRepository=UserRepository()
    private val emailService=EmailService()

    fun registerUser(email:String,password:String){
        userRepository.saveUser(email,password)
        emailService.send(email,"abc@waycool.in","User registered")
    }
}*/


//Manual Dependency injection using Constructor Injection
/**
class UserRegistrationService(private val userRepository: UserRepository  ,
                              private val emailService: EmailService
) {

    fun registerUser(email: String,  : String) {
        userRepository.saveUser(email, password)
        emailService.send(email, "abc@waycool.in", "User registered")
    }
}*/


//Dependency injection using dagger
class UserRegistrationService @Inject constructor(private val userRepository: UserRepository,
                                                  private val emailService: EmailServices
) {
//object
fun registerUser(email: String, password: String) {
userRepository.saveUser(email, password)
emailService.send(email, "abc@waycool.in", "User registered")
}
}

