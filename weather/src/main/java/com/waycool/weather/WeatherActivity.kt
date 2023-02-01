package com.waycool.weather

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@WeatherActivity, LoginActivity::class.java)
                startActivity(intent)
                this@WeatherActivity.finish()

            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("DeepLink","WeatherDeeplink2 ${deepLink}")

                }
                if (!deepLink?.lastPathSegment.isNullOrEmpty()) {
                    Log.d("DeepLink","WeatherDeeplink2 ${deepLink}")
                    if (deepLink?.lastPathSegment == "weathershare") {
                        this.findNavController(R.id.nav_host_fragment).navigate(R.id.weatherFragment)
                    }
                }
            }.addOnFailureListener(this) { e ->
                Log.w("TAG", "getDynamicLink:onFailure", e)
            }
    }
}