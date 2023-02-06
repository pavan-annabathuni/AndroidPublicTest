package com.example.soiltesting

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.waycool.featurelogin.deeplink.DeepLinkNavigator

class SoilTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil_test)

        DeepLinkNavigator.navigateFromDeeplink(this@SoilTestActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink?.lastPathSegment!= null) {
                if (deepLink?.lastPathSegment!! == "soiltesting") {
                    this.findNavController(R.id.fragmentContainerView).navigate(R.id.soilTestingHomeFragment)

                }
                else   if (deepLink?.lastPathSegment!! == "soiltestinghistory") {
                    this.findNavController(R.id.fragmentContainerView).navigate(R.id.allHistoryFragment)

                }
                else   if (deepLink?.lastPathSegment!! == "soiltestinghistorystatus") {
                    val id = deepLink.getQueryParameter ("id")
                    val soilTestNumber = deepLink.getQueryParameter ("soil_test_number")

                    if (!id.isNullOrEmpty()&&!soilTestNumber.isNullOrEmpty()) {
                        val args = Bundle()
                        args.putInt("id", id.toInt())
                        args.putString("soil_test_number", soilTestNumber)
                        this.findNavController(R.id.fragmentContainerView).navigate(
                            R.id.statusTrackerFragment,
                            args)
                    }
                }
            }

        }



    }
}