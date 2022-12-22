package com.waycool.addfarm

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

class AddFarmActivity : AppCompatActivity() {
    lateinit var navHost: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@AddFarmActivity, LoginMainActivity::class.java)
                startActivity(intent)
                this@AddFarmActivity.finish()
            }
            else{
                Firebase.dynamicLinks
                    .getDynamicLink(intent)
                    .addOnSuccessListener {pendingDynamicLinkData: PendingDynamicLinkData? ->
                        var deepLink: Uri? = null
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                        }
                        if (deepLink?.lastPathSegment!= null) {
                            if (deepLink?.lastPathSegment!! == "addfarm") {
                                this@AddFarmActivity.findNavController(R.id.fragmentContainerView).navigate(R.id.drawFarmFragment)

                            }
                        }
                    }
                    .addOnFailureListener {
                            e -> Log.w("TAG", "getDynamicLink:onFailure", e)
                    }
            }
        }

    }

}