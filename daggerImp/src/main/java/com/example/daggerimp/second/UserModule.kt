package com.example.daggerimp.second

import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class UserModule {

    @Provides
    fun getFirebaseRepo():UserService{
        return FirebaseRepo()
    }

    @Binds
    abstract fun getSQLRepo(sqlRepo: SQLRepo):UserService
}