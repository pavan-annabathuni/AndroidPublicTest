package com.waycool.weather.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.utils.Resource
import com.waycool.videos.adapter.BannerAdapter
import com.waycool.weather.R
import com.waycool.weather.adapters.HourlyAdapter
import com.waycool.weather.adapters.WeatherAdapter
import com.waycool.weather.databinding.FragmentWeatherBinding
import com.waycool.weather.utils.Constants.*
import com.waycool.weather.viewModel.WeatherViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var shareLayout: LinearLayout
    val yellow = "#070D09"
    val lightYellow = "#FFFAF0"
    val red = "#FF2C23"
    val lightRed = "#FFD7D0"
    val green = "#08FA12"
    val lightGreen = "#08FA12"
    var bannerImageList: MutableList<com.waycool.data.Network.NetworkModels.AdBannerImage> = java.util.ArrayList()
    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    class AdBannerImage(var url: String, var current_page: String, var position: String)

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
        shareLayout = binding.shareScreen
        binding.imgShare.setOnClickListener() {
            screenShot()
        }
        binding.recycleView.adapter = WeatherAdapter(WeatherAdapter.DiffCallback.OnClickListener {
            viewModel.displayPropertyDaily(it)
        })
        binding.recycleViewHourly.adapter = HourlyAdapter(HourlyAdapter.OnClickListener {
            viewModel.displayPropertyHourly(it)
        })

        observer()
        setBanners()
        getWeatherData("12.22", "77.32")
//        ViewModel.getCurrentWeather()
//        ViewModel.getWeekWeather()
//        ViewModel.getHourlyWeather()


        binding.imgBack.setOnClickListener { requireActivity().onBackPressed() }
        return binding.root
    }
//     fun onClick(){
//          binding.recycleViewHourly.setOnClickListener(){
//              binding.recycleViewHourly.setBackgroundResource(R.drawable.green_border)
//          }
//     }


    fun screenShot() {
        val now = Date()
        android.text.format.DateFormat.format("", now)
        val path = context?.getExternalFilesDir(null)?.absolutePath + "/" + now + ".jpg"
        val bitmap =
            Bitmap.createBitmap(shareLayout.width, shareLayout.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        shareLayout.draw(canvas)
        val imageFile = File(path)
        val outputFile = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputFile)
        outputFile.flush()
        outputFile.close()
        val URI = FileProvider.getUriForFile(requireContext(), "com.example.outgrow", imageFile)

        val i = Intent()
        i.action = Intent.ACTION_SEND
        //i.putExtra(Intent.EXTRA_TEXT,"Title")
        i.putExtra(Intent.EXTRA_STREAM, URI)
        i.type = "text/plain"
        startActivity(i)
    }

    private fun observer() {


        viewModel.getUserDetails().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {

                    Log.d("Profile", it.data.toString())
                    it.data.let { userDetails ->
                        Log.d("Profile", userDetails.toString())


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
            binding.location.text = it.data?.profile?.districtId
        }
//
//        GlobalScope.launch {
//            getWeatherData("16.22","72.33")
//        }

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


    private fun getWeatherData(lat: String, lon: String) {
        viewModel.getWeather("12.22", "78.22").observe(requireActivity()) {

            if (it?.data != null) {

                binding.weatherMaster = it.data

                if (null != it) {
                    val date: Long? = it.data?.current?.dt?.times(1000L)
                    val dateTime = Date()
                    if (date != null) {
                        dateTime.time = date
                    }
                    val formatter =
                        SimpleDateFormat("EE, d MMMM", Locale.ENGLISH)//or use getDateInstance()
                    val formatedDate = formatter.format(dateTime)
                    binding.date.text = formatedDate
                }
                // binding.icon22.text = it.data?.current?.weather?.get(0)?.description
                if (it.data?.current?.weather?.isEmpty() == false)
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
                            // binding.icon2.setTextColor(Color.parseColor(red))
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
                            //binding.icon2.setTextColor(Color.parseColor(yellow))
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
                            binding.tvTodayTips.text = it.data?.current?.weather?.get(0)?.description
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

    private fun setBanners() {
        val adBannerImage =
            com.waycool.data.Network.NetworkModels.AdBannerImage(
                "https://www.digitrac.in/pub/media/magefan_blog/Wheat_crop.jpg",
                "1",
                "0"
            )
        bannerImageList.add(adBannerImage)
        val adBannerImage2 = com.waycool.data.Network.NetworkModels.AdBannerImage(
            "https://cdn.telanganatoday.com/wp-content/uploads/2020/10/Paddy.jpg",
            "2",
            "1"
        )
        bannerImageList.add(adBannerImage2)
        val bannerAdapter = BannerAdapter(requireContext(), bannerImageList)
        binding.bannerViewpager.adapter = bannerAdapter
        TabLayoutMediator(
            binding.bannerIndicators, binding.bannerViewpager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = "${position + 1} / ${bannerImageList.size}"
        }.attach()

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
}