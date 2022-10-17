package com.waycool.iwap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.WeatherActivity
import com.waycool.featurecrophealth.CropHealthActivity
import com.waycool.featurecropprotect.CropProtectActivity
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.iwap.databinding.ActivityMainBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.videos.VideoActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        binding.newsButton.setOnClickListener {
            val intent = Intent(this, NewsAndArticlesActivity::class.java)
            startActivity(intent)

        }

        binding.videosButton.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }
//
//
        binding.weatherButton.setOnClickListener {
            val intent =  Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }
        binding.cropprotectButton.setOnClickListener {
            val intent =  Intent(this, CropProtectActivity::class.java)
            startActivity(intent)
        }
//
        binding.crophealthButton.setOnClickListener {
            val intent = Intent(this, CropHealthActivity::class.java)
            startActivity(intent);
        }
//        if (SharedPreferenceUtility.getMobileNumber(applicationContext) != "0") {

        binding.textView5.text= "Version ${BuildConfig.VERSION_NAME}"

        loginViewModel.getUserDetails().observe(this){}

        val mobileno = loginViewModel.getMobileNumber()
        if (mobileno != null)
            binding.logoutButton.setOnClickListener {

                loginViewModel.logout(mobileno)
                    .observe(this@MainActivity) {

                        loginViewModel.setUserToken(null)
                        loginViewModel.setIsLoggedIn(false)

                        Toast.makeText(
                            applicationContext,
                            "Successfully Logout",
                            Toast.LENGTH_LONG
                        ).show()
                        moveToLogin()
                    }
            }


        /*if(SharedPreferenceUtility.getLogin(getApplicationContext()).equals("0")) {
            if (SharedPreferenceUtility.getUserFirst(getApplicationContext()).equals("0")) {
                moveToFragment(new LanguageFragment());
            } else {

            }
        }*/
    }

    //    private void moveToFragment(Fragment fragment) {
    //        getSupportFragmentManager().beginTransaction()
    //                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    //
    //    }


    private fun moveToLogin() {
        val intent = Intent(this, LoginMainActivity::class.java)
        startActivity(intent)
        finish()
    }

}