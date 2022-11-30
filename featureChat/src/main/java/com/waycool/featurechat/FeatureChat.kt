package com.waycool.featurechat

import android.content.Context
import android.util.Log
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.LocalSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import zendesk.android.Zendesk
import zendesk.messaging.android.DefaultMessagingFactory

object FeatureChat {

    fun zenDeskInit(context: Context) {

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

        GlobalScope.launch {
            val jwt = getJwtToken() ?: ""
            Zendesk.initialize(

                context = context,
                channelKey = AppSecrets.getChatChannelKey(),
                successCallback = { zendesk ->
                    Log.i("IntegrationApplication", "Initialization successful")
                    Zendesk.instance.loginUser(jwt = jwt,
                        successCallback = { user ->
                            Log.d(
                                "zendesktest",
                                "zendeskChat: ${user.externalId} "
                            )
                        },
                        failureCallback = { error ->
                            Log.d(
                                "zendesktest",
                                "zendeskChat: ${error.message} "
                            )
                        }
                    )
                    Zendesk.instance.messaging.showMessaging(context)
                },

                failureCallback = { error ->
                    // Tracking the cause of exceptions in your crash reporting dashboard will help to triage any unexpected failures in production
                    Log.e("IntegrationApplication", "Initialization failed", error)
                },

                messagingFactory = DefaultMessagingFactory()

            )

        }
    }

    fun zendeskLogout() {
        Zendesk.instance.logoutUser(
            successCallback = { user -> Log.d("zendesktest", "zendeskChat: ${user.toString()} ") },
            failureCallback = { error -> Log.d("zendesktest", "zendeskChat: ${error.message} ") }
        )
    }

    private suspend fun getJwtToken(): String? {
        return LocalSource.getUserDetailsEntity()?.jwt

    }


}