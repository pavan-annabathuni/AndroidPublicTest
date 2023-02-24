package com.example.profile

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.PROFILE
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.navigateFromDeeplink

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        navigateFromDeeplink(this@ProfileActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink?.lastPathSegment != null) {
                if (deepLink?.lastPathSegment!! == PROFILE) {
                    this.findNavController(R.id.nav_host_dashboard_profile)
                        .navigate(R.id.myProfileFragment)

                }
            }
        }


        }


}