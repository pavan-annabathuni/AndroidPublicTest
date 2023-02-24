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
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.PEST_DISEASE
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.PEST_DISEASE_DETAIL

class CropProtectActivity : AppCompatActivity() {
    lateinit var navHost: Fragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_protect)
        navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
        DeepLinkNavigator.navigateFromDeeplink(this@CropProtectActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
                Log.d("DeepLinkURL","DeepLinkURL Pest $deepLink")

            }
            if (deepLink != null) {
                if (deepLink.lastPathSegment!!.equals(PEST_DISEASE_DETAIL, ignoreCase = true)) {
                    val diseaseId = deepLink.getQueryParameter("disease_id")
                    val diseaseName = deepLink.getQueryParameter("disease_name")
                    Log.d("DeepLinkURL","DeepLinkURL Pest $deepLink    DiseaseDetail $diseaseName $diseaseId")

                    if (!diseaseId.isNullOrEmpty()) {
                        val args = Bundle()

                        args.putInt("diseaseid", diseaseId.toInt())
                        args.putString("diseasename", diseaseName)

                        this.findNavController(R.id.fragmentContainerView).navigate(
                            R.id.action_cropSelectionFragment_to_pestDiseaseDetailsFragment,
                            args
                        )
                    }
                }
                else if (deepLink.lastPathSegment!!.equals(
                        PEST_DISEASE,
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
                    findNavController(R.id.fragmentContainerView).navigate(R.id.cropSelectionFragment)

                }

            }

        }
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