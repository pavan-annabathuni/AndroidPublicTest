package com.waycool.iwap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.utils.Resource
import com.waycool.featurecropprotect.CropProtectActivity
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.iwap.databinding.ActivityMainBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.newsandarticles.view.NewsFullviewActivity
import com.waycool.weather.WeatherActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var dashboardDomain: DashboardDomain? = null
    private var accountID: Int? = null

    //    val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setupBottomNavigationBar()
        getDashBoard()

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
                if (!deepLink?.lastPathSegment.isNullOrEmpty()) {
                    if (deepLink?.lastPathSegment!! == "weathershare") {
                        val intent = Intent(this, WeatherActivity::class.java)
                        startActivity(intent)
                    } else if (deepLink.lastPathSegment == "newsandarticlesfullscreen") {
                        val title = deepLink.getQueryParameter("title")
                        val desc = deepLink.getQueryParameter("content")
                        val image = deepLink.getQueryParameter("image")
                        val audioUrl = deepLink.getQueryParameter("audio")
                        val newsDate = deepLink.getQueryParameter("date")
                        val source = deepLink.getQueryParameter("source")
                        if (!title.isNullOrEmpty()) {
                            val intent = Intent(this, NewsFullviewActivity::class.java)
                            intent.putExtra("title", title)
                            intent.putExtra("content", desc)
                            intent.putExtra("image", image)
                            intent.putExtra("audio", audioUrl)
                            intent.putExtra("date", newsDate)
                            intent.putExtra("source", source)
                            startActivity(intent)
                        }
                    }

                }
            }

            .addOnFailureListener(this) { e ->
                Log.w("TAG", "getDynamicLink:onFailure", e)
            }


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
            .addOnFailureListener(this) { e ->
                Log.w("TAG", "getDynamicLink:onFailure", e)
            }


        tokenCheckViewModel.getUserDetails().observe(this) {
            accountID = it.data?.accountId
            if (accountID != null) {
                Log.d(Constant.TAG, "onCreateViewAccountID:$accountID")
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("TAG", "onCreateToken:$accountID")
                    Log.d("TAG", "onCreateToken:${tokenCheckViewModel.getUserToken()}")
                    val token: String = tokenCheckViewModel.getUserToken()
                    validateToken(accountID!!, token)
                    Log.d("TAG", "onCreateToken: ${tokenCheckViewModel.getUserToken()}")
                }

            }


        }
    }

    private fun validateToken(user_id: Int, token: String) {
        tokenCheckViewModel.checkToken(user_id, token).observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.status == true) {
//                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    } else if (it.data?.status == false) {
                        ToastStateHandling.toastError(this, "Account Logged in Another Device", Toast.LENGTH_SHORT)
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

    private fun getDashBoard() {

        tokenCheckViewModel.getDasBoard().observe(this) {
            if (dashboardDomain == null) {
                dashboardDomain = it.data
                when (it) {
                    is Resource.Success -> {
                        Log.d("dashboard", "${it.data?.subscription?.iot}")

                        if (it.data?.subscription?.iot == true) {
                            setupBottomNavigationAndNavigationGraph(isPremium = true)
                            Log.d("dashboard", "Premium Triggered")

                        } else {
                            Log.d("dashboard", "Free Triggered")
                            setupBottomNavigationAndNavigationGraph(isPremium = false)
                        }
                    }
                    is Resource.Loading -> {


                    }
                    is Resource.Error -> {
                    }
                }
            } else {
                if (dashboardDomain?.subscription?.iot != it.data?.subscription?.iot) {

                    when (it) {
                        is Resource.Success -> {
                            Log.d("dashboard", "${it.data?.subscription?.iot}")

                            if (it.data?.subscription?.iot == true) {
                                setupBottomNavigationAndNavigationGraph(isPremium = true)
                                Log.d("dashboard", "Premium Triggered")

                            } else {
                                Log.d("dashboard", "Free Triggered")
                                setupBottomNavigationAndNavigationGraph(isPremium = false)
                            }
                        }
                        is Resource.Loading -> {


                        }
                        is Resource.Error -> {
                        }
                    }
                }
            }


        }

    }

    private fun setupBottomNavigationAndNavigationGraph(isPremium: Boolean) {

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_mainactivity
        ) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = binding.activityMainBottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.itemIconTintList = null

        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_main)
        navController = navHostFragment.navController

        if (isPremium) {
            navGraph.setStartDestination(R.id.nav_home_premium)
            navController.graph = navGraph
            bottomNavigationView.menu.clear()
            bottomNavigationView.inflateMenu(R.menu.nav_menu_premium)
        } else {
            navGraph.setStartDestination(R.id.nav_home)
            navController.graph = navGraph
            bottomNavigationView.menu.clear()
            bottomNavigationView.inflateMenu(R.menu.nav_menu_free)
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homePagesFragment -> showBottomNav()
                R.id.myFarmFragment -> showBottomNav()
                com.example.profile.R.id.myProfileFragment -> showBottomNav()
                com.example.mandiprice.R.id.mandiFragment -> {
                    if (isPremium)
                        hideBottomNav()
                    else
                        showBottomNav()
                }
                com.waycool.featurecropprotect.R.id.cropSelectionFragment -> {
                    if (isPremium)
                        hideBottomNav()
                    else
                        showBottomNav()
                }
                R.id.homePagePremiumFragment3 -> showBottomNav()
                else -> hideBottomNav()
            }
        }

    }

    private fun showBottomNav() {
        binding.activityMainBottomNavigationView.clearAnimation()
        binding.activityMainBottomNavigationView.animate().translationY(0f).duration = 300
        binding.activityMainBottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        binding.activityMainBottomNavigationView.clearAnimation()
        binding.activityMainBottomNavigationView.animate().translationY(
            binding.activityMainBottomNavigationView.height.toFloat()
        ).duration = 300
        binding.activityMainBottomNavigationView.visibility = View.GONE

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    fun <T> LiveData<T>.observeOnce(
        owner: LifecycleOwner,
        reactToChange: (T) -> Unit
    ): Observer<T> {
        val wrappedObserver = object : Observer<T> {
            override fun onChanged(data: T) {
                reactToChange(data)
                removeObserver(this)
            }
        }

        observe(owner, wrappedObserver)
        return wrappedObserver
    }

}