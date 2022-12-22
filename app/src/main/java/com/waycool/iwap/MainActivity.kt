package com.waycool.iwap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.profile.ProfileActivity
import com.example.soiltesting.utils.Constant
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.data.utils.Resource
import com.waycool.featurecropprotect.CropProtectActivity
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.iwap.databinding.ActivityMainBinding
import com.waycool.weather.WeatherActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var accountID: Int? = null
    val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationBar()


        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@MainActivity, LoginMainActivity::class.java)
                startActivity(intent)
                this@MainActivity.finish()

            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                }
                if (deepLink?.lastPathSegment != null) {
                    if (deepLink?.lastPathSegment!! == "weathershare") {
                        val intent = Intent(this, WeatherActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
            .addOnFailureListener(this) {
                    e -> Log.w("TAG", "getDynamicLink:onFailure", e)
            }


        tokenCheckViewModel.getUserDetails().observe(this) {
            accountID = it.data?.accountId
            if (accountID != null) {
                Log.d(Constant.TAG, "onCreateViewAccountID:$accountID")
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("TAG", "onCreateToken:$accountID")
                    Log.d("TAG", "onCreateToken:${tokenCheckViewModel.getUserToken()}")
                    val token: String = tokenCheckViewModel.getUserToken()
                    tokenCheckViewModel(accountID!!, token)
                    Log.d("TAG", "onCreateToken: ${tokenCheckViewModel.getUserToken()}")
                }

            }


        }

    }

    fun tokenCheckViewModel(user_id: Int, token: String) {
        tokenCheckViewModel.checkToken(user_id, token).observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.status == true) {
//                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    } else if (it.data?.status == false) {
                        Toast.makeText(this, "Account Login Anther Device", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, LoginMainActivity::class.java)
                        startActivity(intent);
                    } else {
                        val intent = Intent(this, LoginMainActivity::class.java)
                        startActivity(intent);
                    }
                }
                is Resource.Loading -> {


                }
                is Resource.Error -> {
//                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
//                        .show()
//                        .show()
                }
            }


        }

    }

    private fun setupBottomNavigationBar() {

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_main
        ) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = binding.activityMainBottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homePagesFragment -> showBottomNav()
                com.example.profile.R.id.myProfileFragment -> showBottomNav()
                com.example.mandiprice.R.id.mandiFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }

    }

    private fun showBottomNav() {
        binding.activityMainBottomNavigationView.clearAnimation();
        binding.activityMainBottomNavigationView.animate().translationY(0f).setDuration(300);
        binding.activityMainBottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        binding.activityMainBottomNavigationView.clearAnimation();
        binding.activityMainBottomNavigationView.animate().translationY(
            binding.activityMainBottomNavigationView.getHeight()
                .toFloat()
        ).setDuration(300);
        binding.activityMainBottomNavigationView.visibility = View.GONE

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginMainActivity::class.java)
        startActivity(intent)
        finish()
    }

}