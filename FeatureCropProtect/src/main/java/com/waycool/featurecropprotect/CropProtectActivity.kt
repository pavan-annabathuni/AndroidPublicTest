package com.waycool.featurecropprotect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class CropProtectActivity : AppCompatActivity() {

    lateinit var navHost: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_protect)
//        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        navHost.let { navFragment ->
//            navFragment.childFragmentManager.primaryNavigationFragment?.onActivityResult(
//                requestCode,
//                resultCode,
//                data
//            )
//        }
//    }
}