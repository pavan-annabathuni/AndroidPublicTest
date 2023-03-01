package com.waycool.featurelogin.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.truecaller.android.sdk.TruecallerSDK
import com.waycool.featurelogin.R
import com.waycool.featurelogin.databinding.ActivityLoginBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    /*variables declaration*/
    var binding: ActivityLoginBinding? = null
    private lateinit var navHost: NavHostFragment
    private val deviceName: String by lazy { Build.MODEL }
    private val deviceManufacturer: String by lazy { Build.MANUFACTURER }
    val viewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*view binding -You can now use the instance of the binding class to reference any of the views*/
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        /*initialization of nav host*/
        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        lifecycleScope.launch(Dispatchers.Main) {
            /*First time user open Language screen*/
            if (viewModel.getIsFirst()) {
                navHost.findNavController().setGraph(R.navigation.login_onboarding_nav)
            }
            /*Open the login nav graph*/
            else {
                navHost.findNavController().setGraph(R.navigation.nav_graph_login)
            }
        }

        /*(FCM) generates unique tokens for each device and later using those for sending web push notifications to respective devices.*/
        getFCMToken()

        /*sending mobile details such as name and manufacturer to backend*/
        viewModel.saveDeviceDetails(deviceManufacturer, deviceName)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener addOnCompleteListener@{ task: Task<String?> ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                if (token != null) {
                    //pushing fcm token to backend
                    viewModel.addFcmToken(token)
                }
            })
    }

    /*To get True caller callbacks in Fragment*/
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
            TruecallerSDK.getInstance()
                .onActivityResultObtained(this, requestCode, resultCode, data)
        }
    }


}