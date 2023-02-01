package com.waycool.featurechat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@ChatActivity, LoginActivity::class.java)
                startActivity(intent)
                this@ChatActivity.finish()

            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if(deepLink!=null){
                    if(deepLink.lastPathSegment.equals("chat")){
                        FeatureChat.zenDeskInit(this)
                    }

                }

            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }


        override fun onResume() {
            super.onResume()
            EventScreenTimeHandling.calculateScreenTime("ChatActivity")

    }
}