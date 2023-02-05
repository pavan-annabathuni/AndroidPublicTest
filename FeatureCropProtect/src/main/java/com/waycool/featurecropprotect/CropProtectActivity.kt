package com.waycool.featurecropprotect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropProtectActivity : AppCompatActivity() {
    lateinit var navHost: Fragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_protect)
        navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@CropProtectActivity, LoginActivity::class.java)
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
                }
                if (deepLink != null) {
                    if (deepLink.lastPathSegment!!.contains(
                            "pestdiseasedetail",
                            ignoreCase = true
                        )
                    ) {
                        val diseaseId = deepLink.getQueryParameter("disease_id")
                        val diseaseName = deepLink.getQueryParameter("disease_name")
                        if (!diseaseId.isNullOrEmpty() && !diseaseName.isNullOrEmpty()) {
                            val args = Bundle()

                            args.putInt("diseaseid", diseaseId.toInt())
                            args.putString("diseasename", diseaseName)

                            this.findNavController(R.id.fragmentContainerView).navigate(
                                R.id.action_cropSelectionFragment_to_pestDiseaseDetailsFragment,
                                args
                            )
                        }
                    } else if (deepLink.lastPathSegment!!.contains(
                            "pestdisease",
                            ignoreCase = true
                        )
                    ) {
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
                    } else {
                        Log.d("CropProtectNavigation", "CropProtectNavigation1")
                        findNavController(R.id.fragmentContainerView).navigate(R.id.cropSelectionFragment)

                    }

                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController
    }


    override fun onSupportNavigateUp(): Boolean {
        return if (navController.navigateUp())
            true
        else {
            finish()
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        findNavController(R.id.fragmentContainerView).handleDeepLink(intent)
    }


}