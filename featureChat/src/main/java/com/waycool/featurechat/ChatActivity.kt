package com.waycool.featurechat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@ChatActivity, LoginMainActivity::class.java)
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
}