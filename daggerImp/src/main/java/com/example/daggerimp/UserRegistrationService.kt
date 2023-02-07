package com.example.daggerimp


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
class UserRegistrationService(private val userRepository: UserRepository  ,
                              private val emailService: EmailService
) {

    fun registerUser(email: String, password: String) {
        userRepository.saveUser(email, password)
        emailService.send(email, "abc@waycool.in", "User registered")
    }
}