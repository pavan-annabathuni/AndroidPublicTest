package com.waycool.iwap.premium

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.ekn.gruzer.gaugelibrary.Range
import com.example.addcrop.AddCropActivity
import com.example.adddevice.AddDeviceActivity
import com.example.irrigationplanner.IrrigationPlannerActivity
import com.github.anastr.speedviewlib.components.Section
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.components.indicators.ImageIndicator
import com.github.anastr.speedviewlib.components.indicators.Indicator
import com.github.anastr.speedviewlib.util.doOnSections
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.Polygon
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import java.util.*
import kotlin.math.roundToInt


class HomePagePremiumFragment : Fragment(), ViewDeviceFlexListener, farmdetailslistener,
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
        val fragment: ArrayList<Fragment> = arrayListOf(
            DeviceFragmentOne(),
            DeviceFragmentTwo()

        )

        val map = mutableMapOf<String, Any>()
        map.put("serial_no_id", 1)
        map.put("device_model_id", 2)
        val adapter = ViewPagerAdapter(fragment, requireParentFragment())
//        binding.idViewPager.adapter=adapter
    }

    private fun initViewPager() {
        binding.tvWelcomeName.setOnClickListener {
            findNavController().navigate(R.id.action_homePagePremiumFragment2_to_deviceFragmentOne)
        }


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
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

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
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            if (it.data != null) {
                var accountId = it.data?.accountId
                Log.d("TAG", "initMyCropObserveAccountId: ")
//                var accountId: Int? = null
//                for (account in it?.data?.) {
//                    if (account.accountType?.lowercase() == "outgrow") {
//                        accountId = account.id
//                    }
//
//                }
//                var accountId: Int = it.data!!.account[0].id!!
                if (accountId != null)
                    viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
//                        myCropAdapter.submitList(it.data)
                        val response = it.data as ArrayList<MyCropDataDomain>
                        myCropPremiumAdapter.setMovieList(response)
                        if ((it.data != null)) {
//                            binding.tvCount.text = it.data!!.size.toString()

                        } else {

//                            binding.tvCount.text = "0"
                        }
                        if (it.data!!.isNotEmpty()) {
                            binding.cvEditCrop.visibility = View.VISIBLE
                            binding.cardAddForm.visibility = View.GONE
                        } else {
                            binding.cvEditCrop.visibility = View.GONE
                            binding.cardAddForm.visibility = View.VISIBLE
                        }
                    }
            }
        }

    }


    private fun initViewAddCrop() {


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
        var myFarmPremiumAdapter = MyFarmPremiumAdapter(this)
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
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (it.data != null) {
                var accountId = it.data?.accountId
                Log.d("TAG", "initObserveMYFarmAccount $accountId: ")

                if (accountId != null)
                    viewModel.getMyFarms(accountId, null).observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                if (it.data == null) {
                                    Log.d("TAG", "initObserveMYFarmPraveen: ")
                                } else if (it.data != null)
                                    if (it.data!!.isNotEmpty()) {
                                        binding.clAddForm.visibility = View.GONE
                                        binding.cardMYFarm.visibility = View.VISIBLE
//                                        binding.farmsDetailsCl.visibility = View.VISIBLE
//                                        binding.tvAddress.visibility=View.INVISIBLE
                                        val response = it.data as ArrayList<MyFarmsDomain>
                                        Log.d("TAG", "initObserveMYFarmData:$response ")
                                        myFarmPremiumAdapter.setMovieList(response)
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
        binding.let {
            it.totalAreea.text = data.iotDevicesData?.battery.toString()
            it.tvAddDeviceStart.text = data.model?.modelName.toString()
            it.tvTempDegree.text = data.temperature.toString() + " \u2103"
            it.tvWindDegree.text = data.rainfall.toString() + " mm"
            it.tvHumidityDegree.text = data.humidity.toString() + " %"
            it.tvWindSpeedDegree.text = data.windspeed.toString() + " Km/h"
            if (data.leafWetness!!.equals(1)) {
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
                Section(0f, .1f, Color.parseColor("#DA0101"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.1f, .3f, Color.parseColor("#01B833"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.3f, .5f, Color.parseColor("#F3C461"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.5f, 1f, Color.parseColor("#DA0101"), binding.soilMoistureOne.dpTOpx(12f))
            )
            //two
//            binding.soilMoistureOne .indicator.color = Color.RED
            binding.soilMoistureTwo.addSections(
                Section
                    (0f, .1f, Color.parseColor("#DA0101"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.1f, .3f, Color.parseColor("#01B833"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.3f, .5f, Color.parseColor("#F3C461"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.5f, 1f, Color.parseColor("#DA0101"), binding.soilMoistureTwo.dpTOpx(12f))
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
//                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
//                        R.id.action_homePagePremiumFragment2_to_graphsFragment,
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
        bundle.putInt("farm_id", data.id!!)
        findNavController().navigate(
            R.id.action_homePagePremiumFragment2_to_farmDetailsFragment3,
            bundle
        )
    }

    override fun myCropListener(data: MyCropDataDomain) {
        val intent = Intent(activity, IrrigationPlannerActivity::class.java)
        startActivity(intent)
    }


}