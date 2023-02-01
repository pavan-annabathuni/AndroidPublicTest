package com.example.mandiprice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MandiActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mandi)
        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@MandiActivity, LoginMainActivity::class.java)
                startActivity(intent)
                this@MandiActivity.finish()

            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("DeepLink", "deeplink1: $deepLink")
                }
                if (deepLink != null) {
                    if (deepLink.lastPathSegment.equals("/mandi")) {
                        Log.d("DeepLink", "deeplink2: $deepLink")
                        this.findNavController(R.id.nav_host_dashboard).navigate(R.id.mandiFragment)
                    } else {
                        val cropMasterId = deepLink.getQueryParameter("crop_master_id")
                        val mandiMasterId = deepLink.getQueryParameter("mandi_master_id")
                        val cropName = deepLink.getQueryParameter("crop_name")
                        val marketName = deepLink.getQueryParameter("market_name")
                        val fragment = deepLink.getQueryParameter("fragment")

                        Log.d("DeepLink", "deeplink3 $deepLink")

                        if (!cropMasterId.isNullOrEmpty() && !mandiMasterId.isNullOrEmpty()) {
                            val args = Bundle()
                            args.putInt("cropId", cropMasterId.toInt())
                            args.putInt("mandiId", mandiMasterId.toInt())
                            args.putString("cropName", cropName)
                            args.putString("market", marketName)
                            args.putString("fragment", fragment)
                            this.findNavController(R.id.nav_host_dashboard).navigate(R.id.action_mandiFragment_to_mandiGraphFragment, args)
                        }
                    }

                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }


        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_dashboard
        ) as NavHostFragment
        navController = navHostFragment.navController
    }



    override fun onSupportNavigateUp(): Boolean {
        return if(navController.navigateUp())
            true
        else {
            finish()
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        findNavController(R.id.nav_host_dashboard).handleDeepLink(intent)
    }
}
