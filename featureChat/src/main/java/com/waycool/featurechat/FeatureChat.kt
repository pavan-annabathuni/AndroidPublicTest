package com.waycool.featurechat

import android.content.Context
import android.util.Log
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.LocalSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import zendesk.android.Zendesk
import zendesk.android.events.ZendeskEvent
import zendesk.android.events.ZendeskEventListener
import zendesk.messaging.android.DefaultMessagingFactory
import zendesk.messaging.android.push.PushNotifications

object FeatureChat {

    var UNREAD_MESSAGES_COUNT = 0
    val zendeskEventListener: ZendeskEventListener = ZendeskEventListener { zendeskEvent ->
        when (zendeskEvent) {
            is ZendeskEvent.UnreadMessageCountChanged -> {
                // Your custom action...
                UNREAD_MESSAGES_COUNT=zendeskEvent.currentUnreadCount
            }
            is ZendeskEvent.AuthenticationFailed -> {
                // Your custom action...
            }
            else -> {
                // Default branch for forward compatibility with Zendesk SDK and its `ZendeskEvent` expansion
            }
        }
    }

    fun zenDeskInit(context: Context) {


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
            addZendeskEventListener()
        }
    }

    suspend fun addZendeskEventListener() {
        Zendesk.instance.addEventListener(zendeskEventListener)
        PushNotifications.updatePushNotificationToken(getFCMToken())
    }

    fun removeZendeskEventListener(){
        Zendesk.instance.removeEventListener(zendeskEventListener)
    }

    fun zendeskLogout() {
        Zendesk.instance.logoutUser(
            successCallback = { user -> Log.d("zendesktest", "zendeskChat: ${user.toString()} ") },
            failureCallback = { error -> Log.d("zendesktest", "zendeskChat: ${error.message} ") }
        )
    }

    private suspend fun getFCMToken(): String {
        return LocalSource.getFcmToken()
    }


    private suspend fun getJwtToken(): String? {
        return LocalSource.getUserDetailsEntity()?.jwt

    }


}