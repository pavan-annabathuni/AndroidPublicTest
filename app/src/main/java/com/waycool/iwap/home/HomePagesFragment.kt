package com.waycool.iwap.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.addcrop.AddCropActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.example.mandiprice.viewModel.MandiViewModel
import com.example.soiltesting.SoilTestActivity
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
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
import com.waycool.iwap.premium.FarmSelectedListener
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.adapter.onItemClick
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.AdsAdapter
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import com.waycool.weather.WeatherActivity
import com.waycool.weather.utils.WeatherIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class HomePagesFragment : Fragment(), OnMapReadyCallback, onItemClick, FarmSelectedListener {

    private var handler: Handler? = null
    private var runnable: Runnable?=null
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
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val mandiViewModel by lazy { ViewModelProvider(requireActivity())[MandiViewModel::class.java] }
    private val farmsCropsAdapter by lazy { FarmCropsAdapter() }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }
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


        newsBinding = binding.layoutNews

        videosBinding = binding.layoutVideos

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) activity?.let { it.finish()}
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.myLooper()!!)

        binding.recyclerview.layoutManager =
            GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mandiAdapter = MandiHomePageAdapter(MandiHomePageAdapter.DiffCallback.OnClickListener {
            val eventBundle=Bundle()
            eventBundle.putString("cropName",mandiAdapter.cropName)
            eventBundle.putString("marketName",mandiAdapter.marketName)
            EventItemClickHandling.calculateItemClickEvent("MandiItemClickHomePageFragment",eventBundle)

            val args = Bundle()
            args.putParcelable("mandiRecord", it)
            this.findNavController()
                .navigate(R.id.action_homePagesFragment_to_mandiGraphFragment22, args)
        })

        setWishes()
        checkNetwork()
        initClick()
        notification()
        binding.tvRain.isSelected = true
        binding.recyclerview.adapter = mandiAdapter
        binding.cropFarmRv.adapter = farmsCropsAdapter

        binding.tvAddFromOne.isSelected = true
        binding.tvViewFarmDetails.isSelected = true


        binding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScroll.value =
                    calculateScrollPercentage2(binding).toFloat()
            }
        })


        userDetailsCall()
        //mandiDetailCall()
        if (accountID != null) {
            getFarms()
        }
        setVideos()
        setNews()
        fabButton()
        myCrop()
        setBanners()
        setTranslation()

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

    private fun setTranslation() {
        TranslationsManager().loadString("welcome", binding.tvName, "Welcome")
        TranslationsManager().loadString("add_crop_info", binding.tvYourForm, "Add your Crop and get more details.")
        TranslationsManager().loadString("add_crop", binding.tvAddFrom, "Add crops")
        TranslationsManager().loadString("add_farm", binding.tvAddFromOne, "Add your farm")
        TranslationsManager().loadString("my_farm", binding.tvMyform, "My Farms")
        TranslationsManager().loadString("add_farm_top", binding.tvOurAddFormData, "Add Farm")
        TranslationsManager().loadString("str_today", binding.tvDays, "Today")

        TranslationsManager().loadString("view_tepm", binding.tvTemp, "Temperature")
        TranslationsManager().loadString("str_humidity", binding.tvHumidity, "Humidity")
        TranslationsManager().loadString("str_wind", binding.tvWind, "Wind")
        TranslationsManager().loadString("str_rain", binding.tvRain, "Rain")
        TranslationsManager().loadString("our_services", binding.tvOurService, "Our Services")
        TranslationsManager().loadString("str_viewall", binding.tvOurServiceViewAll, "View All")

        TranslationsManager().loadString("view_farm_detail", binding.tvViewFarmDetails, "View Farm Details")


        TranslationsManager().loadString("soil_testing", binding.tvSoilTesting, "Soil Testing")
        TranslationsManager().loadString("soil_testing_info", binding.tvSoilTestingDesc, "Helps you assess and recommend the nutrition of your soil")
        TranslationsManager().loadString("txt_know_more", binding.tvSoilTestingKnowMore, "Know more")

        TranslationsManager().loadString("crop_health", binding.tvCropHealth, getString(R.string.pestdiseasedetection))
        TranslationsManager().loadString("crop_health_info", binding.tvCropHealthDesc, "Helps you detect crop’s health using Artificial Intelligence")
        TranslationsManager().loadString("txt_know_more", binding.tvCropHealthKnowMore, "Know more")

        TranslationsManager().loadString("str_title", binding.tvCropInformation, "Crop Information")
        TranslationsManager().loadString("crop_information_info", binding.tvCropInformationDesc, "Gives you end to end  information about your crop.")
        TranslationsManager().loadString("txt_know_more", binding.tvCropInformationKnowMore, "Know more")

        TranslationsManager().loadString("crop_protection", binding.tvCropProtect, "Crop Protection")
        TranslationsManager().loadString(
            "crop_protection_info",
            binding.tvCropProtectDesc,
            "Complete information to fight against all the possible diseases."
        )
        TranslationsManager().loadString("txt_know_more", binding.tvCropProtectKnowMore, "Know more")

        TranslationsManager().loadString("videos", videosBinding.videosTitle, "Videos")
        TranslationsManager().loadString("str_viewall", videosBinding.viewAllVideos, "View all")
        TranslationsManager().loadString("news_articles", newsBinding.newsTitle, getString(R.string.newsarticles))
        TranslationsManager().loadString("str_viewall", newsBinding.viewAllNews, "View All")

        TranslationsManager().loadString("my_crops", binding.myCropsTitle, "My Crops")
        TranslationsManager().loadString("str_edit", binding.tvEditMyCrops, "Edit Crops")

        TranslationsManager().loadString("add_crop", binding.AddCrop, "Add Crop")

        TranslationsManager().loadString("mandi_prices", binding.tvRequest, "Market Prices")
        TranslationsManager().loadString("str_viewall", binding.tvViewAllMandi, "View All")

        TranslationsManager().loadString("videos_not_available", videosBinding.tvNoVANs, "Videos are not available with us.")
        TranslationsManager().loadString("news_not_available", newsBinding.tvNoVANS, "News and Articles are not \navailable with us.")

        TranslationsManager().loadString("videos", videosBinding.videosTitle,"Videos")
        TranslationsManager().loadString("str_viewall", videosBinding.viewAllVideos,"View all")
        TranslationsManager().loadString("news_articles", newsBinding.newsTitle,"Videos")
        TranslationsManager().loadString("str_viewall", newsBinding.viewAllNews,"View all")
    }
    private fun setWishes() {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in (1..11) -> {
                TranslationsManager().loadString("good_morning", binding.tvGoodMorning, "Good Morning")
            }
            in 12..15 -> {
                TranslationsManager().loadString("good_afternoon", binding.tvGoodMorning, "Good Afternoon")
            }
            in 16..20 -> {
                TranslationsManager().loadString("good_evening", binding.tvGoodMorning, "Good Evening")
            }
            in 21..23 -> {
                TranslationsManager().loadString("good_night", binding.tvGoodMorning, "Good Night")
            }
            else -> {
                TranslationsManager().loadString("namaste", binding.tvGoodMorning, "Namaste")
            }
        }
    }

    private fun checkNetwork() {
        networkCall()
        videosBinding.imgRetry.setOnClickListener {
            EventClickHandling.calculateClickEvent("VideosBindingImgRetryNetworkCall")
            networkCall()
        }
        networkNewsCall()
        newsBinding.imgRetry.setOnClickListener {
            EventClickHandling.calculateClickEvent("NewsBindingImgRetryNetworkCall")
            networkNewsCall()
        }
    }

    private fun userDetailsCall() {
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    accountID = it.data?.accountId
                    it.data.also { userDetails ->
                        binding.tvWelcome.text = userDetails?.profile?.village
                        binding.tvWelcomeName.text = ", ${it.data?.name}"
                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                weather(it1, it2)
                                lat = it.data?.profile?.lat.toString()
                                long = it.data?.profile?.long.toString()
//                                Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                        it.data?.roleId?.let { it1 -> checkRole(it1) }
                        mandiDetailCall()
                        getFarms()
                    }
                }
                is Resource.Error ->
                {
                AppUtils.translatedToastServerErrorOccurred(context)
                }
                is Resource.Loading -> {}
            }
            binding.tvAddress.text = it.data?.profile?.district
        }

    }

    private fun mandiDetailCall() {

        Log.d("MandiFromHome", "Called")

        mandiViewModel.getMandiSinglePage(lat, long).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if(it.data!=null && !it.data?.data?.records.isNullOrEmpty()){
                        if(it.data?.data?.records?.size!! > 5){
                            mandiAdapter.submitList(it.data?.data?.records?.subList(0, 5))
                        }else{
                            mandiAdapter.submitList(it.data?.data?.records)
                        }
                    }
                    binding.mandiProgressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)

                }
                is Resource.Loading -> {
                    binding.mandiProgressBar.visibility = View.VISIBLE
                }

            }
//                    mandiAdapter.submitList()

        }
    }


    private fun initClick() {

        newsBinding.viewAllNews.setOnClickListener {
            EventClickHandling.calculateClickEvent("NewsArticles_viewall")

            val intent = Intent(context, NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }
        newsBinding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("NewsArticles_viewall")
            val intent = Intent(context, NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }
        videosBinding.viewAllVideos.setOnClickListener {
            EventClickHandling.calculateClickEvent("video_viewall")
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }
        videosBinding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("video_viewall")
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }
        binding.soilTestingCv.setOnClickListener {
            EventClickHandling.calculateClickEvent("Soiltesting_landing")
            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent)
        }
        binding.tvSoilTestingKnowMore.setOnClickListener {
            EventClickHandling.calculateClickEvent("Soiltesting_landing")

            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent)
        }

        binding.cardCropHealth.setOnClickListener {
            EventClickHandling.calculateClickEvent("CropHealthCardHomePagesFragment")
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent)
        }
        binding.tvCropHealthKnowMore.setOnClickListener {
            EventClickHandling.calculateClickEvent("CropHealthKnowMoreHomePagesFragment")
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent)
        }

        binding.clCropProtect.setOnClickListener {
            EventClickHandling.calculateClickEvent("CropProtectCardHomePagesFragment")
            findNavController().navigate(R.id.action_homePagesFragment_to_nav_crop_protect)
        }

        binding.tvCropProtectKnowMore.setOnClickListener {
            EventClickHandling.calculateClickEvent("CropProtectKnowMoreHomePagesFragment")
            val intent = Intent(activity, CropProtectActivity::class.java)
            startActivity(intent)
        }

        binding.clCropInformation.setOnClickListener {
            EventClickHandling.calculateClickEvent("CropInformationCardHomePagesFragment")
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent)
        }
        binding.clAddFromServiceCropInformation.setOnClickListener {
            EventClickHandling.calculateClickEvent("CropInformationKnowMoreHomePagesFragment")
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent)
        }
        binding.tvAddFrom.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddCropHomePagesFragment")
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)

        }
        binding.tvViewAllMandi.setOnClickListener {
            EventClickHandling.calculateClickEvent("ViewAllMandiHomePagesFragment")
            this.findNavController().navigate(R.id.navigation_mandi)
        }
        binding.cvWeather.setOnClickListener {
            EventClickHandling.calculateClickEvent("WeatherCardHomePagesFragment")
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }
        binding.tvOurServiceViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("View_all_services")
            findNavController().navigate(R.id.action_homePagesFragment_to_allServicesFragment)
        }
        binding.tvEditMyCrops.setOnClickListener {
            EventClickHandling.calculateClickEvent("EditCropsHomePagesFragment")
            findNavController().navigate(R.id.action_homePagesFragment_to_editCropFragment)
        }
        binding.ivEditCrop.setOnClickListener {
            EventClickHandling.calculateClickEvent("EditCropsHomePagesFragment")
            findNavController().navigate(R.id.action_homePagesFragment_to_editCropFragment)
        }
        binding.cvAddCrop.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddCropHomePagesFragment")
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.clAddForm.setOnClickListener {
            EventClickHandling.calculateClickEvent("Added_Farm_name")
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }

        binding.tvOurAddFormData.setOnClickListener {
            EventClickHandling.calculateClickEvent("Added_Farm_name")
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }

        binding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("Added_Farm_name")
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.IvNotification.setOnClickListener {
            EventClickHandling.calculateClickEvent("NotificationsHomePagesFragment")
            this.findNavController().navigate(R.id.action_homePagesFragment_to_notificationFragment)
        }


    }

    private fun getDashBoard() {
        tokenCheckViewModel.getDasBoard().observe(viewLifecycleOwner) {
            dashboardDomain = it.data
            when (it) {
                is Resource.Success -> {
                    if (it.data?.subscription?.iot == true) {
                        binding.clAddYourFarm.visibility = View.GONE
                        binding.tvWelcomeName.visibility = View.VISIBLE
                        binding.tvGoodMorning.visibility = View.VISIBLE
                        binding.IvNotification.visibility = View.GONE
                        binding.ll.visibility = View.GONE
                        binding.tvOurServiceViewAll.visibility = View.INVISIBLE
                        binding.ivOurService.visibility = View.INVISIBLE
                    }else{
                        lifecycleScope.launch {
                            val value: String? = DataStoreManager.read("FirstTime")
                            if (value != "true")
                                findNavController().navigate(R.id.action_homePagesFragment_to_spotLightFragment)
                        }
                    }
                }
                is Resource.Loading -> {


                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)

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
            newsBinding.ivViewAll.visibility = View.GONE
            AppUtils.translatedToastCheckInternet(context)


        } else {
            newsBinding.videoCardNoInternet.visibility = View.GONE
            newsBinding.newsListRv.visibility = View.VISIBLE
            newsBinding.viewAllNews.visibility = View.VISIBLE
            newsBinding.ivViewAll.visibility = View.VISIBLE

            newsBinding.viewAllNews.isClickable = true
            newsBinding.ivViewAll.isClickable = true
            setNews()
        }
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            videosBinding.videoCardNoInternet.visibility = View.VISIBLE
            videosBinding.videosListRv.visibility = View.GONE
            videosBinding.viewAllVideos.visibility = View.GONE
            videosBinding.ivViewAll.visibility = View.GONE
            videosBinding.videosScroll.visibility = View.GONE
            AppUtils.translatedToastCheckInternet(context)

        } else {
            videosBinding.videoCardNoInternet.visibility = View.GONE
            videosBinding.videosListRv.visibility = View.VISIBLE
            videosBinding.viewAllVideos.visibility = View.VISIBLE
            videosBinding.ivViewAll.visibility = View.VISIBLE

            videosBinding.videosScroll.visibility = View.VISIBLE
            setVideos()
        }
    }


    private fun getFarms() {
        viewModel.getMyFarms().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (!it.data.isNullOrEmpty()) {
                        binding.clAddForm.visibility = View.GONE
                        binding.clMyForm.visibility = View.VISIBLE
                        binding.farmsDetailsCl.visibility = View.VISIBLE
                        val farmsAdapter = FarmsAdapter(requireContext(), this)
                        binding.farmsRv.adapter = farmsAdapter

                        val sortedList = it.data?.sortedByDescending { farm ->
                            farm.isPrimary == 1
                        }
                        farmsAdapter.submitList(sortedList)
                    } else {
                        binding.clAddForm.visibility = View.VISIBLE
                        binding.clMyForm.visibility = View.GONE
                        binding.farmsDetailsCl.visibility = View.GONE
                    }

                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
                else -> {

                }
            }
        }
    }

    private fun populateMyFarm() {
        binding.farmnameHome.text = selectedFarm?.farmName

        binding.farmsDetailsCl.setOnClickListener {
            val eventBundle=Bundle()
            eventBundle.putString("farmName",selectedFarm?.farmName)
            EventItemClickHandling.calculateItemClickEvent("ViewFarmDetails",eventBundle)

            val bundle = Bundle()
            bundle.putParcelable("farm", selectedFarm)
            findNavController().navigate(R.id.action_homePagesFragment_to_nav_farmdetails, bundle)
        }
        drawFarmBoundaries(selectedFarm?.farmJson)
        binding.tvCityName.text = selectedFarm?.farmLocation
        viewModel.getMyCrop2().observe(viewLifecycleOwner) { crops ->
            val croplist =
                crops.data?.filter { filter ->
                    filter.farmId == selectedFarm?.id
                }
            farmsCropsAdapter.submitList(croplist)
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

        val bannerAdapter = AdsAdapter(activity ?: requireContext(), binding.bannerViewpager)

        runnable =Runnable {
            if ((bannerAdapter.itemCount - 1) == binding.bannerViewpager.currentItem)
                binding.bannerViewpager.currentItem = 0
            else
                binding.bannerViewpager.currentItem += 1
        }
        binding.bannerViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (runnable != null) {
                    AppUtil.handlerSet(handler!!,runnable!!,3000)
                }
            }
        })
        viewModel.getVansAdsList("49").observe(viewLifecycleOwner) {

            bannerAdapter.submitList(it.data)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.itemCount}"
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
        val adapter = NewsGenericAdapter(context, this)
        newsBinding.newsListRv.adapter = adapter
        lifecycleScope.launch((Dispatchers.Main)) {
            viewModel.getVansNewsList().collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    newsBinding.videoCardNoInternet.visibility = View.VISIBLE
                    newsBinding.noDataNews.visibility = View.GONE
                    newsBinding.viewAllNews.visibility = View.GONE
                    newsBinding.ivViewAll.visibility = View.GONE
                    newsBinding.newsListRv.visibility = View.INVISIBLE
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->

                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    newsBinding.noDataNews.visibility = View.VISIBLE
                                    newsBinding.videoCardNoInternet.visibility = View.GONE
                                    newsBinding.tvNoVANS.text = "News and Articles are being loaded.Please wait for some time"
                                    newsBinding.newsListRv.visibility = View.INVISIBLE
                                    newsBinding.viewAllNews.visibility = View.GONE
                                    newsBinding.ivViewAll.visibility = View.GONE
                                }

                                if (it1 is LoadState.NotLoading) {
                                    if (adapter.itemCount == 0) {
                                        newsBinding.noDataNews.visibility = View.VISIBLE
                                        TranslationsManager().loadString("news_not_available", newsBinding.tvNoVANS, "News and Articles are not \navailable with us.")
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.INVISIBLE
                                        newsBinding.viewAllNews.visibility = View.GONE
                                        newsBinding.ivViewAll.visibility = View.GONE

                                    } else {
                                        newsBinding.noDataNews.visibility = View.GONE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.VISIBLE
                                        newsBinding.viewAllNews.visibility = View.VISIBLE
                                        newsBinding.ivViewAll.visibility = View.VISIBLE


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
            viewModel.getVansVideosList().collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    videosBinding.videoCardNoInternet.visibility = View.VISIBLE
                    videosBinding.noDataVideo.visibility = View.GONE
                    videosBinding.ivViewAll.visibility = View.GONE
                    videosBinding.viewAllVideos.visibility = View.GONE
                    videosBinding.videosListRv.visibility = View.INVISIBLE
                } else {

                    adapter.loadStateFlow.map { it.refresh }
                        .distinctUntilChanged()
                        .collect { it1 ->

                            if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                videosBinding.noDataVideo.visibility = View.VISIBLE
                                videosBinding.tvNoVANs.text = "Videos are being loaded.Please wait for some time"
                                videosBinding.ivViewAll.visibility = View.GONE
                                videosBinding.viewAllVideos.visibility = View.GONE
                                videosBinding.videoCardNoInternet.visibility = View.GONE
                                videosBinding.videosListRv.visibility = View.INVISIBLE
                            }

                            if (it1 is LoadState.NotLoading) {
                                if (adapter.itemCount == 0) {
                                    videosBinding.noDataVideo.visibility = View.VISIBLE
                                    TranslationsManager().loadString("videos_not_available", videosBinding.tvNoVANs, "Videos are not available with us.")
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

        adapter.onItemClick = {
            val eventBundle=Bundle()
            eventBundle.putString("title",it?.title)
            EventItemClickHandling.calculateItemClickEvent("Homepage_video",eventBundle)
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
                        binding.tvDay.text = " $formatedDate"
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
            EventClickHandling.calculateClickEvent("AddFabBtnHomeFragment")

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
            EventClickHandling.calculateClickEvent("call_icon")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener {
            EventClickHandling.calculateClickEvent("chat_icon")
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
        bundle.putString("vansType", it?.vansType)

        findNavController().navigate(
            R.id.action_homePagesFragment_to_newsFullviewActivity2,
            bundle
        )
    }

    private fun notification() {
        viewModel.getNotification().observe(viewLifecycleOwner) {
            var data = it.data?.data?.filter { itt ->
                itt.readAt == null
            }
            if (data?.size != 0) {
                binding.IvNotification.setImageResource(com.example.soiltesting.R.drawable.ic_notification)
            } else {
                binding.IvNotification.setImageResource(R.drawable.ic_simple_notification)
            }
        }
    }

    override fun onFarmSelected(data: MyFarmsDomain) {
        val  eventBundle=Bundle()
        eventBundle.putString("SelectedFarmHomeFrag",data.farmName)
        EventItemClickHandling.calculateItemClickEvent("SelectedFarmHomeFrag",eventBundle)
        selectedFarm = data
        populateMyFarm()
    }

    private fun checkRole(roleId: Int) {
        if (roleId == 31) {
            binding.tvEditMyCrops.visibility = View.INVISIBLE
            binding.ivEditCrop.visibility = View.INVISIBLE
            binding.cvAddCrop.isEnabled = false
            binding.clAdd.visibility = View.INVISIBLE
            binding.clAddForm.visibility = View.GONE
            binding.tvMyForm.visibility = View.INVISIBLE
            binding.ivMyForm.visibility = View.GONE

        }
    }

    override fun onPause() {
        super.onPause()
        if (runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
    }
    override fun onResume() {
        super.onResume()
        if (runnable != null) {
            handler?.postDelayed(runnable!!, 3000)
        }
        EventScreenTimeHandling.calculateScreenTime("HomePagesFragment")
    }

}

