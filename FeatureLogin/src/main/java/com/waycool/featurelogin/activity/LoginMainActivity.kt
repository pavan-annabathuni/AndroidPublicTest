package com.waycool.featurelogin.activity

import android.Manifest
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
import com.waycool.featurelogin.databinding.ActivityLoginMainBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginMainActivity : AppCompatActivity() {
    var binding: ActivityLoginMainBinding? = null
    var permissionId = 1001
    lateinit var navHost: NavHostFragment

    val deviceName: String by lazy { Build.MODEL }
    val deviceManufacturer: String by lazy { Build.MANUFACTURER }

    val viewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

//        navHost.findNavController().setGraph(R.navigation.login_nav)

        lifecycleScope.launch(Dispatchers.Main) {
            if (viewModel.getIsFirst()) {
                navHost.findNavController().setGraph(R.navigation.onboarding_nav)
            } else {
                navHost.findNavController().setGraph(R.navigation.login_nav)
            }
        }

        getFCMToken()
        viewModel.saveDeviceDetails(deviceManufacturer, deviceName)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener addOnCompleteListener@{ task: Task<String?> ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                if (token != null) {
                    viewModel.addFcmToken(token)
                }
            })
    }


    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, permissionId)
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
            TruecallerSDK.getInstance()
                .onActivityResultObtained(this, requestCode, resultCode, data);
        }
    }

//    private fun moveToFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.login_fragment_frame, fragment, fragment.javaClass.simpleName)
//            .addToBackStack(null).commit()
//    }

//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val fragment = supportFragmentManager.findFragmentById(R.id.login_fragment_frame)
//        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
//            fragment!!.onActivityResult(requestCode, resultCode, data)
//        }
//        if (requestCode == 101) {
//            fragment!!.onActivityResult(requestCode, resultCode, data)
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        val fragment = supportFragmentManager.findFragmentById(R.id.login_fragment_frame)
//        fragment!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
//}
}