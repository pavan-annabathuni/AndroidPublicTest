package com.waycool.addfarm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator

class AddFarmActivity : AppCompatActivity() {
    lateinit var navHost: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
        DeepLinkNavigator.navigateFromDeeplink(this@AddFarmActivity) {pendingDynamicLinkData->

            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink?.lastPathSegment != null) {
                if (deepLink?.lastPathSegment!! == "addfarm") {
                    this@AddFarmActivity.findNavController(R.id.fragmentContainerView)
                        .navigate(R.id.drawFarmFragment)

                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navHost?.let {
            it.childFragmentManager.primaryNavigationFragment?.onActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }

}