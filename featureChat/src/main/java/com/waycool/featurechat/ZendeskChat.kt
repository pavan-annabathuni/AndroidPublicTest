package com.waycool.featurechat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.LocalSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import zendesk.chat.*
import zendesk.messaging.MessagingActivity

object ZendeskChat {

     fun zenDesk(context: Context) {

        Chat.INSTANCE.init(context,AppSecrets.getAccountKey(),
            AppSecrets.getChatAppId())
        val chatConfiguration = ChatConfiguration.builder()
            .withAgentAvailabilityEnabled(false)
            .withTranscriptEnabled(false)
            .build()



        val chatProvidersConfiguration: ChatProvidersConfiguration = ChatProvidersConfiguration.builder()
            .withDepartment("English Language Group")
            .build()
     GlobalScope.launch {
         val jwt = getJwtToken()
         Log.d("JWT", "zenDesk: $jwt")
         Chat.INSTANCE.setChatProvidersConfiguration(chatProvidersConfiguration)


         val jwtAuthenticator = JwtAuthenticator {
             it.onTokenLoaded(jwt)
             it.onError()
         }


         Chat.INSTANCE.setIdentity(jwtAuthenticator)

         MessagingActivity.builder()
             .withEngines(ChatEngine.engine())
             .show(context, chatConfiguration);
     }
    }

    suspend fun getJwtToken():String?{
        return LocalSource.getUserDetailsEntity()?.encryptedToken

    }

}