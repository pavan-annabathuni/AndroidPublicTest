package com.example.soiltesting.ui.checksoil

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentSoilTestingHomeBinding
import com.example.soiltesting.ui.history.HistoryDataAdapter
import com.example.soiltesting.ui.history.HistoryViewModel
import com.example.soiltesting.ui.history.StatusTrackerListener
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.AdsAdapter
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt


class SoilTestingHomeFragment : Fragment(), StatusTrackerListener {

    //    private val binding get() = _binding!!
    private lateinit var binding: FragmentSoilTestingHomeBinding
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private lateinit var videosBinding: GenericLayoutVideosListBinding
    private var soilHistoryAdapter = HistoryDataAdapter(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var accountID: Int? = null
    private var moduleId = "22"
    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }


    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .build()

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            getLocation()
            locationResult.lastLocation?.let {
                removeLocationCallback()
                checkForONP(it)
            }
        }
    }

    private fun removeLocationCallback() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        if (allAreGranted) {
            getLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSoilTestingHomeBinding.inflate(layoutInflater)
        soilHistoryAdapter = HistoryDataAdapter(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        videosBinding = binding.layoutVideos
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        apiErrorHandlingBinding = binding.errorState
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        binding.recyclerview.adapter = soilHistoryAdapter
        binding.tvCheckCrop.isSelected = true
        initViewClick()
        initViewBackClick()
        expandableView()
        expandableViewTWo()
        binding.clProgressBar.visibility = View.VISIBLE
        expandableViewThree()
        getVideos()
        fabButton()
        getAllHistory()
        setBanners()
        locationClick()
        translationSoilTesting()
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.cardCheckHealth.visibility = View.GONE
            binding.addFab.visibility = View.GONE

            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check internet connectivity",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.cardCheckHealth.visibility = View.VISIBLE
            binding.addFab.visibility = View.VISIBLE
            setBanners()
            getAllHistory()
            getVideos()


        }
    }

    private fun setBanners() {
        binding.progressBar.visibility = View.VISIBLE

        val bannerAdapter = AdsAdapter(requireContext())
        viewModel.getVansAdsList().observe(viewLifecycleOwner) {

            bannerAdapter.submitData(lifecycle, it)
            binding.clProgressBar.visibility = View.GONE

            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.snapshot().size}"
            }.attach()
        }
        binding.bannerViewpager.adapter = bannerAdapter
//        TabLayoutMediator(
//            binding.bannerIndicators, binding.bannerViewpager
//        ) { tab: TabLayout.Tab, position: Int ->
//            tab.text = "${position + 1} / ${bannerImageList.size}"
//        }.attach()

        binding.bannerViewpager.clipToPadding = false
        binding.bannerViewpager.clipChildren = false
        binding.bannerViewpager.offscreenPageLimit = 3
        binding.bannerViewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)
    }

    private fun getAllHistory() {
        binding.clProgressBar.visibility = View.VISIBLE

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountID = it.data?.accountId
            if (accountID != null) {
                Log.d(ContentValues.TAG, "onCreateViewAccountID:$$accountID")
                bindObserversSoilTestHistory(accountID!!)

            }
        }
    }


    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
        binding.cardCheckHealth.setOnClickListener {
            viewModel.getUserDetails().observe(viewLifecycleOwner) {
                binding.clProgressBar.visibility = View.VISIBLE

                accountID = it.data?.accountId
                if (accountID != null) {
                    isLocationPermissionGranted(accountID!!)
                    binding.cardCheckHealth.isClickable = false

                }
            }
        }
    }

    private fun initViewClick() {
        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_allHistoryFragment)
        }

    }


    private fun getVideos() {
        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getVansVideosList(moduleId).collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    videosBinding.videoCardNoInternet.visibility = View.VISIBLE
                    videosBinding.noDataVideo.visibility = View.GONE
                    videosBinding.ivViewAll.visibility = View.GONE
                    videosBinding.viewAllVideos.visibility = View.GONE
                    videosBinding.videosListRv.visibility = View.INVISIBLE
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error) {
                                    if(adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.ivViewAll.visibility = View.GONE
                                        videosBinding.tvNoVANs.text="Videos are being loaded.Please wait for some time"
                                        videosBinding.viewAllVideos.visibility = View.GONE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                    }
                                }

                                if (it1 is LoadState.NotLoading) {
                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.ivViewAll.visibility = View.GONE
                                        videosBinding.viewAllVideos.visibility = View.GONE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                    } else {
                                        videosBinding.noDataVideo.visibility = View.GONE
                                        videosBinding.ivViewAll.visibility = View.VISIBLE
                                        videosBinding.viewAllVideos.visibility = View.VISIBLE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.VISIBLE

                                    }
                                }
                            }
                    }


                }

            }
        }




        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_soilTestingHomeFragment_to_playVideoFragment2,
                bundle
            )
        }
        videosBinding.viewAllVideos.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            intent.putExtra("module_id", moduleId)
            startActivity(intent)
        }
        videosBinding.ivViewAll.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            intent.putExtra("module_id", moduleId)
            startActivity(intent)
        }

        videosBinding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        videosBinding.videosListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                videosBinding.videosScroll.value =
                    calculateScrollPercentage(videosBinding).toFloat()
            }
        })
    }

    fun calculateScrollPercentage(videosBinding: GenericLayoutVideosListBinding): Int {
        val offset: Int = videosBinding.videosListRv.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.videosListRv.computeHorizontalScrollExtent()
        val range: Int = videosBinding.videosListRv.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }


    private fun expandableView() {
        binding.clFAQAnsOne.setOnClickListener {
            if (binding.clExpandeble.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.cardData, AutoTransition())
                binding.clExpandeble.visibility = View.VISIBLE
                binding.ivSoil.setBackgroundResource(R.drawable.ic_arrow_up)

            } else {
                TransitionManager.beginDelayedTransition(binding.cardData, AutoTransition())
                binding.clExpandeble.visibility = View.GONE
                binding.ivSoil.setBackgroundResource(R.drawable.ic_down_arrpw)


            }
        }
    }

    private fun expandableViewThree() {
        binding.clFAQAnsThree.setOnClickListener {
            if (binding.clExpandebleThree.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.cardDataThree, AutoTransition())
                binding.clExpandebleThree.visibility = View.VISIBLE
                binding.ivSoilThree.setBackgroundResource(R.drawable.ic_arrow_up)

            } else {
                TransitionManager.beginDelayedTransition(binding.cardDataTwo, AutoTransition())
                binding.clExpandebleThree.visibility = View.GONE
                binding.ivSoilThree.setBackgroundResource(R.drawable.ic_down_arrpw)


            }
        }
    }

    private fun expandableViewTWo() {
        binding.clFAQAnsTwo.setOnClickListener {
            if (binding.clExpandebleTwo.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.cardDataTwo, AutoTransition())
                binding.clExpandebleTwo.visibility = View.VISIBLE
                binding.ivSoilTwo.setBackgroundResource(R.drawable.ic_arrow_up)

            } else {
                TransitionManager.beginDelayedTransition(binding.cardDataTwo, AutoTransition())
                binding.clExpandebleTwo.visibility = View.GONE
                binding.ivSoilTwo.setBackgroundResource(R.drawable.ic_down_arrpw)


            }
        }
    }

    private fun locationClick() {
        binding.cardCheckHealth.setOnClickListener {
            viewModel.getUserDetails().observe(viewLifecycleOwner) {
                accountID = it.data?.accountId
                isLocationPermissionGranted(accountID!!)

            }
        }
    }
    fun translationSoilTesting() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("soil_testing")
            binding.toolText.text = title
        }
        TranslationsManager().loadString(
            "our_soil_testing_service_enables_you_with_a_better_understanding_of_your_soil_health_and_helps_you_to_get_a_better_yield",
            binding.tvOurAll,"Our ‘Soil testing’ service enables you with a better understanding of your soil health and recommends you required nutrition to improve the yield."
        )
        TranslationsManager().loadString("soil_str_raise", binding.tvRaise,"Raise the Request")
        TranslationsManager().loadString("soil_str_soil", binding.SoilSample,"Soil Sample Collection")
        TranslationsManager().loadString("soil_lab_testing", binding.tvSoilLab,"Lab Testing")
        TranslationsManager().loadString("soil_details_report", binding.tvDetaols,"Detailed Report")

        TranslationsManager().loadString("request_history", binding.tvRequest,"Request History")
        TranslationsManager().loadString("faq_s", binding.tvFAQ,"FAQ’s")
        TranslationsManager().loadString("soil_test_q_one", binding.tvSoilText,"1. Why should I do a soil test?")
        TranslationsManager().loadString("soil_test_a_one", binding.clExpandeble,"Regular testing helps develop and maintain more productive soils for farming. Soil tests indicate whether crop nutrients are deficient and, if so, what amounts are needed for optimum growth. It helps to identify problems related to imbalances in nutrients, pH, salts, and organic matter. On the basis of the test report, recommendations will be provided that help increase productivity and profits.")
        TranslationsManager().loadString("soil_test_q_two", binding.tvSoilTextTwo,"2. What is the ideal time for soil sampling?")
        TranslationsManager().loadString("soil_test_a_two", binding.clExpandebleTwo,"Soil Samples are taken anytime throughout the year, after harvesting Kharif and Rabi crops is a good time for most of the crops, or when there is no standing crop in the field.")
        TranslationsManager().loadString("soil_test_q_three", binding.tvSoilTextThree,"3. How often do I soil Test?")
        TranslationsManager().loadString("soil_test_a_three", binding.clExpandebleThree,"Sampling soil once a year is ideal to recommend soil nutrient application. The frequency of soil testing also depends on the crops grown. For annuals such as Corn, Mustard, Lettuce, Wheat, Maize, and Rice the soil should be tested once every year. For perennial plants such as Grapes, Lemons, Bananas, Figs, Asparagus, and Papayas the soil should be tested prior to planting and once every two to three years. However frequent soil testing helps to decide whether the current management is affecting future productivity and farm profitability.")
        TranslationsManager().loadString("str_viewall", binding.tvViewAll,"View all")
        TranslationsManager().loadString("check_soil_health", binding.tvCheckCrop,"Check your Soil health")
        TranslationsManager().loadString("videos", videosBinding.videosTitle,"Videos")
        TranslationsManager().loadString("str_viewall", videosBinding.viewAllVideos,"View all")
    }


    private fun bindObserversSoilTestHistory(account_id: Int) {
        viewModel.getSoilTestHistory(account_id).observe(requireActivity()) {
            if (it.data!!.isEmpty()) {
                binding.clTopGuide.visibility = View.VISIBLE
                binding.clRequest.visibility = View.GONE
            } else
                when (it) {
                    is Resource.Success -> {
                        binding.clProgressBar.visibility = View.GONE

                        binding.clTopGuide.visibility = View.GONE
                        binding.clRequest.visibility = View.VISIBLE

                        Log.d("TAG", "bindObserversData:" + it.data.toString())
                        if (it.data != null) {
                            val response = it.data as ArrayList<SoilTestHistoryDomain>
                            if (response.size <= 2) {
                                soilHistoryAdapter.setMovieList(response)

                            } else {
                                val arrayList = ArrayList<SoilTestHistoryDomain>()
                                arrayList.add(response[0])
                                arrayList.add(response[1])
                                soilHistoryAdapter.setMovieList(arrayList)

                            }

                        }


                    }
                    is Resource.Error -> {
                        context?.let { it1 ->
                            ToastStateHandling.toastError(
                                it1,
                                "Error",
                                Toast.LENGTH_SHORT
                            )
                        }

                    }
                    is Resource.Loading -> {

                    }
                }

        }
    }

    override fun statusTracker(data: SoilTestHistoryDomain) {
        val bundle = Bundle()
        bundle.putInt("id", data.id!!)
        bundle.putString("soil_test_number", data.soil_test_number)
        findNavController().navigate(
            R.id.action_soilTestingHomeFragment_to_statusTrackerFragment,
            bundle
        )
    }

    private fun isLocationPermissionGranted(account_id: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            // use your location object
            false
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && account_id != null) {
                        val latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)

                        viewModel.getCheckSoilTestLab(
                            account_id,
                            latitude,
                            longitutde
                        ).observe(requireActivity()) {
                            binding.clProgressBar.visibility = View.VISIBLE
                            when (it) {
                                is Resource.Success -> {
                                    if (it.data!!.isEmpty()) {
                                        CustomeDialogFragment.newInstance().show(requireActivity().supportFragmentManager, CustomeDialogFragment.TAG)
                                        binding.clProgressBar.visibility = View.GONE
                                        binding.cardCheckHealth.isClickable = true
                                    } else if (it.data!!.isNotEmpty()) {
                                        val response = it.data
                                        val bundle = Bundle().apply {
                                            putParcelableArrayList(
                                                "list",
                                                ArrayList<Parcelable>(response)
                                            )
                                        }
                                        bundle.putString("lat", latitude)
                                        bundle.putString("lon", longitutde)

                                        try {
                                            findNavController().navigate(
                                                R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment,
                                                bundle
                                            )
                                        }catch (e:Exception){
                                        }

                                        binding.clProgressBar.visibility = View.GONE
                                    }

                                }
                                is Resource.Error -> {
                                    if(NetworkUtil.getConnectivityStatusString(context)==0){
                                        ToastStateHandling.toastError(
                                            requireContext(),
                                            "Please check you internet connectivity",
                                            Toast.LENGTH_SHORT
                                        )
                                    }
                                    else{
                                        ToastStateHandling.toastError(
                                            requireContext(),
                                           "Server Error. Try again later",
                                            Toast.LENGTH_SHORT
                                        )
                                    }

                                    binding.clProgressBar.visibility = View.GONE
                                    binding.cardCheckHealth.isClickable = true

                                }
                                is Resource.Loading -> {
                                    binding.clProgressBar.visibility = View.VISIBLE

                                }
                            }

                        }


                    }
                }
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
                fusedLocationProviderClient?.lastLocation!!
                    .addOnSuccessListener {
                        if (it != null) {
                            checkForONP(it)

                        }
                    }
                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } else {

                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)

                val locationResponseTask: Task<LocationSettingsResponse> =
                    LocationServices.getSettingsClient(requireContext().applicationContext)
                        .checkLocationSettings(builder.build())
                locationResponseTask.addOnCompleteListener {
                    try {
                        val response: LocationSettingsResponse =
                            it.getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            val apiException: ResolvableApiException = e as ResolvableApiException
                            try {
                                apiException.startResolutionForResult(
                                    requireActivity(),
                                    REQUEST_CODE_GPS
                                )
                            } catch (sendIntent: IntentSender.SendIntentException) {
                                sendIntent.printStackTrace()
                            }
                        }
                    }

                }

//                context?.let {
//                    ToastStateHandling.toastError(
//                        it,
//                        "Please turn on location",
//                        Toast.LENGTH_LONG
//                    )
//                }
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun checkForONP(it: Location) {
        val latitude = String.format(Locale.ENGLISH, "%.2f", it.latitude)
        val longitutde = String.format(Locale.ENGLISH, "%.2f", it.longitude)

        viewModel.getCheckSoilTestLab(
            accountID!!,
            latitude,
            longitutde
        ).observe(requireActivity()) {
            binding.clProgressBar.visibility = View.VISIBLE
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.isEmpty()) {

                        CustomeDialogFragment.newInstance().show(
                            requireActivity().supportFragmentManager,
                            CustomeDialogFragment.TAG
                        )
                        binding.cardCheckHealth.isClickable = true
                    } else if (it.data!!.isNotEmpty()) {
                        val response = it.data
                        val bundle = Bundle().apply {
                            putParcelableArrayList(
                                "list",
                                ArrayList<Parcelable>(response)
                            )
                        }

                        bundle.putString("lat", latitude)
                        bundle.putString("lon", longitutde)

                        try {
                            findNavController().navigate(
                                R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment,
                                bundle
                            )
                        } catch (e: Exception) {
                            binding.clProgressBar.visibility = View.GONE
                            Log.d(
                                "TAGPraveen",
                                "isLocationPermissionGranted: SetPass"
                            )
                        } catch (e: Exception) {
                            Log.d(
                                "TAGPraveenAade",
                                "isLocationPermissionGranted: NotPassed $e"
                            )
                        }
                        binding.clProgressBar.visibility = View.GONE
                                        binding.view.visibility=View.GONE


                    }

                }
                is Resource.Error -> {
                    if (NetworkUtil.getConnectivityStatusString(context) == 0) {
                        ToastStateHandling.toastError(
                            requireContext(),
                            "Please check you internet connectivity",
                            Toast.LENGTH_SHORT
                        )
                    } else {
                        ToastStateHandling.toastError(
                            requireContext(),
                            "Too many attempts.Try again later",
                            Toast.LENGTH_SHORT
                        )
                    }

                    binding.clProgressBar.visibility = View.GONE
                    binding.cardCheckHealth.isClickable = true


                }
                is Resource.Loading -> {
                    binding.clProgressBar.visibility = View.VISIBLE
                    ToastStateHandling.toastWarning(
                        requireContext(),
                        "Loading",
                        Toast.LENGTH_SHORT
                    )

                }
            }

        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

//        requestPermissions(
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            permissionId
//        )
        //requestPermissions(String[] {android.Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("registerresponse2", "test" + requestCode)
        if (requestCode == 2) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener {
            if (!isVisible) {
                binding.addFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        com.waycool.uicomponents.R.drawable.ic_cross
                    )
                )
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        com.waycool.uicomponents.R.drawable.ic_chat_call
                    )
                )
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener {
            FeatureChat.zenDeskInit(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    companion object {
        private const val REQUEST_CODE_GPS = 1011
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SoilTestingHomeFragment")
    }
}