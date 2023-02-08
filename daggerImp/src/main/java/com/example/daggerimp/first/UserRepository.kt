package com.example.daggerimp.first

import android.util.Log
import javax.inject.Inject

//DI without Dagger
/*
class UserRepository {
    fun saveUser(email:String,password:String){
        Log.d("msg","save")
    }
}*/

//DI with Dagger
class UserRepository @Inject constructor(){
    fun saveUser(email:String,password:String){
        Log.d("msg","save")
    }
}
