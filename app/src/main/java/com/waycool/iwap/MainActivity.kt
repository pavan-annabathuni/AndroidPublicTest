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
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Local.LocalSource
import com.waycool.data.Sync.SyncManager
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurelogin.activity.LoginActivity
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.CALL
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.NEWS_ARTICLE
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.RATING
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.navigateFromDeeplink
import com.waycool.iwap.databinding.ActivityMainBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.newsandarticles.view.NewsFullviewActivity
import com.waycool.uicomponents.utils.Constants.PLAY_STORE_LINK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var dashboardDomain: DashboardDomain? = null
    private var accountID: Int? = null

    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDashBoard()

        tokenCheckViewModel.getUserDetails().observe(this) {
            accountID = it.data?.accountId
            if (accountID != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val token: String = tokenCheckViewModel.getUserToken()
                    validateToken(accountID.toString().toInt(), token)
                }
            }
        }
    }

    private fun getDeepLink() {
        navigateFromDeeplink(this@MainActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (!deepLink?.lastPathSegment.isNullOrEmpty()) {
                if (deepLink?.lastPathSegment!!.equals(NEWS_ARTICLE)) {
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
                else if (deepLink?.lastPathSegment.equals(DeepLinkNavigator.NEWS_LIST)){
                    val intent = Intent(this, NewsAndArticlesActivity::class.java)
                    startActivity(intent)

                }
                else if (deepLink?.lastPathSegment == RATING) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(PLAY_STORE_LINK)
                    )
                    startActivity(intent)
                } else if (deepLink?.lastPathSegment!!.contains(CALL)) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(Contants.CALL_NUMBER)
                    startActivity(intent)
                } else if(dashboardDomain?.subscription?.iot == true){
                    if (deepLink?.lastPathSegment!! =="irrigation") {
                        val plotId = deepLink.getQueryParameter("plot_id")
                        if(!plotId.isNullOrEmpty()){
                            val args = Bundle()
                            args.putInt("plotId", plotId.toInt())
//                            val navHostFragment = supportFragmentManager.findFragmentById(
//                                R.id.nav_host_mainactivity
//                            ) as NavHostFragment
//                            navController = navHostFragment.navController


                           navController?.navigate(
                            R.id.action_homePagePremiumFragment3_to_navigation_irrigation,
                                args
                            )
                        }
                    }
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
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                is Resource.Loading -> {


                }
                is Resource.Error -> {

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
                        getDeepLink()
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
                        hideBottomNav()
                }
                com.waycool.featurecropprotect.R.id.cropSelectionFragment -> {
                    if (isPremium)
                        hideBottomNav()
                    else
                        hideBottomNav()
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
        return if(navController.navigateUp())
            true
        else {
            finish()
            true
        }
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

