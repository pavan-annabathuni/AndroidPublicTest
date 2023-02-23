package com.waycool.featurechat

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.CHAT
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.navigateFromDeeplink

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        navigateFromDeeplink(this@ChatActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink != null) {
                if (deepLink.lastPathSegment.equals(CHAT)) {
                    FeatureChat.zenDeskInit(this)
                }
            }
        }

    }
    override fun onResume() {
            super.onResume()
            EventScreenTimeHandling.calculateScreenTime("ChatActivity")

    }
}