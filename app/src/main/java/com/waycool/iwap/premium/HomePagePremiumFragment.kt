package com.waycool.iwap.premium

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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.addcrop.AddCropActivity
import com.example.adddevice.AddDeviceActivity
import com.github.anastr.speedviewlib.components.Section
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.uicomponents.utils.DateFormatUtils
import com.waycool.videos.adapter.AdsAdapter
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt


class HomePagePremiumFragment : Fragment(), ViewDeviceFlexListener, Farmdetailslistener,
    FarmSelectedListener,
    myCropListener {
    private var _binding: FragmentHomePagePremiumBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    private val myCropPremiumAdapter by lazy { MyCropPremiumAdapter(this) }
    private var myFarmPremiumAdapter: MyFarmPremiumAdapter? = null
    private lateinit var handler: Handler
    private var runnable: Runnable? = null
    private var lastUpdatedTime: String? = null

    var selectedDevice: ViewDeviceDomain? = null


    var viewDeviceListAdapter = ViewDeviceListAdapter(this)

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
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        handler = Handler(Looper.myLooper()!!)

        initClickEvents()
        initViewProfile()
        initViewAddCrop()
        initMyCropObserve()
        initObserveMYFarm()
        translations()
        fabButton()
        setBanners()
        notification()
        setWishes()
        movingTextViewEnable()

        lifecycleScope.launch {
            val value: String? = DataStoreManager.read("FirstTime")
            if (value != "true")
                findNavController().navigate(R.id.action_homePagePremiumFragment3_to_spotLightFragment2)
        }

        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in (1..11) -> binding.tvGoodMorning.text = "Good Morning!"
            in 12..15 -> binding.tvGoodMorning.text = "Good Afternoon!"
            in 16..20 -> binding.tvGoodMorning.text = "Good Evening!"
            in 21..23 -> binding.tvGoodMorning.text = "Good Night!"
            else -> binding.tvGoodMorning.text = "Namaste"
        }

        binding.IvNotification.setOnClickListener {
            EventClickHandling.calculateClickEvent("NotificationsHomePagePremiumFragment")
            findNavController().navigate(R.id.action_homePagePremiumFragment3_to_notificationFragment2)
        }
        binding.tvAddFromOne.isSelected = true

        viewModel.selectedDeviceLiveData.observe(viewLifecycleOwner){
            if(it!=null){
                populateDevice(it)
            }
        }

        viewModel.selectedFarmIdLiveData.observe(viewLifecycleOwner){
            if(it!=null){
                populateFarm(it)
            }
        }

    }

    private fun movingTextViewEnable() {
        binding.tvAddFromOne.isSelected = true
        binding.tvTemp.isSelected = true
        binding.tvWind.isSelected = true
        binding.tvHumidity.isSelected = true
        binding.tvWindSpeed.isSelected = true
        binding.tvLeafWetness.isSelected = true
        binding.tvPressureDegree.isSelected = true
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

    fun translations() {
        TranslationsManager().loadString("welcome", binding.tvName, "Welcome")
        TranslationsManager().loadString("add_crop_info", binding.tvYourForm, "Add your Crop and get more details.")
        TranslationsManager().loadString("add_crop", binding.tvAddFrom, "Add crops")
        TranslationsManager().loadString("my_crops", binding.title3SemiBold, "My Crops")
        TranslationsManager().loadString("add_crop", binding.tvEditMyCrops, "Add crops")
        TranslationsManager().loadString("add_farm", binding.tvAddFromOne, "Add your farm")
        TranslationsManager().loadString("my_farm", binding.titleMyFarm, "")
        TranslationsManager().loadString("add_farm_top", binding.MyFarm, "Add Farm")
        TranslationsManager().loadString("my_device", binding.titleMyDevice, "My Devices")
        TranslationsManager().loadString("str_add_device", binding.MyDevice, "My Devices")
        TranslationsManager().loadString("view_tepm", binding.tvTemp, "Temperature")
        TranslationsManager().loadString("view_rainfall", binding.tvWind, "Rainfall")
        TranslationsManager().loadString("str_humidity", binding.tvHumidity, "Humidity")
        TranslationsManager().loadString("str_wind_speed", binding.tvWindSpeed, "Wind Speed")
        TranslationsManager().loadString("view_leaf", binding.tvLeafWetness, "Leaf wetness")
        TranslationsManager().loadString("view_pressure", binding.tvPressure, "Pressure")
        TranslationsManager().loadString("view_light", binding.ivSoilTempText, "Light Intensity")
        TranslationsManager().loadString("soil_moisture", binding.SoilMoisture, "Soil Moisture")
        TranslationsManager().loadString("view_top", binding.tvTop, "Top")
        TranslationsManager().loadString("view_bottom", binding.tvBottom, "Bottom")
        TranslationsManager().loadString("view_soil_temp", binding.ivSoilTemp, "Soil Temperature")
        TranslationsManager().loadString("battery", binding.tvEnableAddDevice, "Battery")
        TranslationsManager().loadString("elevation", binding.tvEnableAddDeviceTwo, "Elevation")
        TranslationsManager().loadString("update", binding.tvLastUpdateRefresh, "Update")
        TranslationsManager().loadString("no_devices_add", binding.devicesEmptyText, "No Devices added for this farm")
    }

    private fun initObserveDevice(farmId: Int) {
        viewModel.setSelectedFarm(farmId)
        populateFarm(farmId)
    }

    private fun populateFarm(farmId: Int){
        viewDevice.getIotDeviceByFarm(farmId).observe(requireActivity()) {
            checkForDeviceApiUpdate()
            when (it) {
                is Resource.Success -> {
                    if (!it.data.isNullOrEmpty()) {
                        binding.cardMYDevice.visibility = View.VISIBLE
                        binding.deviceParamsCL.visibility = View.VISIBLE
                        binding.deviceFarm.visibility = View.VISIBLE
                        binding.devicesEmptyText.visibility = View.GONE
                        binding.deviceFarm.adapter = viewDeviceListAdapter
                        viewDeviceListAdapter.setMovieList(it.data!!)
                    } else {
                        binding.cardMYDevice.visibility = View.VISIBLE
                        binding.deviceParamsCL.visibility = View.GONE
                        binding.deviceFarm.visibility = View.GONE
                        binding.devicesEmptyText.visibility = View.VISIBLE
                        TranslationsManager().loadString("no_devices_add", binding.devicesEmptyText, "No Devices added for this farm")

                            }
                    }
                    is Resource.Error -> {
                        AppUtils.translatedToastServerErrorOccurred(context)
                    }
                    is Resource.Loading -> {
                        AppUtils.translatedToastCheckInternet(context)

                    }
                }

        }

    }

    private fun checkForDeviceApiUpdate() {
        activity?.let {
            viewModel.getLatestTimeStamp().observe(it) { time ->

                if (lastUpdatedTime.isNullOrEmpty()) {
                    lastUpdatedTime = time
                }
                if (lastUpdatedTime != time) {
                    lastUpdatedTime = time
                    binding.updateProgressDevice.visibility = View.INVISIBLE
                    binding.ivUpdate.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun initClickEvents() {
        binding.clAddCropData.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddCropHomePremiumFragment")
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.tvEditMyCrops.setOnClickListener {
            EventClickHandling.calculateClickEvent("EditCropsHomePremiumFragment")
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.cardAddForm.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddCropHomePremiumFragment")
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
//        binding.cardAddDevice.setOnClickListener {
//            val intent = Intent(activity, AddDeviceActivity::class.java)
//            startActivity(intent)
//        }
        binding.clAddForm.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddYourFarmHomePremiumFragment")

            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.MyFarm.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddYourFarmHomePremiumFragment")

            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddYourFarmHomePremiumFragment")
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }

        binding.MyDevice.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddYourDeviceHomePremiumFragment")

            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }


        binding.tvLastUpdateRefresh.setOnClickListener {
            updateDevice()

        }

    }

    private fun updateDevice() {
        binding.ivUpdate.visibility = View.INVISIBLE
        binding.updateProgressDevice.visibility = View.VISIBLE
        viewModel.updateDevices()
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
            response.reverse()
            myCropPremiumAdapter.setMovieList(response)

            if(myFarmPremiumAdapter==null)
                myFarmPremiumAdapter=MyFarmPremiumAdapter(this, this, requireContext())

            myFarmPremiumAdapter?.updateCropsList(response)

            if (it.data.isNullOrEmpty()) {
                binding.cvEditCrop.visibility = View.GONE
                binding.cardAddForm.visibility = View.VISIBLE
            } else {
                if (it.data?.size!! > 1) {
                    binding.videosScroll.visibility = View.VISIBLE
                } else {
                    binding.videosScroll.visibility = View.GONE

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
        binding.addFab.setOnClickListener {
            EventClickHandling.calculateClickEvent("AddFabBtnHomePremiumFragment")

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
                        binding.tvWelcomeName.text = ", ${it.data?.name.toString()}"

                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                Log.d("Profile", it1 + it2)
//                                Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    it.data?.roleId?.let { it1 -> checkRole(it1) }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
            }
        }
    }

    private fun initObserveMYFarm() {
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
                            if(myFarmPremiumAdapter==null)
                                myFarmPremiumAdapter=MyFarmPremiumAdapter(this, this, requireContext())
                            binding.rvMyFarm.adapter = myFarmPremiumAdapter
                            myCropPremiumAdapter.updateMyfarms(it.data)
                            binding.clAddForm.visibility = View.GONE
                            binding.cardMYFarm.visibility = View.VISIBLE
                            val sortedList = it.data?.sortedByDescending { farm ->
                                farm.isPrimary == 1
                            }
                            myFarmPremiumAdapter?.setMovieList(sortedList)
                            if (it.data?.size!! > 1) {
                                binding.videosScrollMyFarm.visibility = View.VISIBLE
                            } else
                                binding.videosScrollMyFarm.visibility = View.GONE

                        } else {
                            binding.clAddForm.visibility = View.VISIBLE
                            binding.cardMYFarm.visibility = View.GONE


                        }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
            }
        }

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            if (it.data != null) {
                var accountId = it.data?.accountId

            }
        }

        viewDevice.getIotDevice().observe(viewLifecycleOwner){
            if(!it.data.isNullOrEmpty()){
                myFarmPremiumAdapter?.updateDeviceList(it.data!!)}
        }
    }



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


    @SuppressLint("SetTextI18n")
    override fun viewDevice(data: ViewDeviceDomain) {
        selectedDevice = data
        viewModel.setSelectedDevice(data)
    }

    private fun populateDevice(data: ViewDeviceDomain) {
        binding.let {

            it.totalAreea.text = data.battery.toString()
            if (data.battery == null) {
                it.clBattery.visibility = View.GONE
            }
            it.tvAddDeviceStart.text = "${data.modelName} - ${data.deviceName}"
            it.deviceNumber.text = "Device Number : ${data.deviceNumber?.uppercase()}"
            it.tvTempDegree.text = "${(data.temperature?:"--")} \u2103"
            it.tvWindDegree.text = "${(data.rainfall?:"--")} mm"
            it.tvHumidityDegree.text = data.humidity.toString() + " %"
            it.tvWindSpeedDegree.text = data.windspeed.toString() + " Km/h"
            it.totalAreeaTwo.text=data.deviceElevation.toString() +" m"
            if (data.leafWetness != null && data.leafWetness!!.equals(1)) {
                it.tvLeafWetnessDegree.text = "Wet"
                it.ivLeafWetness.setImageResource(R.drawable.ic_leaf_wetness)
            } else {
                it.tvLeafWetnessDegree.text = "Dry"
                it.ivLeafWetness.setImageResource(R.drawable.ic_dry_image)
            }

            if (data.isApproved == 0) {
                it.approvedCV.visibility = View.VISIBLE
                it.tvTextAlert.text = "Your device is not approved. Contact us."
                it.cardTopParent.visibility = View.GONE
                it.cardSpeedMeter.visibility = View.GONE
                it.clSoilTemp.visibility = View.GONE
                it.clTempView.visibility = View.GONE
            } else {
                it.approvedCV.visibility = View.GONE
                it.cardTopParent.visibility = View.VISIBLE
                it.cardSpeedMeter.visibility = View.VISIBLE
                it.clSoilTemp.visibility = View.VISIBLE
                it.clTempView.visibility = View.VISIBLE

                if (data.modelSeries.equals("GSX", ignoreCase = true)) {
                    binding.cardTopParent.visibility = View.GONE
                    binding.clTempView.visibility = View.GONE
                } else {
                    binding.cardTopParent.visibility = View.VISIBLE
                    binding.clTempView.visibility = View.VISIBLE
                }

                if(data.temperature==null){
                    it.cardTopParent.visibility = View.GONE
                    it.cardSpeedMeter.visibility = View.GONE
                    it.clSoilTemp.visibility = View.GONE
                    it.clTempView.visibility = View.GONE
                }


            }
            it.tvPressureDegree.text = data.pressure.toString() + " hPa"
            it.ivSoilDegree.text = data.soilTemperature1.toString() + " \u2103"
            it.ivSoilDegreeOne.text = data.lux.toString() + " Lux"
            it.tvLastUpdate.text = DateFormatUtils.dateFormatterDevice(data.dataTimestamp)
            binding.soilMoistureOne.clearSections()
            binding.soilMoistureTwo.clearSections()
            binding.kpaOne.text = "${data.soilMoisture1} kPa"
            binding.kpaTwo.text = "${data.soilMoisture2} kPa"

            if (data.soilTemperature1.isNullOrEmpty()) {
                it.clSoilTemp.visibility = View.GONE
            }
            if (data.soilMoisture2 == null) {
                it.bottomTop.visibility = View.GONE
            }

            var colorSectionListSM1 = mutableListOf<Section>()

            binding.soilMoistureOne.addSections(
                Section(0f, .16f, Color.parseColor("#32A9FF"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.16f, .41f, Color.parseColor("#5FC047"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.41f, .75f, Color.parseColor("#FEC253"), binding.soilMoistureOne.dpTOpx(12f)),
                Section(.75f, 1f, Color.parseColor("#914734"), binding.soilMoistureOne.dpTOpx(12f))
            )
            //two
//            binding.soilMoistureOne .indicator.color = Color.RED
            binding.soilMoistureTwo.addSections(
                Section
                    (0f, .16f, Color.parseColor("#32A9FF"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.16f, .41f, Color.parseColor("#5FC047"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.41f, .75f, Color.parseColor("#FEC253"), binding.soilMoistureTwo.dpTOpx(12f)),
                Section(.75f, 1f, Color.parseColor("#914734"), binding.soilMoistureTwo.dpTOpx(12f))
            )
            binding.soilMoistureOne.tickNumber = 0
            binding.soilMoistureOne.marksNumber = 0
            binding.soilMoistureTwo.tickNumber = 0
            binding.soilMoistureTwo.marksNumber = 0
            binding.soilMoistureOne.maxSpeed = 60F
            binding.soilMoistureTwo.maxSpeed = 60F
            binding.soilMoistureOne.speedTo(data.soilMoisture1?.toFloat() ?: 0f)
            binding.soilMoistureTwo.speedTo(data.soilMoisture2?.toFloat() ?: 0f)


            binding.soilMoistureOne.speedTo(data.soilMoisture1?.toFloat() ?: 0f, 100)
            binding.soilMoistureTwo.speedTo(data.soilMoisture2?.toFloat() ?: 0f, 100)

            it.clTemp.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "temperature")
                    bundle.putString("toolbar", "Temperature")
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
                    bundle.putString("toolbar", "Rainfall")
                    bundle.putString("temp_value", data.rainfall)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )
                }
            }
            it.clHumidity.setOnClickListener {

                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "humidity")
                    bundle.putString("toolbar", "Humidity")
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
                    bundle.putString("toolbar", "Wind Speed")
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
                    bundle.putString("value", "leaf_wetness_hrs")
                    bundle.putString("toolbar", "Leaf wetness")

                    bundle.putString("temp_value", data.leafWetness.toString())
                    bundle.putString("date_time", data.dataTimestamp)
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
                    bundle.putString("toolbar", "Pressure")
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
                    bundle.putString("value", "soil_moisture_1_kpa")
                    bundle.putString("toolbar", "Soil Moisture Top")
                    bundle.putString("temp_value", data.soilMoisture1.toString())
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
                    bundle.putString("value", "soil_moisture_2_kpa")
                    bundle.putString("toolbar", "Soil Moisture Bottom")
                    bundle.putString("temp_value", data.soilMoisture2?.toString())
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_homePagePremiumFragment3_to_graphsFragment2,
                        bundle
                    )

                }
            }
            binding.clTempView.setOnClickListener {
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "lux")
                    bundle.putString("toolbar", "Light Intensity")
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
                    bundle.putString("toolbar", "Soil Temperature")
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

    override fun onFarmDetailsClicked(data: MyFarmsDomain) {
        val eventBundle = Bundle()
        eventBundle.putString("added_farm", data.farmName)
        EventItemClickHandling.calculateItemClickEvent("View_farm_details", eventBundle)
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
        data.cropLogo?.let { bundle.putString("cropLogo", it) }
        data.cropName?.let { bundle.putString("cropName", it) }
        data.cropId?.let { bundle.putInt("cropId", it) }
        this.findNavController()
            .navigate(R.id.action_homePagePremiumFragment3_to_navigation_irrigation, bundle)
    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter(activity ?: requireContext(), binding.bannerViewpager)
        runnable = Runnable {
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
                    AppUtil.handlerSet(handler, runnable, 3000)
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
        bannerAdapter.onItemClick={
            EventClickHandling.calculateClickEvent("Home_page_adbanner")
        }
    }

    override fun onFarmSelected(data: MyFarmsDomain) {
        val  eventBundle=Bundle()
        eventBundle.putString("SelectedFarmHomePremiumFrag",data.farmName)
        EventItemClickHandling.calculateItemClickEvent("View_farm_details",eventBundle)
        data.id?.let { initObserveDevice(it) }
    }


    private fun notification(){
        viewModel.getNotification().observe(viewLifecycleOwner){
            val data = it.data?.data?.filter { itt->
                itt.readAt== null
            }
            if(data?.size!=0){
                binding.IvNotification.setImageResource(com.example.soiltesting.R.drawable.ic_notification)
            }else{
                binding.IvNotification.setImageResource(R.drawable.ic_simple_notification)
            }
        }
    }
    private fun checkRole(roleId:Int){
        if(roleId==31){
            binding.tvEditMyCrops.visibility = View.GONE
            binding.tvEditMyCrops.visibility = View.GONE
            binding.clAddForm.visibility = View.GONE
            binding.ivViewAll.visibility = View.GONE
            binding.MyFarm.visibility = View.GONE
            binding.MyDevice.visibility=View.GONE
        }else{
            binding.tvEditMyCrops.visibility = View.VISIBLE
            binding.tvEditMyCrops.visibility = View.VISIBLE
            binding.ivViewAll.visibility = View.VISIBLE
            binding.MyFarm.visibility = View.VISIBLE
            binding.MyDevice.visibility=View.GONE
        }
    }
    override fun onPause() {
        super.onPause()
        if (runnable != null) {
            handler.removeCallbacks(runnable!!)
        }
    }
    override fun onResume() {
        super.onResume()

        if (runnable != null) {
            handler.postDelayed(runnable!!, 3000)
        }
        EventScreenTimeHandling.calculateScreenTime("HomePagePremiumFragment")
    }
}