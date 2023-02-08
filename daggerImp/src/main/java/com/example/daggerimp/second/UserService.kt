package com.example.daggerimp.second

import android.util.Log
import javax.inject.Inject

interface UserService {
    fun saveUser(email:String,password:String){
    }
}

class SQLRepo @Inject constructor():UserService{
    override fun saveUser(email:String, password:String){
        Log.d("msg","saved in DB")

    }
}

class FirebaseRepo:UserService{
    override fun saveUser(email:String, password:String){
        Log.d("msg","saved in firebase")

    }
}