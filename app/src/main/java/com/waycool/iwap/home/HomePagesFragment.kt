package com.waycool.iwap.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.addcrop.AddCropActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.example.mandiprice.viewModel.MandiViewModel

import com.example.soiltesting.SoilTestActivity
import com.example.soiltesting.ui.checksoil.AdsAdapter
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecrophealth.CropHealthActivity
import com.waycool.featurecropprotect.CropProtectActivity
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.TokenViewModel
import com.waycool.iwap.databinding.FragmentHomePagesBinding
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.adapter.onItemClick
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import com.waycool.weather.WeatherActivity
import kotlinx.coroutines.Dispatchers
import com.waycool.weather.utils.WeatherIcons
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomePagesFragment : Fragment(), OnMapReadyCallback, onItemClick {

    private var dashboardDomain: DashboardDomain? = null
    private var selectedFarm: MyFarmsDomain? = null
    private lateinit var videosBinding: GenericLayoutVideosListBinding
    private lateinit var newsBinding: GenericLayoutNewsListBinding

    private var district: String? = null
    private var polygon: Polygon? = null
    private var mMap: GoogleMap? = null
    private var _binding: FragmentHomePagesBinding? = null
    private val binding get() = _binding!!
    private var orderBy: String = "distance"
    private var cropCategory: String? = null
    private var state: String? = null
    private var crop: String? = null
    private var search: String? = null
    private var sortBy: String = "asc"
    private var accountID: Int? = null
    private var farmjson: String? = null
    private var lat: String = ""
    private var long: String = ""
    private var moduleId = "49"
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val mandiViewModel by lazy { ViewModelProvider(requireActivity())[MandiViewModel::class.java] }
    private val farmsAdapter by lazy { FarmsAdapter(requireContext()) }
    private val farmsCropsAdapter by lazy { FarmCropsAdapter() }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }


    //    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }
//    private val mandiAdapter = MandiHomePageAdapter()
    private lateinit var mandiAdapter: MandiHomePageAdapter
    private val yellow = "#070D09"
    private val lightYellow = "#FFFAF0"
    private val red = "#FF2C23"
    private val lightRed = "#FFD7D0"
    val green = "#146133"
    private val lightGreen = "#DEE9E2"
    private lateinit var myCropAdapter: MyCropsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePagesBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            val value: String? = DataStoreManager.read("FirstTime")
            if (value != "true")
                findNavController().navigate(R.id.action_homePagesFragment_to_spotLightFragment)
        }
        newsBinding = binding.layoutNews

        videosBinding = binding.layoutVideos

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.layoutManager =
            GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mandiAdapter = MandiHomePageAdapter(MandiHomePageAdapter.DiffCallback.OnClickListener {
            val args = Bundle()
            it?.crop_master_id?.let { it1 -> args.putInt("cropId", it1) }
            it?.mandi_master_id?.let { it1 -> args.putInt("mandiId", it1) }
            it?.crop?.let { it1 -> args.putString("cropName", it1) }
            it?.market?.let { it1 -> args.putString("market", it1) }
            this.findNavController()
                .navigate(R.id.action_homePagesFragment_to_mandiGraphFragment22, args)
        })
        newsBinding.viewAllNews.setOnClickListener {
            val intent = Intent(context, NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }
        videosBinding.viewAllVideos.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }

        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in (1..11) -> binding.tvGoodMorning.text = "Good Morning!"
            in 12..15 -> binding.tvGoodMorning.text = "Good Afternoon!"
            in 16..20 -> binding.tvGoodMorning.text = "Good Evening!"
            in 21..23 -> binding.tvGoodMorning.text = "Good Night!"
            else -> binding.tvGoodMorning.text = "Namaste"
        }
        networkCall()
        videosBinding.imgRetry.setOnClickListener {
            networkCall()
        }

        networkNewsCall()
        newsBinding.imgRetry.setOnClickListener {
            networkNewsCall()
        }



        binding.recyclerview.adapter = mandiAdapter
        binding.farmsRv.adapter = farmsAdapter
        binding.cropFarmRv.adapter = farmsCropsAdapter

        binding.soilTestingCv.setOnClickListener {
            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent)
        }
        binding.tvAddFromService.setOnClickListener {
            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent)
        }

        binding.cardCropHealth.setOnClickListener {
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent)
        }
        binding.tvAddFromServiceCropHealth.setOnClickListener {
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent)
        }

        binding.clCropProtect.setOnClickListener {
            findNavController().navigate(R.id.action_homePagesFragment_to_nav_crop_protect)
        }

        binding.tvAddFromServiceCropProtect.setOnClickListener {
            val intent = Intent(activity, CropProtectActivity::class.java)
            startActivity(intent)
        }

        binding.clCropInformation.setOnClickListener {
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent)
        }
        binding.clAddFromServiceCropInformation.setOnClickListener {
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent)
        }

        binding.tvAddFrom.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.tvViewAllMandi.setOnClickListener {
            this.findNavController().navigate(R.id.navigation_mandi)
        }
        binding.cvWeather.setOnClickListener {
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }
        binding.tvOurServiceViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homePagesFragment_to_allServicesFragment)
        }
        binding.tvMyCrops.setOnClickListener {
            findNavController().navigate(R.id.action_homePagesFragment_to_editCropFragment)
        }
        binding.cvAddCrop.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.clAddForm.setOnClickListener {
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }

        binding.tvOurAddFormData.setOnClickListener {
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }

        binding.IvNotification.setOnClickListener {
            this.findNavController().navigate(R.id.action_homePagesFragment_to_notificationFragment)
        }

        binding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScroll.value =
                    calculateScrollPercentage2(binding).toFloat()
            }
        })

        mandiViewModel.viewModelScope.launch {

            mandiViewModel.getMandiDetails(
                lat,
                long,
                cropCategory, state,
                crop,
                sortBy,
                orderBy,
                search,
                0
            )
                .observe(viewLifecycleOwner) {
                    mandiAdapter.submitData(lifecycle, it)

                }
        }

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    accountID = it.data?.accountId
                    it.data.also { userDetails ->
                        binding.tvWelcome.text = userDetails?.profile?.village
                        binding.tvWelcomeName.text = "Welcome, ${it.data?.name}"
                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                weather(it1, it2)
                                lat = it.data?.profile?.lat.toString()
                                long = it.data?.profile?.long.toString()
//                                Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                        getFarms()
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            binding.tvAddress.text = it.data?.profile?.district
        }

        if (accountID != null) {
            getFarms()
        }
        setVideos()
        setNews()
        fabButton()
        myCrop()
        setBanners()

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_farms_home) as SupportMapFragment?

        mapFragment!!.requireView().isClickable = false
        mapFragment.getMapAsync { googleMap: GoogleMap ->
            mMap = googleMap
            mMap?.uiSettings?.setAllGesturesEnabled(false)
            mMap?.uiSettings?.isMapToolbarEnabled = false
        }

        getDashBoard()

    }

    private fun getDashBoard() {

        tokenCheckViewModel.getDasBoard().observe(viewLifecycleOwner) {

            dashboardDomain = it.data
            when (it) {
                is Resource.Success -> {
                    Log.d("dashboard", "${it.data?.subscription?.iot}")
                    if (it.data?.subscription?.iot == true) {
                        binding.clAddYourFarm.visibility = View.GONE
                        binding.tvWelcomeName.visibility = View.INVISIBLE
                        binding.tvGoodMorning.visibility = View.INVISIBLE
                        binding.IvNotification.visibility = View.GONE
                        binding.ll.visibility=View.GONE

                    }
                }
                is Resource.Loading -> {


                }
                is Resource.Error -> {
                }
            }
        }
    }


    private fun networkNewsCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            newsBinding.videoCardNoInternet.visibility = View.VISIBLE
            newsBinding.noDataNews.visibility = View.GONE
            newsBinding.newsListRv.visibility = View.GONE
            newsBinding.viewAllNews.visibility = View.GONE
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Check internet",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            newsBinding.videoCardNoInternet.visibility = View.GONE
            newsBinding.newsListRv.visibility = View.VISIBLE
            newsBinding.viewAllNews.visibility = View.VISIBLE
            newsBinding.viewAllNews.isClickable=true
            setNews()
        }
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            videosBinding.videoCardNoInternet.visibility = View.VISIBLE
            videosBinding.videosListRv.visibility = View.GONE
            videosBinding.viewAllVideos.visibility = View.GONE
            videosBinding.videosScroll.visibility = View.GONE
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Check internet",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            videosBinding.videoCardNoInternet.visibility = View.GONE
            videosBinding.videosListRv.visibility = View.VISIBLE
            videosBinding.viewAllVideos.visibility = View.VISIBLE
            videosBinding.videosScroll.visibility = View.VISIBLE
            setVideos()
        }
    }


    private fun getFarms() {
        viewModel.getMyFarms().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Log.d("farm", "step3")
                    if (!it.data.isNullOrEmpty()) {
                        Log.d("farm", "step9 ${it.data}")
                        binding.clAddForm.visibility = View.GONE
                        binding.clMyForm.visibility = View.VISIBLE
                        binding.farmsDetailsCl.visibility = View.VISIBLE
                        farmsAdapter.submitList(it.data)
                        farmsAdapter.onItemClick = { farm ->
                            selectedFarm = farm
                            populateMyFarm()
                        }
                    } else {
                        binding.clAddForm.visibility = View.VISIBLE
                        binding.clMyForm.visibility = View.GONE
                        binding.farmsDetailsCl.visibility = View.GONE


                    }
//                    Toast.makeText(context, "Farm Api called Sucessfully", Toast.LENGTH_SHORT)
//                        .show()
                }
                is Resource.Loading -> {
                    Log.d("farm", "step5")
                }
                is Resource.Error -> {
//                    Toast.makeText(
//                        context,
//                        "Farm Api called Error ${it.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    Log.d("farm", "step6 " + it.message)
                }
                else -> {
                    Log.d("farm", "step7")

                }
            }
        }
    }

    private fun populateMyFarm() {
        binding.farmnameHome.text = selectedFarm?.farmName

        binding.farmsDetailsCl.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", selectedFarm)
            findNavController().navigate(R.id.action_homePagesFragment_to_nav_farmdetails, bundle)
        }
        drawFarmBoundaries(selectedFarm?.farmJson)
//                                            weather((farm?.farmCenter)?.get(0)?.latitude.toString(),(farm?.farmCenter)?.get(0)?.longitude.toString())
        (selectedFarm?.farmCenter)?.get(0)?.latitude?.let { lat ->
            (selectedFarm?.farmCenter)?.get(0)?.longitude?.let { lng ->
                getFarmLocation(
                    lat, lng
                )
            }
        }
        binding.tvCityName.text = district
        viewModel.getMyCrop2().observe(viewLifecycleOwner) { crops ->
            val croplist =
                crops.data?.filter { filter ->
                    filter.farmId == selectedFarm?.id
                }
            farmsCropsAdapter.submitList(croplist)
        }

    }

    private fun getFarmLocation(lat: Double, lng: Double) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val list: List<Address> =
                    geocoder.getFromLocation(lat, lng, 1) as List<Address>
                district = list[0].locality + "," + list[0].adminArea
            } catch (e: InvocationTargetException) {

            }
        }
    }

    private fun drawFarmBoundaries(farmJson: ArrayList<LatLng>?) {
        mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        if (farmJson != null) {
            val points = farmJson
            if (points != null) {
                if (polygon != null)
                    polygon!!.remove()
                polygon = null
                if (points.size >= 3) {
                    polygon = mMap?.addPolygon(
                        PolygonOptions().addAll(points).fillColor(Color.argb(100, 58, 146, 17))
                            .strokeColor(
                                Color.argb(255, 255, 255, 255)
                            )
                    )

                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            getLatLnBounds(points), 10
                        )
                    )
                } else {
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(points[0], 16f))
                }
            }
        }
    }

    fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
        val builder = LatLngBounds.builder()
        for (ll in points) {
            builder.include(ll)
        }
        return builder.build()
    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter()
        viewModel.getVansAdsList().observe(viewLifecycleOwner) {

            bannerAdapter.submitData(lifecycle, it)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.snapshot().size}"
            }.attach()
        }
        binding.bannerViewpager.adapter = bannerAdapter
        binding.bannerViewpager.clipToPadding = false
        binding.bannerViewpager.clipChildren = false
        binding.bannerViewpager.offscreenPageLimit = 3
        binding.bannerViewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)
    }


    fun calculateScrollPercentage2(videosBinding: FragmentHomePagesBinding): Int {
        val offset: Int = videosBinding.recyclerview.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.recyclerview.computeHorizontalScrollExtent()
        val range: Int = videosBinding.recyclerview.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }


    private fun setNews() {
        val  adapter=NewsGenericAdapter(context,this)
        newsBinding.newsListRv.adapter = adapter
        lifecycleScope.launch((Dispatchers.Main)) {
            viewModel.getVansNewsList(moduleId).collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    newsBinding.videoCardNoInternet.visibility = View.VISIBLE
                    newsBinding.noDataNews.visibility = View.GONE
                    newsBinding.newsListRv.visibility = View.INVISIBLE
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->

                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    newsBinding.noDataNews.visibility = View.VISIBLE
                                    newsBinding.videoCardNoInternet.visibility = View.GONE
                                    newsBinding.newsListRv.visibility = View.INVISIBLE
                                    newsBinding.viewAllNews.visibility=View.GONE
                                }

                                if (it1 is LoadState.NotLoading) {
                                    if (adapter.itemCount == 0) {
                                        newsBinding.noDataNews.visibility = View.VISIBLE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.INVISIBLE
                                        newsBinding.viewAllNews.visibility=View.GONE

                                    } else {
                                        newsBinding.noDataNews.visibility = View.GONE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.VISIBLE
                                        newsBinding.viewAllNews.visibility=View.VISIBLE


                                    }
                                }
                            }
                    }
                }
            }



        }
    }
    private fun setVideos() {
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getVansVideosList(moduleId).collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    videosBinding.videoCardNoInternet.visibility = View.VISIBLE
                    videosBinding.noDataVideo.visibility = View.GONE
                    videosBinding.videosListRv.visibility = View.INVISIBLE
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->

                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    videosBinding.noDataVideo.visibility = View.VISIBLE
                                    videosBinding.videoCardNoInternet.visibility = View.GONE
                                    videosBinding.videosListRv.visibility = View.INVISIBLE
                                    videosBinding.viewAllVideos.visibility=View.GONE
                                }

                                if (it1 is LoadState.NotLoading) {
                                    Log.d("HomePage", "Adapter Size: ${adapter.itemCount}")

                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                        videosBinding.viewAllVideos.visibility=View.GONE
                                    } else {
                                        videosBinding.noDataVideo.visibility = View.GONE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.VISIBLE
                                        videosBinding.viewAllVideos.visibility=View.VISIBLE


                                    }
                                }
                            }
                    }


                }



            }
        }

        videosBinding.viewAllVideos.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_homePagesFragment_to_playVideoFragment4,
                bundle
            )
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

    @SuppressLint("SuspiciousIndentation")
    private fun weather(lat: String, lon: String) {
        viewModel.getWeather(lat, lon).observe(viewLifecycleOwner) {
            if (it?.data != null) {
                binding.tvDegree.text = String.format("%.0f", it.data?.current?.temp) + "\u2103"
                binding.tvWindDegree.text =
                    String.format("%.0f", it.data?.current?.windSpeed) + "Km/h"
                if (it.data?.daily?.isNotEmpty() == true)
                    binding.tvRainDegree.text =
                        String.format("%.0f", it.data?.daily?.get(0)?.pop?.times(100)) + "%"
                Log.d("Weather", "weather: $it")
                if (it.data?.current?.weather?.isNotEmpty() == true)
//                    Glide.with(requireContext())
//                        .load("https://openweathermap.org/img/wn/${it.data!!.current!!.weather[0].icon}@4x.png")
//                        .into(binding.ivWeather)
                binding.tvHumidityDegree.text =
                    String.format("%.0f", it.data?.current?.humidity) + "%"
                // binding.weatherMaster = it.data


                    if (!it.data?.current?.weather.isNullOrEmpty()) {
                        it.data!!.current?.weather?.get(0)?.icon?.let { it1 ->
                            WeatherIcons.setWeatherIcon(
                                it1, binding.ivWeather
                            )

                            val date: Long? = it.data?.current?.dt?.times(1000L)
                            val dateTime = Date()
                            if (date != null) {
                                dateTime.time = date
                            }
                            val formatter =
                                SimpleDateFormat(
                                    "EE d,MMM",
                                    Locale.ENGLISH
                                )//or use getDateInstance()
                            val formatedDate = formatter.format(dateTime)
                            binding.tvDay.text = "Today $formatedDate"
                        }
                    }

            }
            if (it.data?.current?.weather?.isEmpty() == false)
                when (it.data?.current?.weather?.get(0)?.id) {
                    200 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    201 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    202 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    210 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    211 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    212 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    221 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    230 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    231 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    232 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    300 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    301 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    302 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    310 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    311 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    312 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    313 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    314 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    321 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    500 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    501 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    502 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    503 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    504 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    511 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    520 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    521 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    522 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    531 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(red))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightRed))
                    }
                    701 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    711 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    721 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    731 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    741 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    751 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    761 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    800 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(green))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightGreen))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation, 0, 0, 0
                        )
                    }
                    801 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(green))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightGreen))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation, 0, 0, 0
                        )
                    }
                    802 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(green))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightGreen))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation, 0, 0, 0
                        )
                    }
                    803 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }
                    804 -> {
                        binding.tvCloudy.text = it?.data?.current!!.weather[0].description
                        binding.tvCloudy.setTextColor(Color.parseColor(yellow))
                        binding.clCloudy.setBackgroundColor(Color.parseColor(lightYellow))
                        binding.tvCloudy.setCompoundDrawablesWithIntrinsicBounds(
                            com.waycool.weather.R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                        )
                    }

                }
        }

    }

    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
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

    private fun myCrop() {
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
//            val intent = Intent(activity, IrrigationPlannerActivity::class.java)
//            startActivity(intent)
        })
        binding.rvMyCrops.adapter = myCropAdapter
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            Log.d("MyCrops", "myCrop: ${it.data}")
            myCropAdapter.submitList(it.data)
            if ((it.data != null)) {
                binding.tvCount.text = it.data!!.size.toString()
            } else {
                binding.tvCount.text = "0"
            }
            if (it.data!!.isNotEmpty()) {
                binding.cvEditCrop.visibility = View.VISIBLE
                binding.cardAddForm.visibility = View.GONE
            } else {
                binding.cvEditCrop.visibility = View.GONE
                binding.cardAddForm.visibility = View.VISIBLE
            }
            if (it.data?.size!! < 8) {
                binding.addLl.visibility = View.VISIBLE
            } else binding.addLl.visibility = View.GONE
        }
    }


    private fun convertStringToLatLnList(s: String?): List<LatLng?>? {
        val listType = object : TypeToken<List<LatLng?>?>() {}.type
        return Gson().fromJson(s, listType)
    }

    override fun onMapReady(mMap: GoogleMap?) {
        mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        if (farmjson != null) {
            val points = convertStringToLatLnList(farmjson)
            if (points != null) {
                if (points.size >= 3) {
                    mMap?.addPolygon(
                        PolygonOptions().addAll(points).fillColor(Color.argb(100, 58, 146, 17))
                            .strokeColor(
                                Color.argb(255, 255, 255, 255)
                            )
                    )
                }
                for (latLng in points) {
                    val marker = mMap!!.addMarker(
                        MarkerOptions().position(
                            latLng!!
                        )
                            .icon(BitmapDescriptorFactory.fromResource(com.waycool.addfarm.R.drawable.circle_green))
                            .anchor(0.5f, .5f)
                            .draggable(false)
                            .flat(true)
                    )
                }
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points), 50
                    )
                )
            }
        }
    }

    override fun onItemClickListener(it: VansFeederListDomain?) {
        val bundle = Bundle()
        bundle.putString("title", it?.title)
        bundle.putString("content", it?.desc)
        bundle.putString("image", it?.thumbnailUrl)
        bundle.putString("audio", it?.audioUrl)
        bundle.putString("date", it?.startDate)
        bundle.putString("source", it?.sourceName)
        findNavController().navigate(
            R.id.action_homePagesFragment_to_newsFullviewActivity2,
            bundle
        )
    }


}

