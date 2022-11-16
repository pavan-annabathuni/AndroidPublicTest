package com.waycool.iwap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.soiltesting.SoilTestActivity
import com.example.soiltesting.ui.request.SoilTestRequestViewModel
import com.example.soiltesting.utils.Constant
import com.waycool.data.Local.LocalSource
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.iwap.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var accountID: Int? =null
    val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationBar()


        tokenCheckViewModel.getUserDetails().observe(this) {
            for ( i in it.data!!.account){
                if (i.accountType=="outgrow"){
                    Log.d(Constant.TAG, "onCreateViewAccountID:${i.id}")
                    accountID=i.id
                    if (accountID!=null){
                        Log.d(Constant.TAG, "onCreateViewAccountID:$accountID")
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.d("TAG", "onCreateToken:$accountID")
                            Log.d("TAG", "onCreateToken:${tokenCheckViewModel.getUserToken()}")
                            val token:String=tokenCheckViewModel.getUserToken()
                            tokenCheckViewModel(accountID!!,token)
                            Log.d("TAG", "onCreateToken: ${tokenCheckViewModel.getUserToken()}")
                        }

                    }

                }
            }
        }




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
                moveToFragment(new LanguageFragment());
            } else {

            }
        }*/

        //    private void moveToFragment(Fragment fragment) {
        //        getSupportFragmentManager().beginTransaction()
        //                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        //
        //    }
    }
    fun tokenCheckViewModel(user_id:Int,token:String){
        tokenCheckViewModel.checkToken(user_id,token).observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.status==true){
//                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    }else if (it.data?.status==false){
                        Toast.makeText(this, "Account Login Anther Device", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "tokenCheckViewModelTokenExpire:")
                        val intent = Intent(this, LoginMainActivity::class.java)
                        startActivity(intent);
                    }else{
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