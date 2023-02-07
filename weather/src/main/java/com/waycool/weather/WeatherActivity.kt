package com.waycool.weather

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)

        DeepLinkNavigator.navigateFromDeeplink(this@WeatherActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }

            if (!deepLink?.lastPathSegment.isNullOrEmpty()) {
                Log.d("DeepLink","WeatherDeeplink2 $deepLink")
                if (deepLink?.lastPathSegment.equals("/weathershare")) {
                    this.findNavController(R.id.nav_host_fragment).navigate(R.id.weatherFragment)
                }
            }
        }

    }
}