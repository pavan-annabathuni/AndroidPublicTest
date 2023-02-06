package com.example.addcrop

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator

class AddCropActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_crop)

        DeepLinkNavigator.navigateFromDeeplink(this@AddCropActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink?.lastPathSegment != null) {
                if (deepLink?.lastPathSegment!! == "addcrop") {
                    this.findNavController(R.id.fragmentContainerView)
                        .navigate(R.id.selectAddCropFragment)

                }
            }

        }


    }
}