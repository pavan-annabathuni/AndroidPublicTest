package com.waycool.featurechat

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

object ZendeskChat:Application() {

     fun zenDesk(context: Context) {

//        Chat.INSTANCE.init(context,AppSecrets.getAccountKey(),
//            AppSecrets.getChatAppId())
//        val chatConfiguration = ChatConfiguration.builder()
//            .withAgentAvailabilityEnabled(false)
//            .withTranscriptEnabled(false)
//            .build()
//
//
//
//        val chatProvidersConfiguration: ChatProvidersConfiguration = ChatProvidersConfiguration.builder()
//            .withDepartment("English Language Group")
//            .build()
//     GlobalScope.launch {
//         val jwt = getJwtToken()
//         Log.d("JWT", "zenDesk: $jwt")
//         Chat.INSTANCE.setChatProvidersConfiguration(chatProvidersConfiguration)
//
//
//         val jwtAuthenticator = JwtAuthenticator {
//             it.onTokenLoaded(jwt)
//             it.onError()
//         }
//
//
//         Chat.INSTANCE.setIdentity(jwtAuthenticator)
//
//         MessagingActivity.builder()
//             .withEngines(ChatEngine.engine())
//             .show(context, chatConfiguration);
//     }
//    }
//
//    suspend fun getJwtToken():String?{
//        return LocalSource.getUserDetailsEntity()?.encryptedToken
//
//    }

//         Zendesk.initialize(
//
//             context = this,
//             channelKey = "eyJzZXR0aW5nc191cmwiOiJodHRwczovL3dheWNvb2xpbmRpYS56ZW5kZXNrLmNvbS9tb2JpbGVfc2RrX2FwaS9zZXR0aW5ncy8wMUdIS0E5Njc2MkFKOUVENzRWWUVRSlA5WC5qc29uIn0=",
//             successCallback = { zendesk ->
//                 Log.i("IntegrationApplication", "Initialization successful")
//                 Zendesk.instance.loginUser(jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6ImFwcF82MzZlNDA3OGJjNjZlMjAxMDNiNTg4MzAifQ.eyJpZCI6NSwibmFtZSI6IlJhaHVsIER1ZGhhcmVqaXlhIiwiZW1haWwiOiJyYWh1bC5jaGFuZHJhQHdheWNvb2wuaW4iLCJwaG9uZSI6IjgwMDA4MDU1NjYiLCJleHRlcm5hbF9pZCI6Ik9HNTgwMDA4MDU1NjYiLCJ0YWdzIjoiRW5nbGlzaCIsInJlbW90ZV9waG90b191cmwiOiIiLCJpYXQiOjE2Njg3NzM2NTcsImp0aSI6MTc0OTgzNjAwNjMzNTUxNSwic2NvcGUiOiJ1c2VyIn0.gBc1qMbCMR7B3g-BN5dbfBXtX0QmHYk7V6Bq9Ak0g78",
//                     successCallback = { user -> Log.d("zendesktest", "zendeskChat: ${user.externalId} ") },
//                     failureCallback = { error -> Log.d("zendesktest", "zendeskChat: ${error.message} ")}
//                 )
//                 Zendesk.instance.messaging.showMessaging(this)
//             },
//
//             failureCallback = { error ->
//                 // Tracking the cause of exceptions in your crash reporting dashboard will help to triage any unexpected failures in production
//                 Log.e("IntegrationApplication", "Initialization failed", error)
//             },
//
//             messagingFactory = DefaultMessagingFactory()
//
//         )

     }

}