package com.example.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class ProfileActivity : AppCompatActivity() {

    lateinit var navHost: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        navHost=supportFragmentManager.findFragmentById(R.id.nav_host_dashboard)!!

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navHost.let {
            it.childFragmentManager.primaryNavigationFragment?.onActivityResult(requestCode,resultCode,data)
        }
    }
}