package com.waycool.featurecropprotect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropProtectActivity : AppCompatActivity() {
    lateinit var navHost: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_protect)
        navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@CropProtectActivity, LoginMainActivity::class.java)
                startActivity(intent)
                this@CropProtectActivity.finish()

            }
        }

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->

                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("CropProtect", "Deeplink: $deepLink")


                }
                if (deepLink != null) {
                    if (deepLink.lastPathSegment.equals("cropprotect")) {
//                        this.findNavController(R.id.fragmentContainerView)
//                            .navigate(R.id.cropSelectionFragment)
                    } else if (deepLink.lastPathSegment.equals("pestdiseasedetail", ignoreCase = true)) {

                        val diseaseId = intent.data?.getQueryParameter("disease_id")
                        val diseaseName = intent.data?.getQueryParameter("disease_name")
                        Log.d("CropProtect", "Deeplink: ${intent.data}")
                        Log.d("CropProtect", "Deeplink Params: DiseaseID - $diseaseId DiseaseName - $diseaseName")
                        if (!diseaseId.isNullOrEmpty() && !diseaseName.isNullOrEmpty()) {
                            val args = Bundle()

                            args.putInt("diseaseid", diseaseId.toInt())
                            args.putString("diseasename", diseaseName)

                            this.findNavController(R.id.fragmentContainerView).navigate(
                                R.id.action_cropSelectionFragment_to_pestDiseaseDetailsFragment,
                                args
                            )

                        }

                    } else {
                        val cropId = deepLink.getQueryParameter("crop_id")
                        val cropName = deepLink.getQueryParameter("crop_name")
                        if (!cropId.isNullOrEmpty() && !cropName.isNullOrEmpty()) {
                            val args = Bundle()
                            args.putInt("cropid", cropId.toInt())
                            args.putString("cropname", cropName)
                            this.findNavController(R.id.fragmentContainerView).navigate(
                                R.id.action_cropSelectionFragment_to_pestDiseaseFragment,
                                args
                            )
                        }

                    }

                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        navHost?.let {
            it.childFragmentManager.primaryNavigationFragment?.onActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }

}