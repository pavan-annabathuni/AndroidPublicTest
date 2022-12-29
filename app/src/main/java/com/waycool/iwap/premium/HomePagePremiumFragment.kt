package com.waycool.iwap.premium

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.addcrop.AddCropActivity
import com.example.adddevice.AddDeviceActivity
import com.example.irrigationplanner.IrrigationPlannerActivity
import com.example.soiltesting.ui.checksoil.AdsAdapter
import com.github.anastr.speedviewlib.components.Section
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.Polygon
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import java.util.*
import kotlin.math.roundToInt


class HomePagePremiumFragment : Fragment(), ViewDeviceFlexListener, Farmdetailslistener,
    myCropListener {
    private var _binding: FragmentHomePagePremiumBinding? = null
    private val binding get() = _binding!!
    private var polygon: Polygon? = null
    private var mMap: GoogleMap? = null
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    private var myCropPremiumAdapter = MyCropPremiumAdapter(this)

    var viewDeviceListAdapter = ViewDeviceListAdapter(this)
    var deviceDataAdapter = DeviceDataAdapter()

    //    private var accountID: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomePagePremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickEvents()
        initViewProfile()
        initViewAddCrop()
        initMyCropObserve()
        initObserveMYFarm()
        initObserveDevice()
        progressColor()
        initViewPager()
        fabButton()
        setBanners()
        val fragment: ArrayList<Fragment> = arrayListOf(
            DeviceFragmentOne(),
            DeviceFragmentTwo()

        )

        val map = mutableMapOf<String, Any>()
        map.put("serial_no_id", 1)
        map.put("device_model_id", 2)
        val adapter = ViewPagerAdapter(fragment, requireParentFragment())
//        binding.idViewPager.adapter=adapter

        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in (1..11) -> binding.tvGoodMorning.text = "Good Morning!"
            in 12..15 -> binding.tvGoodMorning.text = "Good Afternoon!"
            in 16..20 -> binding.tvGoodMorning.text = "Good Evening!"
            in 21..23 -> binding.tvGoodMorning.text = "Good Night!"
            else -> binding.tvGoodMorning.text = "Namaste"
        }

        binding.IvNotification.setOnClickListener {
            findNavController().navigate(R.id.action_homePagePremiumFragment3_to_notificationFragment2)
        }

    }

    private fun initViewPager() {
//        binding.tvWelcomeName.setOnClickListener {
//            findNavController().navigate(R.id.action_homePagePremiumFragment2_to_deviceFragmentOne)
//        }


    }

    private fun initObserveDevice() {
        viewDevice.getIotDevice().observe(requireActivity()) {
            if (it.data?.data.isNullOrEmpty()) {
                binding.cardAddDevice.visibility = View.VISIBLE
            } else
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.data != null) {
                            binding.cardAddDevice.visibility = View.GONE
                            binding.cardMYDevice.visibility = View.VISIBLE
                            val response = it.data!!.data as ArrayList<ViewDeviceData>
                            binding.deviceFarm.adapter = viewDeviceListAdapter
                            viewDeviceListAdapter.setMovieList(response)
                        }

                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(requireContext(), "Error", Toast.LENGTH_SHORT)
                    }
                    is Resource.Loading -> {
                        ToastStateHandling.toastWarning(requireContext(), "Loading", Toast.LENGTH_SHORT)

                    }
                }

        }


//

////        binding.rvMyDevice.adapter = deviceDataAdapter
//        viewDevice.getIotDevice().observe(viewLifecycleOwner) { it ->
////            if (it.data?.data==null){
////                Toast.makeText(requireContext(), "Server Data", Toast.LENGTH_SHORT).show()
////            }
//            if (it.data?.data?.isEmpty() == true) {
//                binding.cardAddDevice.visibility = View.VISIBLE
//            } else
//                when (it) {
//                    is Resource.Success -> {
////                        binding.cardAddDevice.visibility = View.GONE
//                        val response = it.data!!.data as ArrayList<ViewDeviceData>
//                        viewDeviceListAdapter.setMovieList(response)
//
////                        val arrayList = ArrayList<ModelFlex>()
////                        response.forEach {
////                            arrayList.add(ModelFlex(it.deviceName.toString()))
////                            Log.d("TAG", "initObserveDevicefgcg:$arrayList ")
////                            viewDeviceListAdapter.setMovieList(arrayList)
////                        }
////                        deviceDataAdapter.setMovieList(response)
////                        Log.d("TAG", "initObserveDevice: $response")
//                    }
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
//
//                    }
//                    is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
//
//                    }
//                }
//
//
//        }

    }

    private fun initClickEvents() {
        binding.clAddCropData.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.tvMyCrops.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.cardAddForm.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.cardAddDevice.setOnClickListener {
            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }
        binding.clAddForm.setOnClickListener {
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.MyFarm.setOnClickListener {
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.MyDevice.setOnClickListener {
            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }
//        binding.tvGoodMorning.setOnClickListener {
//            findNavController().navigate(R.id.action_homePagePremiumFragment2_to_farmDetailsFragment3)
//        }

    }

    private fun initMyCropObserve() {
        binding.rvMyCrops.adapter = myCropPremiumAdapter
        binding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)
        binding.rvMyCrops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScroll.value =
                    calculateScrollPercentage2(binding).toFloat()
            }
        })
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            val response = it.data as ArrayList<MyCropDataDomain>
            myCropPremiumAdapter.setMovieList(response)

            if (it.data.isNullOrEmpty()) {
                binding.cvEditCrop.visibility = View.GONE
                binding.cardAddForm.visibility = View.VISIBLE
            } else {
                if(it.data?.size!! >1){
                    binding.videosScroll.visibility=View.VISIBLE
                }else{
                    binding.videosScroll.visibility=View.GONE

                }
                binding.cvEditCrop.visibility = View.VISIBLE
                binding.cardAddForm.visibility = View.GONE
            }
        }

    }


    private fun initViewAddCrop() {


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
            FeatureChat.zenDeskInit(requireContext())
        }

    }


    @SuppressLint("SetTextI18n")
    private fun initViewProfile() {
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    Log.d("Profile", it.data.toString())
                    it.data.let { userDetails ->
                        Log.d("Profile", userDetails.toString())
                        Log.d("Profile", userDetails?.profile?.lat + userDetails?.profile?.long)
                        binding.tvWelcome.text = userDetails?.profile?.village
                        binding.tvWelcomeName.text = "Welcome, ${it.data?.name.toString()}"

                        Log.d("TAG", "onViewCreatedProfileUser: $it.data?.name")
                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                Log.d("Profile", it1 + it2)
//                                Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    private fun initObserveMYFarm() {
        var myFarmPremiumAdapter = MyFarmPremiumAdapter(this,requireContext())
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.map_farms_home) as SupportMapFragment?
//        mapFragment!!.requireView().isClickable = false
//        mapFragment.getMapAsync { googleMap: GoogleMap ->
//            mMap = googleMap
//            mMap?.uiSettings?.setAllGesturesEnabled(false)
//            mMap?.uiSettings?.isMapToolbarEnabled = false
//        }
        binding.rvMyFarm.adapter = myFarmPremiumAdapter
        binding.videosScrollMyFarm.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)
        binding.rvMyFarm.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScrollMyFarm.value =
                    calculateScrollPercentageFarm(binding).toFloat()
            }
        })
        viewModel.getMyFarms().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data == null) {
                        Log.d("TAG", "initObserveMYFarmPraveen: ")
                    } else if (it.data != null)
                        if (it.data!!.isNotEmpty()) {
                            binding.clAddForm.visibility = View.GONE
                            binding.cardMYFarm.visibility = View.VISIBLE
                            myFarmPremiumAdapter.setMovieList(it.data)
                            if(it.data?.size!! >1){
                                binding.videosScrollMyFarm.visibility=View.VISIBLE
                            }else
                                binding.videosScrollMyFarm.visibility=View.GONE

                        } else {
                            binding.clAddForm.visibility = View.VISIBLE
                            binding.cardMYFarm.visibility = View.GONE
//                                        binding.farmsDetailsCl.visibility = View.GONE
//                                        binding.tvAddress.visibility=View.VISIBLE

                        }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (it.data != null) {
                var accountId = it.data?.accountId
                Log.d("TAG", "initObserveMYFarmAccount $accountId: ")

//                if (accountId != null)
            }
        }
    }

//        viewModel.getUserDetails().observe(viewLifecycleOwner) {
//            if (it.data != null) {
//                var accountId = it.data?.accountId
//                if (accountId != null)
//                    viewModel.getMyFarms(721).observe(viewLifecycleOwner) {
//                        binding.rvMyFarm.adapter = myFarmPremiumAdapter
//                        if (it.data!!.isEmpty()) {
//                            binding.clAddForm.visibility = View.VISIBLE
//                        } else
//                            when (it) {
//                                is Resource.Success -> {
//                                    if (it.data == null) {
//                                        binding.clAddForm.visibility = View.VISIBLE
//                                    }
//                                    binding.clAddForm.visibility = View.GONE
//                                    binding.cardMYFarm.visibility = View.VISIBLE
//                                    val response = it.data as ArrayList<MyFarmsDomain>
//
//                                    myFarmPremiumAdapter.setMovieList(response)
//
//                                }
//                                is Resource.Error -> {
//                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
//                                        .show()
//
//                                }
//                                is Resource.Loading -> {
//                                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
//                                        .show()
//
//                                }
//                            }
//                    }
//            }
//        }


    fun calculateScrollPercentage2(videosBinding: FragmentHomePagePremiumBinding): Int {
        val offset: Int = videosBinding.rvMyCrops.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.rvMyCrops.computeHorizontalScrollExtent()
        val range: Int = videosBinding.rvMyCrops.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }

    fun calculateScrollPercentageFarm(videosBinding: FragmentHomePagePremiumBinding): Int {
        val offset: Int = videosBinding.rvMyFarm.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.rvMyFarm.computeHorizontalScrollExtent()
        val range: Int = videosBinding.rvMyFarm.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }

    fun progressColor() {
//        binding.soilMoistureOne.progress = 60

    }

    @SuppressLint("SetTextI18n")
    override fun viewDevice(data: ViewDeviceData) {

        if(data.model?.series=="GSX"){
            binding.cardTopParent.visibility=View.GONE
            binding.clTempView.visibility=View.GONE
        }else{
            binding.cardTopParent.visibility=View.VISIBLE
            binding.clTempView.visibility=View.VISIBLE
        }

        binding.let {

            it.totalAreea.text = data.iotDevicesData?.battery.toString()
            it.tvAddDeviceStart.text = data.model?.modelName.toString()
            it.tvTempDegree.text = data.temperature.toString() + " \u2103"
            it.tvWindDegree.text = data.rainfall.toString() + " mm"
            it.tvHumidityDegree.text = data.humidity.toString() + " %"
            it.tvWindSpeedDegree.text = data.windspeed.toString() + " Km/h"
            if (data.leafWetness!=null && data.leafWetness!!.equals(1)) {
                it.tvLeafWetnessDegree.text = "Wet"
                it.ivLeafWetness.setImageResource(R.drawable.ic_leaf_wetness)
            } else {
                it.tvLeafWetnessDegree.text = "Dry"
                it.ivLeafWetness.setImageResource(R.drawable.ic_dry_image)
            }
//            val imageIndicator = ImageIndicator(requireContext(), R.drawable.image_indicator1)
//
//            speedometer.indicator = imageIndicator
            it.tvPressureDegree.text = data.pressure.toString() + " hPa"
            it.ivSoilDegree.text = data.soilTemperature1.toString() + " \u2103"
            it.ivSoilDegreeOne.text = data.lux.toString() + " Lux"
            it.tvLastUpdate.text = data.dataTimestamp.toString()
            binding.soilMoistureOne.clearSections()
            binding.soilMoistureTwo.clearSections()
            binding.kpaOne.text = data.soilMoisture1 + " kPa"
            binding.kpaTwo.text = data.soilMoisture2 + " kPa"

            binding.soilMoistureOne.addSections(
                Section(0f, .1f, Color.parseColor("#32A9FF"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.1f, .3f, Color.parseColor("#5FC047"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.3f, .5f, Color.parseColor("#FEC253"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.5f, 1f, Color.parseColor("#914734"), binding.soilMoistureOne.dpTOpx(12f))
            )
            //two
//            binding.soilMoistureOne .indicator.color = Color.RED
            binding.soilMoistureTwo.addSections(
                Section
                    (0f, .1f, Color.parseColor("#32A9FF"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.1f, .3f, Color.parseColor("#5FC047"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.3f, .5f, Color.parseColor("#FEC253"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.5f, 1f, Color.parseColor("#914734"), binding.soilMoistureTwo.dpTOpx(12f))
            )


//            binding.soilMoistureOne.addSections(
//                Section(0f, .1f, Color.parseColor("#DA0101"),  binding.soilMoistureOne.dpTOpx(5f)),
//                Section(.1f, .4f, Color.parseColor("#01B833"),   binding.soilMoistureOne.dpTOpx(5f)),
//                Section(.4f, .7f, Color.parseColor("#F3C461"),   binding.soilMoistureOne.dpTOpx(20f)),
//                Section(.7f, .9f, Color.parseColor("#DA0101"),   binding.soilMoistureOne.dpTOpx(20f))
//            )

//            binding.soilMoistureOne.setIndicator(Indicator.Indicators.KiteIndicator)
//            binding.soilMoistureTwo.setIndicator(Indicator.Indicators.KiteIndicator)

//            binding.soilMoistureOne.ticks= arrayListOf(0f,.1f,.3f,.5f,1f)
            binding.soilMoistureOne.tickNumber = 0
            binding.soilMoistureOne.marksNumber = 0

            binding.soilMoistureTwo.tickNumber = 0
            binding.soilMoistureTwo.marksNumber = 0

//            binding.soilMoistureOne.speedTo()
            binding.soilMoistureOne.maxSpeed = 100F
            binding.soilMoistureTwo.maxSpeed = 100F
//            binding.soilMoistureOne.ticks  = listOf(10.0F)
//            binding.soilMoistureTwo.ticks = 10
            binding.soilMoistureOne.speedTo(data.soilMoisture1!!.toFloat())
            Log.d("TAG", "viewDevice:$data.soilMoisture1!!.toFloat() ")
            binding.soilMoistureTwo.speedTo(data.soilMoisture2!!.toFloat())


            binding.soilMoistureOne.speedTo(data.soilMoisture1!!.toFloat(), 100)
            binding.soilMoistureTwo.speedTo(data.soilMoisture2!!.toFloat(), 100)

//            binding.soilMoistureOne.speed(data.sensorValue1?.toFloat())
//            binding.soilMoistureOne.speedTextListener =
//                {
//                        speed: Float? ->
//                    String.format(data.sensorValue1.toString(), "lol%.0f", speed)
//                }

//            binding.soilMoistureOne.addSections(
//                Section(0f, .1f, Color.parseColor("#DA0101"),  binding.soilMoistureOne.dpTOpx(20f)),
//                Section(.1f, .4f, Color.parseColor("#01B833"),   binding.soilMoistureOne.dpTOpx(20f)),
//                Section(.4f, .7f, Color.parseColor("#F3C461"),   binding.soilMoistureOne.dpTOpx(20f)),
//                Section(.7f, .9f, Color.parseColor("#DA0101"),   binding.soilMoistureOne.dpTOpx(20f))
//            )
            //        speedView.getSections().add(new Section(1f, Color.RED));
//            binding.soilMoistureOne.onSectionChangeListener = { previousSection: Section?, newSection: Section? ->
//                if (previousSection != null) {
////                    previousSection.setPadding(10);
//                    previousSection.width = binding.soilMoistureOne.dpTOpx(20f)
//                }
//                if (newSection != null) {
////                    newSection.setPadding(0);
//                    newSection.width = binding.soilMoistureOne.dpTOpx(20f)
//                }
//                Unit
//            }


            //add color ranges to gauge
//            binding.soilMoistureOne.addRange(range)
//            binding.soilMoistureOne.addRange(range2)
//            binding.soilMoistureOne.addRange(range3)
//            binding.soilMoistureOne.addRange(range4)
//
//            binding.soilMoistureTwo.addRange(range)
//            binding.soilMoistureTwo.addRange(range2)
//            binding.soilMoistureTwo.addRange(range3)
//            binding.soilMoistureTwo.addRange(range4)
//
//            //set min max and current value
//            binding.soilMoistureOne.minValue = 0.0
//            binding.soilMoistureOne.maxValue = 100.0
//            binding.soilMoistureOne.value = data.soilMoisture1!!.toDouble()
//
//            binding.soilMoistureTwo.minValue = 0.0
//            binding.soilMoistureTwo.maxValue = 100.0
//            binding.soilMoistureTwo.value = data.soilMoisture1!!.toDouble()

//            binding.soilMoistureOne.progress = data.soilMoisture1!!.toInt()
//            binding.soilMoistureTwo.progress = data.soilMoisture2!!.toInt()
//
//            if (data.soilMoisture1?.toInt()!! < 30){
//                binding.soilMoistureOne.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
//            }else if (data.soilMoisture1?.toInt()!! < 30)
//                if (data.soilMoisture2?.toInt()!! < 30){
//                    binding.soilMoistureTwo.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
//
//                }
//            binding.soilMoistureOne.progress = data.soilMoisture1!!.toInt()
//            binding.soilMoistureTwo.progress = data.soilMoisture2!!.toInt()

//            it.tubeSpeedometer.maxSpeed = 100f
//            it.tubeSpeedometer.speedTo(140f)
//            it.tubeSpeedometer.speedometerBackColor = Color.GRAY
            it.tvLastUpdateRefresh.setOnClickListener {
                viewDeviceListAdapter.upDateList()
            }
            it.clTemp.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "temperature")
                    bundle.putString("toolbar","Temperature")
                    bundle.putString("temp_value", data.temperature)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }
            it.clWind.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "rainfall")
                    bundle.putString("toolbar","Rainfall")
                    bundle.putString("temp_value", data.rainfall)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }
//            it.clWindSpeed.setOnClickListener {
//                val bundle = Bundle()
//                if (data.serialNoId != null && data.modelId != null) {
//                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
//                    bundle.putInt("device_model_id", data.modelId!!.toInt())
//                    bundle.putString("value", "pressure")
//                    findNavController().navigate(
//                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
//                        bundle
//                    )
//                }
//            }
            it.clHumidity.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "humidity")
                    bundle.putString("toolbar","Humidity")
                    bundle.putString("temp_value", data.humidity)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }
            it.clWindSpeed.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "windspeed")
                    bundle.putString("toolbar","Wind Speed")
                    bundle.putString("temp_value", data.windspeed)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }

            it.clLeafWetness.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "leaf_wetness")
                    bundle.putString("toolbar","Leaf wetness")

                    bundle.putString("temp_value",data.leafWetness)
                    bundle.putString("date_time",data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }
            it.clPressure.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "pressure")
                    bundle.putString("toolbar","Pressure")
                    bundle.putString("temp_value", data.pressure)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }
            binding.clTop.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "soil_moisture_1")
                    bundle.putString("toolbar","Soil Moisture Top")
                    bundle.putString("temp_value", data.soilMoisture1)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )

                }
            }
            binding.bottomTop.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "soil_moisture_2")
                    bundle.putString("toolbar","Soil Moisture Bottom")
                    bundle.putString("temp_value", data.soilMoisture2)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )

                }
            }
            binding.clTempView .setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "lux")
                    bundle.putString("toolbar","Light Intensity")
                    bundle.putString("temp_value", data.lux)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )

                }

            }
            binding.clSoilTemp.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "soil_temperature_1")
                    bundle.putString("toolbar","Soil Temperature")
                    bundle.putString("temp_value", data.soilTemperature1)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )

                }

            }
//            binding.clSoilTemp.setOnClickListener {
//                val bundle = Bundle()
//                if (data.serialNoId != null && data.modelId != null) {
//                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
//                    bundle.putInt("device_model_id", data.modelId!!.toInt())
//                    bundle.putString("value", "soilmoisture")
//                    bundle.putString("toolbar","Soil Temperature")
//                    bundle.putString("temp_value", data.soilTemperature1)
//                    bundle.putString("date_time", data.dataTimestamp)
//                    findNavController().navigate(
//                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
//                        bundle
//                    )
//
//                }
//
//            }
        }


//        deviceDataAdapter.notifyDataSetChanged()
    }

    override fun farmDetails(data: MyFarmsDomain) {
        val bundle = Bundle()
        bundle.putParcelable("farm", data)
        findNavController().navigate(
            R.id.action_homePagePremiumFragment3_to_nav_farmdetails,
            bundle
        )
    }

    override fun myCropListener(data: MyCropDataDomain) {
        val bundle = Bundle()
        data.id?.let { bundle.putInt("plotId", it) }
        data.cropLogo?.let { bundle.putString("cropLogo",it) }
        data.cropName?.let { bundle.putString("cropName",it) }
        data.cropId?.let { bundle.putInt("cropId",it) }
        this.findNavController().navigate(R.id.action_homePagePremiumFragment3_to_navigation_irrigation,bundle)
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


}