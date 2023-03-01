package com.waycool.featurecrophealth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.CROP_HEALTH
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.navigateFromDeeplink

class CropHealthActivity : AppCompatActivity() {
    lateinit var navHost: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_health)

        navigateFromDeeplink(this@CropHealthActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink?.lastPathSegment != null) {
                Log.d("CropHealthDL","CropHealthDL DeepLink $deepLink")

                if (deepLink?.lastPathSegment!! ==CROP_HEALTH) {
                    Log.d("CropHealthDL","CropHealth Frag")

                    this.findNavController(com.example.soiltesting.R.id.fragmentContainerView)
                        .navigate(R.id.cropHealthFragment)
                }

            }
        }

            navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!

        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        navHost.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.onActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }

}