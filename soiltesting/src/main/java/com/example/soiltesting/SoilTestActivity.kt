package com.example.soiltesting

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

class SoilTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil_test)

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@SoilTestActivity, LoginActivity::class.java)
                startActivity(intent)
                this@SoilTestActivity.finish()

            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
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
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

    }
}