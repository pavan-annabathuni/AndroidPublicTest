package com.example.cropinformation

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

class CropInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_info)

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@CropInfo, LoginActivity::class.java)
                startActivity(intent)
                this@CropInfo.finish()

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
                            if (deepLink?.lastPathSegment!! == "cropinfo") {
                                this.findNavController(R.id.nav_host_dashboard).navigate(R.id.cropSelectionFragment)

                            }
                        }
                    }
                    .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
/*
                if(deepLink!=null){
                    if (deepLink.lastPathSegment!! == "cropinfodetail") {
                        val cropId = deepLink.getQueryParameter ("crop_id")
                        val cropName = deepLink.getQueryParameter ("crop_name")
                        val cropLogo=deepLink.getQueryParameter("crop_logo")

                        if (!cropId.isNullOrEmpty()&&!cropName.isNullOrEmpty()&&!cropLogo.isNullOrEmpty()) {
                            val args = Bundle()
                            args.putInt("cropid", cropId.toInt())
                            args.putString("cropname", cropName)
                            args.putString("croplogo", cropLogo)

                            this.findNavController(R.id.nav_host_dashboard).navigate(R.id.action_cropSelectionFragment_to_cropInfoFragment,args)
                        }
                    }
                }
*/




    }
}