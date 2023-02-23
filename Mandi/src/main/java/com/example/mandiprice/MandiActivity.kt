package com.example.mandiprice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.MANDI
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.MANDI_GRAPH
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.navigateFromDeeplink


class MandiActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mandi)

      navigateFromDeeplink(this@MandiActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink != null) {

                if (deepLink.lastPathSegment.equals(MANDI_GRAPH)) {
                    val cropMasterId = deepLink.getQueryParameter("crop_master_id")
                    val mandiMasterId = deepLink.getQueryParameter("mandi_master_id")
                    val subRecordId = deepLink.getQueryParameter("sub_record_id")
                    val cropName = deepLink.getQueryParameter("crop_name")
                    val marketName = deepLink.getQueryParameter("market_name")
                    val fragment = deepLink.getQueryParameter("fragment")



                    if (!cropMasterId.isNullOrEmpty() && !mandiMasterId.isNullOrEmpty()) {
                        val args = Bundle()
                        args.putInt("cropId", cropMasterId.toInt())
                        args.putInt("mandiId", mandiMasterId.toInt())
                        args.putString("subRecordId", subRecordId)
                        args.putString("cropName", cropName)
                        args.putString("market", marketName)
                        args.putString("fragment", fragment)
                        this.findNavController(R.id.nav_host_dashboard).navigate(R.id.action_mandiFragment_to_mandiGraphFragment, args)
                    }
                } else if(deepLink.lastPathSegment.equals(MANDI)){
                    this.findNavController(R.id.nav_host_dashboard).navigate(R.id.mandiFragment)

                }

            }
        }


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
