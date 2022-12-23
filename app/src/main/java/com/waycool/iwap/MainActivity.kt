package com.waycool.iwap

import android.content.Intent
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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.soiltesting.utils.Constant
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.iwap.databinding.ActivityMainBinding
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
                    when (it.data?.status) {
                        true -> {
                            //                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                        }
                        false -> {
                            Toast.makeText(this, "Account Login Anther Device", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("TAG", "tokenCheckViewModelTokenExpire:")
                            val intent = Intent(this, LoginMainActivity::class.java)
                            startActivity(intent)
                        }
                        else -> {
                            val intent = Intent(this, LoginMainActivity::class.java)
                            startActivity(intent)
                        }
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
            if(dashboardDomain==null){
                dashboardDomain=it.data
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
            }else{
                if(dashboardDomain?.subscription?.iot!=it.data?.subscription?.iot){

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
                R.id.allServicesFragment2 -> showBottomNav()
                com.example.profile.R.id.myProfileFragment -> showBottomNav()
                com.example.mandiprice.R.id.mandiFragment -> showBottomNav()
                com.waycool.featurecropprotect.R.id.cropSelectionFragment -> showBottomNav()
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