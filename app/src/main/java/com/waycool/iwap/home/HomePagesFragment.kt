package com.waycool.iwap.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.addcrop.AddCropActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.example.irrigationplanner.IrrigationPlannerActivity
import com.example.mandiprice.MandiActivity
import com.example.mandiprice.viewModel.MandiViewModel
import com.example.soiltesting.SoilTestActivity
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.ZendeskChat
import com.waycool.featurecrophealth.CropHealthActivity
import com.waycool.featurecropprotect.CropProtectActivity
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentHomePagesBinding
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import com.waycool.weather.WeatherActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomePagesFragment : Fragment() {

    private var _binding: FragmentHomePagesBinding? = null
    private val binding get() = _binding!!
    private var orderBy: String = "distance"
    private var cropCategory: String? = null
    private var state: String? = null
    private var crop: String? = null
    private var search: String? = null
    private var sortBy: String = "asc"

    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val mandiViewModel by lazy { ViewModelProvider(requireActivity())[MandiViewModel::class.java] }
    private lateinit var mandiAdapter: MandiHomePageAdapter
    val yellow = "#070D09"
    val lightYellow = "#FFFAF0"
    val red = "#FF2C23"
    val lightRed = "#FFD7D0"
    val green = "#146133"
    val lightGreen = "#DEE9E2"
    private lateinit var myCropAdapter: MyCropsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePagesBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            var value:String? = DataStoreManager.read("FirstTime")
            if(value!="true")
                findNavController().navigate(R.id.action_homePagesFragment_to_spotLightFragment)
        }
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
                .navigate(
                    com.waycool.iwap.R.id.action_homePagesFragment_to_mandiGraphFragment22,
                    args
                )
        })
        binding.recyclerview.adapter = mandiAdapter
        binding.soilTestingCv.setOnClickListener {
            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent)
        }
        binding.tvAddFromService.setOnClickListener {
            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent);
        }

        binding.cardCropHealth.setOnClickListener {
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent)
        }
        binding.tvAddFromServiceCropHealth.setOnClickListener {
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent);
        }

        binding.clCropProtect.setOnClickListener {
            val intent = Intent(activity, CropProtectActivity::class.java)
            startActivity(intent)
        }

        binding.tvAddFromServiceCropProtect.setOnClickListener {
            val intent = Intent(activity, CropProtectActivity::class.java)
            startActivity(intent)
        }

        binding.clCropInformation.setOnClickListener {
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent);
        }
        binding.clAddFromServiceCropInformation.setOnClickListener {
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent);
        }

        binding.tvAddFrom.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.tvViewAllMandi.setOnClickListener {
//            val intent = Intent(activity, MandiActivity::class.java)
//            startActivity(intent)
            this.findNavController().navigate(R.id.navigation_mandi)
        }
        binding.cvWeather.setOnClickListener() {
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }
        binding.tvOurServiceViewAll.setOnClickListener {
            findNavController().navigate(com.waycool.iwap.R.id.action_homePagesFragment_to_allServicesFragment)
        }
        binding.tvMyCrops.setOnClickListener() {
            findNavController().navigate(com.waycool.iwap.R.id.action_homePagesFragment_to_editCropFragment)
        }
        binding.cvAddCrop.setOnClickListener() {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScroll.value =
                    calculateScrollPercentage2(binding).toFloat()
            }
        })

        //weather("12.22", "78.22")

        mandiViewModel.viewModelScope.launch {
            mandiViewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy, search)
                .observe(viewLifecycleOwner) {
                    mandiAdapter.submitData(lifecycle, it)
                    // binding.viewModel = it
//                adapterMandi.submitData(lifecycle,it)
                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                }
        }

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    Log.d("Profile", it.data.toString())
                    it.data.let { userDetails ->
                        Log.d("Profile", userDetails.toString())
                        Log.d("Profile", userDetails?.profile?.lat + userDetails?.profile?.long)
                        binding.tvWelcome.text = userDetails?.profile?.village
                        binding.tvWelcomeName.text = "Welcome, ${it.data?.name}"
                        Log.d("TAG", "onViewCreatedProfileUser: $it.data?.name")
                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                Log.d("Profile", it1 + it2)
                                weather(it1, it2)
//                                Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }


                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            binding.tvAddress.text = it.data?.profile?.village
        }
        setVideos()
        setNews()
        fabButton()
        myCrop()
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

//    private fun setMandi() {
//        mandiViewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search)
//        mandiViewModel
//    }


    private fun setNews() {
        val newsBinding: GenericLayoutNewsListBinding = binding.layoutNews
        val adapter = NewsGenericAdapter()
        newsBinding.newsListRv.adapter = adapter
        viewModel.getVansNewsList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        newsBinding.viewAllNews.setOnClickListener {
            val intent = Intent(requireActivity(), NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("title", it?.title)
            bundle.putString("content", it?.desc)
            bundle.putString("image", it?.thumbnailUrl)
            bundle.putString("audio", it?.audioUrl)
            bundle.putString("date", it?.startDate)
            bundle.putString("source", it?.sourceName)

            findNavController().navigate(
                com.waycool.iwap.R.id.action_homePagesFragment_to_newsFullviewActivity2,
                bundle
            )
        }

    }

    private fun setVideos() {
        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        viewModel.getVansVideosList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        videosBinding.viewAllVideos.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                com.waycool.iwap.R.id.action_homePagesFragment_to_playVideoFragment4,
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
                    Glide.with(requireContext())
                        .load("https://openweathermap.org/img/wn/${it.data!!.current!!.weather[0].icon}@4x.png")
                        .into(binding.ivWeather)
                 binding.tvHumidityDegree.text =String.format("%.0f",it.data?.current?.humidity)+"%"
                // binding.weatherMaster = it.data

                if (null != it) {
                    val date: Long? = it.data?.current?.dt?.times(1000L)
                    val dateTime = Date()
                    if (date != null) {
                        dateTime.time = date
                    }
                    val formatter =
                        SimpleDateFormat("EE d,MMM", Locale.ENGLISH)//or use getDateInstance()
                    val formatedDate = formatter.format(dateTime)
                    binding.tvDay.text = "Today $formatedDate"
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
        binding.addFab.setOnClickListener() {
            if (!isVisible) {
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener() {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener() {
            ZendeskChat.zenDesk(requireContext())
        }

    }

    private fun myCrop() {
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
//            val intent = Intent(activity, IrrigationPlannerActivity::class.java)
//            startActivity(intent)
        })
        binding.rvMyCrops.adapter = myCropAdapter
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (it.data != null) {
                var accountId: Int? = null
                for (account in it?.data?.account!!) {
                    if (account.accountType?.lowercase() == "outgrow") {
                        accountId = account.id
                    }

                }
//                var accountId: Int = it.data!!.account[0].id!!
                if (accountId != null)
                    viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
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
                        if(it.data?.size!! <8){
                            binding.addLl.visibility = View.VISIBLE
                        }else binding.addLl.visibility = View.GONE
                    }
            }
        }
    }

}