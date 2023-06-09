package com.waycool.weather.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.WEATHER_SHARE_LINK
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.videos.adapter.AdsAdapter
import com.waycool.weather.R
import com.waycool.weather.adapters.HourlyAdapter
import com.waycool.weather.adapters.WeatherAdapter
import com.waycool.weather.databinding.FragmentWeatherBinding
import com.waycool.weather.utils.WeatherIcons
import com.waycool.weather.viewModel.WeatherViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {
    private var selectedFarm: MyFarmsDomain? = null
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var shareLayout: ConstraintLayout
    lateinit var mWeatherAdapter: WeatherAdapter
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private var handler: Handler? = null
    private var runnable: Runnable?=null
    val yellow = "#070D09"
    val lightYellow = "#FFFAF0"
    val red = "#FF2C23"
    val green = "#146133"
    val moduleId="6"
    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentWeatherBinding.inflate(inflater)
        binding.lifecycleOwner = this
        apiErrorHandlingBinding = binding.errorState
       networkErrorStateTranslations(apiErrorHandlingBinding)

        shareLayout = binding.clScreenShare
        binding.imgShare.setOnClickListener {
            binding.clShareProgress.visibility=View.VISIBLE
            binding.imgShare.isEnabled = false
            EventClickHandling.calculateClickEvent("weather_share")
            screenShot()
        }
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }
        observer()

        mWeatherAdapter = WeatherAdapter(WeatherAdapter.DiffCallback.OnClickListener {
            EventClickHandling.calculateClickEvent("weather_daily")
            viewModel.displayPropertyDaily(it)
        })
        binding.recycleView.adapter = mWeatherAdapter
        binding.recycleViewHourly.adapter = HourlyAdapter(HourlyAdapter.OnClickListener {
            EventClickHandling.calculateClickEvent("weather_hourly")
            viewModel.displayPropertyHourly(it)
        })
        translation()
        binding.lableRain.isSelected = true
        binding.lableVisibility.isSelected = true

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        EventClickHandling.calculateClickEvent("weather_landing_page")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.let {
                        it.finish()
                    }
                }
            }
        handler = Handler(Looper.myLooper()!!)

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
    }

    private fun networkCall() {
        if(NetworkUtil.getConnectivityStatusString(context)==0){
            binding.clInclude.visibility=View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility=View.VISIBLE
            AppUtils.translatedToastCheckInternet(context)
        }
        else{
            binding.clInclude.visibility=View.GONE
            apiErrorHandlingBinding.clInternetError.visibility=View.GONE

            observer()
            setBanners()
        }
    }

 /** function that takes screen-shot and share the scree-shot and dynamic link with it*/
    fun screenShot() {

        val now = Date()
        android.text.format.DateFormat.format("", now)
        val path = context?.getExternalFilesDir(null)?.absolutePath + "/" + now + ".jpg"
        val bitmap =
            Bitmap.createBitmap(shareLayout.width, shareLayout.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        shareLayout.draw(canvas)
        val imageFile = File(path)
        val outputFile = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputFile)
        outputFile.flush()
        outputFile.close()
        val URI = FileProvider.getUriForFile(requireContext(), "com.example.outgrow", imageFile)


     val share = Intent(Intent.ACTION_SEND)
     share.type = "text/plain"
     share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
     share.putExtra(Intent.EXTRA_SUBJECT, "View weather details")
     share.putExtra(Intent.EXTRA_STREAM, URI)
     share.putExtra(Intent.EXTRA_TEXT, WEATHER_SHARE_LINK)
     binding.clShareProgress.visibility=View.GONE
     Handler().postDelayed({ binding.imgShare.isEnabled = true
     },1000)
     startActivity(Intent.createChooser(share, "Share link!"))


    }

    private fun observer() {
        viewModel.viewModelScope.launch {
            viewModel.getUserProfileDetails().observe(viewLifecycleOwner){
                binding.location.text = it.data?.profile?.village
            }
        }

        viewModel.getMyFarms().observe(requireActivity()) {
            if (it.data.isNullOrEmpty()) {
                loadWeatherFromRegisteredLocation()
            } else {
                loadWeatherFromFarms(it.data!!)
            }
        }
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    WeatherFragmentDirections.actionWeatherFragmentToSheetDialogFragment(it)
                )
                viewModel.displayPropertyDetailsCompleteDaily()
            }
        }
        viewModel.navigateToSelectedHourly.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    WeatherFragmentDirections.actionWeatherFragmentToSheetHourlyFragment(it)
                )
                viewModel.displayPropertyDetailsCompleteHourly()
            }
        }
    }

    private fun loadWeatherFromRegisteredLocation() {
        binding.horizontalScrollView4.visibility = View.GONE
        viewModel.getUserDetails().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    Log.d("Profile", it.data.toString())
                    it.data.let { userDetails ->
                        Log.d("Profile", userDetails?.profile?.lat + userDetails?.profile?.long)
                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                Log.d("Profile", it1 + it2)
                                getWeatherData(it1, it2)
                            }
                        }
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
//            binding.location.text = it.data?.profile?.village
        }
    }

    /** showing weather location accordingly farm*/
    private fun loadWeatherFromFarms(farms: List<MyFarmsDomain>) {

        binding.horizontalScrollView4.visibility = View.VISIBLE
        if (farms.isNotEmpty()) {
            binding.myfarmsChipGroup.removeAllViews()
            selectedFarm = null
            for (farm in farms) {
                createChip(farm)
            }

        }
    }

    private fun createChip(farm: MyFarmsDomain) {
        val chip = Chip(requireContext())
        chip.text = farm.farmName
        chip.isCheckable = true
        chip.isClickable = true
        chip.isCheckedIconVisible = true
        chip.setTextColor(
            AppCompatResources.getColorStateList(
                requireContext(),
                com.waycool.uicomponents.R.color.bg_chip_text
            )
        )
        chip.setChipBackgroundColorResource(com.waycool.uicomponents.R.color.chip_bg_selector)
        chip.chipStrokeWidth = 1f
        chip.chipStrokeColor = AppCompatResources.getColorStateList(
            requireContext(),
            com.waycool.uicomponents.R.color.strokegrey
        )

        if (selectedFarm == null) {
            chip.isChecked = true
            selectedFarm = farm
            val centroid = selectedFarm?.farmCenter
            getWeatherData(
                centroid?.get(0)?.latitude.toString(),
                centroid?.get(0)?.longitude.toString()
            )
            binding.location.text = selectedFarm?.farmName

        }

        chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                selectedFarm = farm
                val centroid = selectedFarm?.farmCenter
                getWeatherData(
                    centroid?.get(0)?.latitude.toString(),
                    centroid?.get(0)?.longitude.toString()
                )
                binding.location.text = selectedFarm?.farmName
            }
        }
        binding.myfarmsChipGroup.addView(chip)
    }


    private fun getWeatherData(lat: String, lon: String) {
        viewModel.getWeather(lat, lon).observe(requireActivity()) {
            if (it?.data != null) {
                if (!it.data?.current?.weather.isNullOrEmpty())
                    WeatherIcons.setWeatherIcon(
                        it.data?.current?.weather?.get(0)?.icon.toString(),
                        binding.weatherIcon
                    )
                //Toast.makeText(context, "${it.data?.current?.weather?.get(0)?.icon}", Toast.LENGTH_SHORT).show()
                binding.weatherMaster = it.data

                if (null != it.data) {
                    val date: Long? = it.data?.current?.dt?.times(1000L)
                    val dateTime = Date()
                    if (date != null) {
                        dateTime.time = date
                    }
                    val formatter =
                        SimpleDateFormat(
                            "EE, d MMMM",
                            Locale.ENGLISH
                        )//or use getDateInstance()
                    val formatedDate = formatter.format(dateTime)
                    binding.date.text = formatedDate

                if (!it.data?.current?.weather.isNullOrEmpty()) {
                    WeatherIcons.setWeatherIcon(
                        it.data?.current?.weather?.get(0)?.icon.toString(),
                        binding.weatherIcon
                    )
                    //Toast.makeText(context, "${it.data?.current?.weather?.get(0)?.icon}", Toast.LENGTH_SHORT).show()
                    binding.weatherMaster = it.data
                   /** checking the id of weather and setting the description */
                    when (it.data?.current?.weather?.get(0)?.id) {
                        200 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon22.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        201 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        202 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )

                        }
                        210 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        211 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        212 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //   binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        221 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        230 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //  binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        231 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        232 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        300 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0

                            )
                        }
                        301 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        302 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        310 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        311 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        312 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_warning, 0, 0, 0
                            )
                        }
                        313 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        314 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))
                        }
                        321 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))
                            binding.tvTodayTips.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        500 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        501 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        502 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))
                        }
                        503 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))

                        }
                        504 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(red))

                        }
                        511 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(red))

                        }
                        520 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        521 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        522 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(red))

                        }
                        531 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(red))

                        }
                        701 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        711 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //  binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        721 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        731 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        741 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        751 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        761 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        800 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(green))
                            //
                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation, 0, 0, 0
                            )

                        }
                        801 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            //binding.icon2.setTextColor(Color.parseColor(green))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation, 0, 0, 0
                            )
                        }
                        802 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(green))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation, 0, 0, 0
                            )
                        }
                        803 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )
                        }
                        804 -> {
                            binding.tvTodayTips.text =
                                it.data?.current?.weather?.get(0)?.description
                            // binding.icon2.setTextColor(Color.parseColor(yellow))

                            binding.icon2.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_circle_exclamation_brown, 0, 0, 0
                            )

                                    }
                                }
                            }
                        }
                    }
            }
        }



    private fun setBanners() {

        val bannerAdapter = AdsAdapter(activity?:requireContext(), binding.bannerViewpager)
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
        viewModel.getVansAdsList(moduleId).observe(viewLifecycleOwner) {

            bannerAdapter.submitList( it.data)
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

        bannerAdapter.onItemClick = {
            EventClickHandling.calculateClickEvent("weather_Adbanner")
        }
    }
    private fun translation(){
        TranslationsManager().loadString("str_Weather",binding.textView,"Weather")
        TranslationsManager().loadString("str_share",binding.imgShare,"Share")
        TranslationsManager().loadString("str_today",binding.icon2,"Today")
        TranslationsManager().loadString("str_today",binding.today,"Today")
        TranslationsManager().loadString("str_humidity",binding.lableHumidity,"Humidity")
        TranslationsManager().loadString("str_vsibility",binding.lableVisibility,"Visibility")
        TranslationsManager().loadString("str_wind",binding.lableWind,"Wind")
        TranslationsManager().loadString("str_rain",binding.lableRain,"Chance of Rain")
        TranslationsManager().loadString("str_hourly",binding.tvHouly,"Hourly")
        TranslationsManager().loadString("str_next",binding.tvDaily,"Next 7 Days")

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
        EventScreenTimeHandling.calculateScreenTime("WeatherFragment")
    }
}