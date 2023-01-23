package com.waycool.iwap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.soiltesting.utils.Constant
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Local.LocalSource
import com.waycool.data.Sync.SyncManager
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.iwap.databinding.ActivityMainBinding
import com.waycool.newsandarticles.view.NewsFullviewActivity
import com.waycool.weather.WeatherActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
                    deepLink = pendingDynamicLinkData.link }
                if (!deepLink?.lastPathSegment.isNullOrEmpty()) {
                     if (deepLink?.lastPathSegment == "newsandarticlesfullscreen") {
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
                    validateToken(accountID.toString().toInt(), token)
                    Log.d("TAG", "onCreateToken: ${tokenCheckViewModel.getUserToken()}")
                }

            }


        }
    }

    private fun validateToken(user_id: Int, token: String) {
        tokenCheckViewModel.checkToken(user_id, token).observe(this) {
            when (it) {
                is Resource.Success -> {
                   if (it.data?.status != true) {
                        clearData()
                        val intent = Intent(this, LoginMainActivity::class.java)
                        startActivity(intent)
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
                        } else {
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

                            } else {
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
            TranslationsManager().getStringAsLiveData("home")?.observe(this){
                if(bottomNavigationView.menu.findItem(R.id.nav_home_premium)!=null){
                bottomNavigationView.menu.findItem(R.id.nav_home_premium).title = it?.appValue?:"Home"}
            }
            TranslationsManager().getStringAsLiveData("services")?.observe(this){
                if(bottomNavigationView.menu.findItem(R.id.nav_home)!=null){
                    bottomNavigationView.menu.findItem(R.id.nav_home).title = it?.appValue?:"Services"}
            }
            TranslationsManager().getStringAsLiveData("my_farm")?.observe(this){
                if(bottomNavigationView.menu.findItem(R.id.nav_myfarms)!=null){
                    bottomNavigationView.menu.findItem(R.id.nav_myfarms).title = it?.appValue?:"My Farms"}
            }
            TranslationsManager().getStringAsLiveData("profile")?.observe(this){
                if(bottomNavigationView.menu.findItem(R.id.navigation_profile)!=null){
                    bottomNavigationView.menu.findItem(R.id.navigation_profile).title = it?.appValue?:"Profile"}
            }
        } else {
            navGraph.setStartDestination(R.id.nav_home)
            navController.graph = navGraph
            bottomNavigationView.menu.clear()
            bottomNavigationView.inflateMenu(R.menu.nav_menu_free)
            TranslationsManager().getStringAsLiveData("home")?.observe(this){
                if(bottomNavigationView.menu.findItem(R.id.nav_home)!=null){
                    bottomNavigationView.menu.findItem(R.id.nav_home).title = it?.appValue?:"Home"}
            }
            TranslationsManager().getStringAsLiveData("mandi_price")?.observe(this){
                if(bottomNavigationView.menu.findItem(R.id.navigation_mandi)!=null){
                    bottomNavigationView.menu.findItem(R.id.navigation_mandi).title = it?.appValue?:"Market Place"}
            }
            TranslationsManager().getStringAsLiveData("crop_protection")?.observe(this) {
                if (bottomNavigationView.menu.findItem(R.id.nav_crop_protect) != null) {
                    bottomNavigationView.menu.findItem(R.id.nav_crop_protect).title =
                        it?.appValue ?: "Crop Protection"
                }
            }
            TranslationsManager().getStringAsLiveData("profile")?.observe(this) {
                if(bottomNavigationView.menu.findItem(R.id.navigation_profile)!=null) {

                    bottomNavigationView.menu.findItem(R.id.navigation_profile).title =
                        it?.appValue ?: "Profile"
                }
            }
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

    private fun clearData(){
        GlobalScope.launch {
            LocalSource.deleteAllMyCrops()
            LocalSource.deleteTags()
            LocalSource.deleteCropMaster()
            LocalSource.deleteCropInformation()
            LocalSource.deletePestDisease()
            LocalSource.deleteMyFarms()
            SyncManager.invalidateAll()
            DataStoreManager.clearData()
        }
    }
}

