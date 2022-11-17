package com.waycool.iwap

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.iwap.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationBar()


//        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment)
//        val bottomNavigationView: BottomNavigationView = findViewById(R.id.activity_main_bottom_navigation_view)
//        setupWithNavController(bottomNavigationView, navController)


//        if (SharedPreferenceUtility.getLogin(applicationContext) == "0") {
//            val intent = Intent(this@MainActivity, LoginMainActivity::class.java)
//            startActivityForResult(intent, 1011)
//        }

//        CoroutineScope(Dispatchers.Main).launch {
//            if (!loginViewModel.getIsLoggedIn()) {
//                val intent = Intent(this@MainActivity, LoginMainActivity::class.java)
//                startActivityForResult(intent, 1011)
//            }
//        }

//        binding.textView5.setText(BuildConfig.VERSION_NAME);


//        binding.newsButton.setOnClickListener {
//            val intent = Intent(this, NewsAndArticlesActivity::class.java)
//            startActivity(intent)
//
//        }
//        binding.addCrop.setOnClickListener {
//            val intent = Intent(this, AddCropActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        binding.videosButton.setOnClickListener {
//            val intent = Intent(this, VideoActivity::class.java)
//            startActivity(intent)
//        }
////
////
//        binding.weatherButton.setOnClickListener {
//            val intent = Intent(this, WeatherActivity::class.java)
//            startActivity(intent)
//        }
//        binding.cropprotectButton.setOnClickListener {
//            val intent = Intent(this, CropProtectActivity::class.java)
//            startActivity(intent)
//        }
////
//        binding.crophealthButton.setOnClickListener {
//            val intent = Intent(this, CropHealthActivity::class.java)
//            startActivity(intent);
//        }
//        binding.soilTesting.setOnClickListener {
//            val intent = Intent(this, SoilTestActivity::class.java)
//            startActivity(intent);
//        }
//
//        binding.cropInfoButton.setOnClickListener {
//            val intent = Intent(this, CropInfo::class.java)
//            startActivity(intent);
//        }
//
//        binding.profileButton.setOnClickListener {
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//        }
////        if (SharedPreferenceUtility.getMobileNumber(applicationContext) != "0") {
//
//        binding.textView5.text = "Version ${BuildConfig.VERSION_NAME}"
//
//        loginViewModel.getUserDetails().observe(this) {}
//
//        val mobileno = loginViewModel.getMobileNumber()
//        if (mobileno != null)
//            binding.logoutButton.setOnClickListener {
//
//                loginViewModel.logout(mobileno)
//                    .observe(this@MainActivity) {
//
//                        loginViewModel.setUserToken(null)
//                        loginViewModel.setIsLoggedIn(false)
//
//                        Toast.makeText(
//                            applicationContext,
//                            "Successfully Logout",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        moveToLogin()
//                    }
//            }


        /*if(SharedPreferenceUtility.getLogin(getApplicationContext()).equals("0")) {
            if (SharedPreferenceUtility.getUserFirst(getApplicationContext()).equals("0")) {
                moveToFragment(new ProfileLanguageFragment());
            } else {

            }
        }*/

        //    private void moveToFragment(Fragment fragment) {
        //        getSupportFragmentManager().beginTransaction()
        //                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        //
        //    }
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
//        binding.activityMainBottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        binding.activityMainBottomNavigationView.clearAnimation();
        binding.activityMainBottomNavigationView.animate().translationY(
            binding.activityMainBottomNavigationView.getHeight()
                .toFloat()
        ).setDuration(300);
//        binding.activityMainBottomNavigationView.visibility = View.GONE

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