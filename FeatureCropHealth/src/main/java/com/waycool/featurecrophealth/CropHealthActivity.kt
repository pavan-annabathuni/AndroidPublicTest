package com.waycool.featurecrophealth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropHealthActivity : AppCompatActivity() {
    lateinit var navHost: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_health)
        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@CropHealthActivity, LoginActivity::class.java)
                startActivity(intent)
                this@CropHealthActivity.finish()

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
                    if (deepLink?.lastPathSegment!! == "crophealth") {
                        this.findNavController(com.example.soiltesting.R.id.fragmentContainerView).navigate(R.id.cropHealthFragment)

                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

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