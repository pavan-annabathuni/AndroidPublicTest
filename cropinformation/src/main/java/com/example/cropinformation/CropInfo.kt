package com.example.cropinformation

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator

class CropInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_info)

        DeepLinkNavigator.navigateFromDeeplink(this@CropInfo) {pendingDynamicLinkData->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink?.lastPathSegment!= null) {
                if (deepLink?.lastPathSegment == "cropinfo") {
                    this.findNavController(R.id.nav_host_dashboard).navigate(R.id.cropSelectionFragment)

                }
            }
        }
    }
}