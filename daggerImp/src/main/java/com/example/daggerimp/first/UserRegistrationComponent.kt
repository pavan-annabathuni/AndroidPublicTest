package com.example.daggerimp.first

import dagger.Component


/*Now Component in a Dagger works by creating a graph of all the dependencies
in the project so that it can find out where it should get those dependencies when they are needed.*/
@Component
interface UserRegistrationComponent {
    //define objects that want from dagger
   /** fun userRegistrationService() :UserRegistrationService
    fun emailService() :EmailService*/
    /*Now here we are defining so many objects and then consumer is using it*/

    //inject consumer
     fun inject(mainsActivity: MainsActivity)

}