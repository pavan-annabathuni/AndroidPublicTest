package com.waycool.iwap.farmdetails

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.AddCropActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.github.anastr.speedviewlib.components.Section
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.chip.Chip
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.maps.android.SphericalUtil
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Network.NetworkModels.DeltaT
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.TokenViewModel
import com.waycool.iwap.databinding.FragmentFarmDetails2Binding
import com.waycool.iwap.premium.*
import com.waycool.uicomponents.utils.DateFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FarmDetailsFragment : Fragment(), ViewDeviceFlexListener, OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var _binding: FragmentFarmDetails2Binding? = null
    private val binding get() = _binding!!
    private var isPremium: Int? = null

    private val viewDevice by lazy { ViewModelProvider(this)[ViewDeviceViewModel::class.java] }
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }

    private var lastUpdatedTime: String? = null

    private lateinit var myCropAdapter: MyCropsAdapter

    //    private var myFarmPremiumAdapter = MyFarmPremiumAdapter(this)
    var viewDeviceListAdapter = ViewDeviceListAdapter(this)
    private var myFarm: MyFarmsDomain? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFarmDetails2Binding.inflate(inflater, container, false)


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) activity?.let { it.finish() }
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }

        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) activity?.let { it1 -> it1.finish() }
        }
        viewModel.getMyFarms().observe(viewLifecycleOwner) {
            val farm = it.data?.firstOrNull { it1 -> myFarm?.id == it1.id }
            myFarm = farm
            farmDetailsObserve()
            drawFarm()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myFarm = arguments?.getParcelable<MyFarmsDomain>("farm")

        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_farmdetails) as SupportMapFragment
        mapFragment.requireView().isClickable = false
        mapFragment.getMapAsync(this)

        binding.backBtn.setOnClickListener { findNavController().navigateUp() }

        initViewClick()
        initMyObserve()
//        farmDetailsObserve()
        translations()
        checkRole()
        myCrop()


        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) activity?.onBackPressed()
//            soilTestingLabsAdapter.upDateList()
        }

//        val progressbar: ProgressBar = findViewById(R.id.progressbar) as ProgressBar
//        val color = -0xff0100
//        progressbar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        progressbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)


        setupDeltaTGuage()
        setUpSoilMoisture()
    }

    private fun setupDeltaTGuage() {
        binding.currentDelta.clearSections()
        binding.currentDelta.addSections(
            Section
                (
                0f,
                .24f,
                Color.parseColor("#DA0101"),
                binding.currentDelta.dpTOpx(12f)
            ),
            Section(
                .24f,
                .59f,
                Color.parseColor("#01B833"),
                binding.currentDelta.dpTOpx(12f)
            ),
            Section(
                .59f,
                .71f,
                Color.parseColor("#F3C461"),
                binding.currentDelta.dpTOpx(12f)
            ),
            Section(
                .71f,
                1f,
                Color.parseColor("#DA0101"),
                binding.currentDelta.dpTOpx(12f)
            )
        )
        binding.currentDelta.maxSpeed = 15F
        binding.currentDelta.tickNumber = 0
        binding.currentDelta.marksNumber = 0
    }

    private fun setUpSoilMoisture() {

        binding.soilMoistureOne.clearSections()
        binding.soilMoistureOne.addSections(
            Section(0f, .16f, Color.parseColor("#32A9FF"), binding.soilMoistureOne.dpTOpx(12f)),
            Section(.16f, .41f, Color.parseColor("#5FC047"), binding.soilMoistureOne.dpTOpx(12f)),
            Section(.41f, .75f, Color.parseColor("#FEC253"), binding.soilMoistureOne.dpTOpx(12f)),
            Section(.75f, 1f, Color.parseColor("#914734"), binding.soilMoistureOne.dpTOpx(12f))
        )
        //two
        binding.soilMoistureTwo.clearSections()
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
    }


    fun translations() {
        CoroutineScope(Dispatchers.Main).launch {

        }
        TranslationsManager().loadString("farm", binding.tvMyform, "Farm")
        TranslationsManager().loadString(
            "add_crop_info",
            binding.tvYourForm,
            "Add your Crop and get more details."
        )
        TranslationsManager().loadString("add_crop", binding.tvAddFrom, "Add crops")
        TranslationsManager().loadString("str_edit", binding.editFarmFarmsSingle, "Edit")
        TranslationsManager().loadString("farm_details", binding.tvMyformDetails, "Farm Details")
        TranslationsManager().loadString("water_source", binding.tvWaterSource, "Water Sources")
        TranslationsManager().loadString("pump_hp", binding.tvPump, "Pump HP")
        TranslationsManager().loadString("add_crop", binding.tvEditMyCrops, "Add Crop")
        TranslationsManager().loadString("pump_type", binding.tvShowingDateTotalFoem, "Pump Type")
        TranslationsManager().loadString("pump_size", binding.tvPempSize, "Pump Size (in Inches)")
        TranslationsManager().loadString(
            "pipe_height",
            binding.tvPempHeight,
            "Pump Height (in Mtrs)"
        )
        TranslationsManager().loadString(
            "pump_flow",
            binding.tvPumpFlowRate,
            "Pump Flow Rate (in Ltre per hr)"
        )
//        TranslationsManager().loadString("submersible", binding.totalFormDate, "Submersible")
        TranslationsManager().loadString("str_mycrops", binding.myCropsTitle, "My Crops")
        TranslationsManager().loadString("my_device", binding.titleMyDevice, "My Devices")
        TranslationsManager().loadString("view_tepm", binding.tvTemp, "Temperature")
        TranslationsManager().loadString("view_rainfall", binding.tvWind, "Wind")
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
        TranslationsManager().loadString("deltat", binding.deltat, "Spraying Condition (Delta T)")
        TranslationsManager().loadString("tvNDVi", binding.tvNDVi, "Farm Health Monitoring (NDVI)")
        TranslationsManager().loadString(
            "get_sattilate_image",
            binding.title3Bold,
            "Get Satellite imagery for your crops"
        )
        TranslationsManager().loadString("txt_know_more", binding.ndviButton, "Know more")
        TranslationsManager().loadString("str_today", binding.textView159, "Today")
        TranslationsManager().loadString("tomorrow", binding.textView164, "Tomorrow")
        TranslationsManager().loadString("how_it_work", binding.deltaTInfo, "How does this work?")
        TranslationsManager().loadString("str_add_device", binding.MyDevice, "Add Device")
        //to translations
        TranslationsManager().loadString("call", binding.callDevice, "Call")
        TranslationsManager().loadString("chat", binding.messageDevice, "Chat")
        TranslationsManager().loadString(
            "have_device",
            binding.addDeviceFree,
            "Already have our device? Click here to Add Device"
        )
        TranslationsManager().loadString(
            "get_recommendation",
            binding.textView253,
            "Get Advanced disease and irrigation recommendations. To know more\""
        )
        TranslationsManager().loadString(
            "gwx_txt",
            binding.gwxText,
            "GWX-100 Smart Weather Station"
        )


    }

    private fun farmDetailsObserve() {
        binding.toolbarTextFarm.text = myFarm?.farmName
        binding.tvPempDate.text = myFarm?.farmPumpHp ?: "NA"
        binding.totalFormDate.text = myFarm?.farmPumpType ?: "NA"
        binding.totalHeightInches.text = myFarm?.farmPumpDepth ?: "NA"
        binding.totalPempInches.text = myFarm?.farmPumpPipeSize ?: "NA"
        binding.tvPumpFlowRateNUmber.text = myFarm?.farmPumpFlowRate ?: "NA"
        if (myFarm?.farmWaterSource != null) {
            binding.waterNotAvailable.visibility = View.INVISIBLE
            binding.waterChipGroup.visibility = View.VISIBLE

            binding.waterChipGroup.removeAllViews()
            for (category in myFarm?.farmWaterSource!!)
                createChip(category)
        } else {
            binding.waterNotAvailable.visibility = View.VISIBLE
            binding.waterNotAvailable.text = "NA"
            binding.waterChipGroup.visibility = View.INVISIBLE
        }
    }

    private fun initFarmDeltaT() {
        var deltaAdapter = DeltaAdapter(requireContext())
        var deltaTomAdapter = DeltaTomAdapter(requireContext())
        binding.sparayingRv.adapter = deltaAdapter
        binding.sparayingRv2.adapter = deltaTomAdapter
        if (myFarm?.id != null) {
            viewDevice.farmDetailsDelta(myFarm?.id!!).observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.data?.Today.isNullOrEmpty()) {
                            binding.textView159.visibility = View.GONE
                            binding.sparayingRv.visibility = View.GONE
                            binding.lineTwo.visibility = View.GONE

                        } else {

                            deltaAdapter.setMovieList(getSprayingItemsToday(it.data?.data?.Today))
                        }
                        if (it.data?.data?.Tomorrow.isNullOrEmpty()) {
                            binding.textView164.visibility = View.GONE
                            binding.sparayingRv2.visibility = View.GONE
                        } else {
                            deltaTomAdapter.setMovieList(getSprayingItems(it.data?.data?.Tomorrow))
                        }
                    }
                    is Resource.Error -> {
                        AppUtils.translatedToastServerErrorOccurred(context)

                    }
                    is Resource.Loading -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val toastLoading = TranslationsManager().getString("loading")
                            if (!toastLoading.isNullOrEmpty()) {
                                context?.let { it1 ->
                                    ToastStateHandling.toastError(
                                        it1, toastLoading,
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            } else {
                                context?.let { it1 ->
                                    ToastStateHandling.toastError(
                                        it1, "Loading",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    //    private String getdeltaTLastUpdated(List<Spraying2Days.Day> today) {
    //        Calendar calendar = Calendar.getInstance();
    //        return today.get(calendar.get(Calendar.HOUR_OF_DAY)).getTime();
    //    }
    private fun getSprayingItemsToday(today: MutableList<DeltaT>?): List<DeltaT> {
        if (today.isNullOrEmpty())
            return emptyList()
        val iterator = today.iterator()
        while (iterator.hasNext()) {
            val day = iterator.next()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH", Locale.ENGLISH)
            try {
                val date = dateFormat.parse(day.datetime)
                val calendar = Calendar.getInstance()
                val calendar1 = Calendar.getInstance()
                calendar1.time = date
                if (calendar1[Calendar.HOUR_OF_DAY] <= calendar[Calendar.HOUR_OF_DAY]) {
                    iterator.remove()
                } else if (calendar1[Calendar.HOUR_OF_DAY] >= 20) {
                    iterator.remove()
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
        return today
    }

    private fun getSprayingItems(tomorrow: MutableList<DeltaT>?): List<DeltaT> {
        if (tomorrow.isNullOrEmpty())
            return mutableListOf()
        if (tomorrow.size < 20)
            return tomorrow
        return tomorrow.subList(4, 20)
    }

    private fun myCrop() {
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
//            val intent = Intent(activity, IrrigationPlannerActivity::class.java)
//            startActivity(intent)
        })
        binding.rvMyCrops.adapter = myCropAdapter
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            if (isPremium == 0) {
                if (it.data?.size!! >= 8) {
                    binding.tvEditMyCrops.visibility = View.GONE
                    binding.cardAddForm.visibility = View.GONE
                }
            }

            Log.d("MyCrops", "myCrop: ${it.data}")
            val cropList = it.data?.filter { plot -> plot.farmId == myFarm?.id }
            myCropAdapter.submitList(cropList)
            if (!(cropList.isNullOrEmpty())) {
                binding.tvCount.text = cropList.size.toString()
            } else {
                binding.tvCount.text = "0"
            }
            if (!cropList.isNullOrEmpty()) {
                binding.cvEditCrop.visibility = View.VISIBLE
                binding.cardAddForm.visibility = View.GONE
            } else {
                binding.cvEditCrop.visibility = View.GONE
                // binding.cardAddForm.visibility = View.VISIBLE
            }
//                        if (it.data?.size!! < 8) {
//                            binding.tvEditMyCrops.visibility = View.VISIBLE
//                        } else binding.tvEditMyCrops.visibility = View.GONE
        }
    }

    private fun initObserveDevice() {
        binding.ndviCl.visibility = View.GONE
        binding.farmdetailsPremiumCl.visibility = View.GONE
        binding.cardMYDevice.visibility = View.GONE
//        binding.freeAddDeviceCv.visibility = View.VISIBLE

        viewDevice.getIotDeviceByFarm(myFarm?.id!!).observe(viewLifecycleOwner) {
            checkForDeviceApiUpdate()
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        binding.deviceFarm.adapter = viewDeviceListAdapter
                        viewDeviceListAdapter.setMovieList(it.data as ArrayList<ViewDeviceDomain>)
                        val dataList = it.data as ArrayList<ViewDeviceDomain>

                        val approvedList = dataList.filter { data -> data.isApproved == 1 }
                        if (!approvedList.isNullOrEmpty()) {
                            binding.ndviCl.visibility = View.VISIBLE
                            binding.detailId.visibility = View.VISIBLE
                            binding.viewOne.visibility = View.VISIBLE
                        } else {
                            binding.ndviCl.visibility = View.GONE
                            binding.detailId.visibility = View.GONE
                            binding.viewOne.visibility = View.GONE
                        }
                        if (it.data.isNullOrEmpty()) {
                            binding.ndviCl.visibility = View.GONE
                            binding.farmdetailsPremiumCl.visibility = View.GONE
                            binding.cardMYDevice.visibility = View.GONE
                            binding.freeAddDeviceCv.visibility = View.VISIBLE
                        } else {
                            binding.ndviCl.visibility = View.VISIBLE
                            binding.farmdetailsPremiumCl.visibility = View.VISIBLE
                            binding.cardMYDevice.visibility = View.VISIBLE
                            binding.freeAddDeviceCv.visibility = View.GONE
                        }
                    }

                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
                is Resource.Loading -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val toastLoading = TranslationsManager().getString("alert_valid_number")
                        if (!toastLoading.isNullOrEmpty()) {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1, toastLoading,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        } else {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1, "Enter Valid Mobile Number",
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }

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


    private fun initMyObserve() {
        tokenCheckViewModel.getDasBoard().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.subscription?.iot == true) {
                        binding.farmdetailsPremiumCl.visibility = View.VISIBLE
                        binding.cardMYDevice.visibility = View.VISIBLE
                        binding.freeAddDeviceCv.visibility = View.GONE
                        binding.ndviCl.visibility = View.VISIBLE
                        initObserveDevice()
                        initFarmDeltaT()
                    } else {
                        binding.farmdetailsPremiumCl.visibility = View.GONE
                        binding.cardMYDevice.visibility = View.GONE
                        binding.ndviCl.visibility = View.GONE
                        binding.freeAddDeviceCv.visibility = View.VISIBLE
                        binding.ndviCl.visibility = View.GONE

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

    private fun createChip(waterSource: String) {
        val chip = Chip(requireContext())
        chip.text = waterSource
        chip.isEnabled = false
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
            com.waycool.uicomponents.R.color.bg_chip_text
        )

        binding.waterChipGroup.addView(chip)
    }


    private fun initViewClick() {
        binding.tvEditMyCrops.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("farmID", myFarm?.id.toString().toInt())
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.addCropCl.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("farmID", myFarm?.id.toString().toInt())
            intent.putExtras(bundle)
            startActivity(intent)
        }


        binding.MyDevice.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            findNavController().navigate(
                R.id.action_farmDetailsFragment4_to_navigation_adddevice,
                bundle
            )
        }
        binding.ivViewAll.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            findNavController().navigate(
                R.id.action_farmDetailsFragment4_to_navigation_adddevice,
                bundle
            )
        }
        binding.ndviCl.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            findNavController().navigate(R.id.action_farmDetailsFragment4_to_navigation, bundle)
        }
        binding.ndviButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            findNavController().navigate(R.id.action_farmDetailsFragment4_to_navigation, bundle)
        }


        binding.callDevice.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.messageDevice.setOnClickListener {
            FeatureChat.zenDeskInit(requireContext())
        }
        binding.addDeviceFree.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            findNavController().navigate(
                R.id.action_farmDetailsFragment4_to_navigation_adddevice,
                bundle
            )
        }
        binding.deltaTInfo.setOnClickListener {
            findNavController().navigate(R.id.action_farmDetailsFragment4_to_deltaTInfoBottomDialogFragment)
        }
        binding.cardAddForm.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("farmID", myFarm?.id.toString().toInt())
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.editFarmFarmsSingle.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            bundle.putBoolean("isedit", true)
//            findNavController().navigate(R.id.action_farmDetailsFragment4_to_nav_add_farm, bundle)

            val intent = Intent(activity, AddFarmActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.tvLastUpdateRefresh.setOnClickListener {
            EventClickHandling.calculateClickEvent("Device_card_Refresh")
            updateDevice()

        }
        binding.ivUpdate.setOnClickListener {
            EventClickHandling.calculateClickEvent("Device_card_Refresh")
            updateDevice()
        }

    }

    override fun viewDevice(data: ViewDeviceDomain) {

        setupDeltaT(data)

        binding.let {

            it.totalAreea.text = data.battery.toString()
            if (data.battery == null) {
                it.clBattery.visibility = View.GONE
            }
            it.tvAddDeviceStart.text = "${data.modelName} - ${data.deviceName}"
            it.deviceNumber.text = "Device Number : ${data.deviceNumber?.uppercase()}"
            it.tvTempDegree.text = data.temperature.toString() + " \u2103"
            it.tvWindDegree.text = data.rainfall.toString() + " mm"
            it.tvHumidityDegree.text = data.humidity.toString() + " %"
            it.tvWindSpeedDegree.text = data.windspeed.toString() + " Km/h"
            it.totalAreeaTwo.text = data.deviceElevation.toString() + " m"
            if (data.leafWetness != null && data.leafWetness!! == 1) {
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
                binding.cardSpeedMeterDeltat.visibility = View.GONE

            } else {
                it.approvedCV.visibility = View.GONE
                it.cardTopParent.visibility = View.VISIBLE
                it.cardSpeedMeter.visibility = View.VISIBLE
                it.clSoilTemp.visibility = View.VISIBLE
                it.clTempView.visibility = View.VISIBLE
                binding.cardSpeedMeterDeltat.visibility = View.VISIBLE

                if (data.modelSeries.equals("GSX", ignoreCase = true)) {
                    binding.cardTopParent.visibility = View.GONE
                    binding.clTempView.visibility = View.GONE
                } else {
                    binding.cardTopParent.visibility = View.VISIBLE
                    binding.clTempView.visibility = View.VISIBLE
                }

                if (data.modelSeries.equals("GSX", ignoreCase = true)) {
                    if (data.soilMoisture1 == null) {
                        it.cardTopParent.visibility = View.GONE
                        it.cardSpeedMeter.visibility = View.GONE
                        it.clSoilTemp.visibility = View.GONE
                        it.clTempView.visibility = View.GONE
                    }
                } else {
                    if (data.temperature == null) {
                        it.cardTopParent.visibility = View.GONE
                        it.cardSpeedMeter.visibility = View.GONE
                        it.clSoilTemp.visibility = View.GONE
                        it.clTempView.visibility = View.GONE
                    }
                }
            }

            it.tvPressureDegree.text = data.pressure.toString() + " hPa"

            if (data.soilTemperature1==null) {
                it.clSoilTemp.visibility = View.GONE
            }
            if (data.soilMoisture2 == null || data.soilMoisture2 == 0.0) {
                it.bottomTop.visibility = View.GONE
            } else {
                it.bottomTop.visibility = View.VISIBLE
            }
            it.ivSoilDegree.text = data.soilTemperature1.toString() + " \u2103"
            it.ivSoilDegreeOne.text = data.lux.toString() + " Lux"
            it.tvLastUpdate.text =
                "Last Updated: ${if (data.dataTimestamp != null) DateFormatUtils.dateFormatterDevice(data.dataTimestamp) else "--"}"
//            binding.soilMoistureOne.clearSections()
//            binding.soilMoistureTwo.clearSections()
            binding.kpaOne.text = "${data.soilMoisture1} kPa"
            binding.kpaTwo.text = "${data.soilMoisture2} kPa"

            binding.soilMoistureOne.speedTo(data.soilMoisture1?.toFloat() ?: 0f, 100)
            binding.soilMoistureTwo.speedTo(data.soilMoisture2?.toFloat() ?: 0f, 100)

            it.clTemp.setOnClickListener {
                EventClickHandling.calculateClickEvent("Temparature_card_today")
                val bundle = Bundle()
                if (data.serialNoId != null && data.modelId != null) {
                    bundle.putInt("serial_no", data.serialNoId!!.toInt())
                    bundle.putInt("device_model_id", data.modelId!!.toInt())
                    bundle.putString("value", "temperature")
                    bundle.putString("toolbar", "Temperature")
                    data.temperature?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.rainfall?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.humidity?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.windspeed?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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

                    data.leafWetness?.toDouble()?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.pressure?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.soilMoisture1?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.soilMoisture2?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.lux?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    data.soilTemperature1?.let { it1 -> bundle.putDouble("temp_value", it1) }
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
                        bundle
                    )

                }

            }
        }


    }

    private fun updateDevice() {
        binding.ivUpdate.visibility = View.INVISIBLE
        binding.updateProgressDevice.visibility = View.VISIBLE
        viewModel.updateDevices()
    }

    private fun setupDeltaT(data: ViewDeviceDomain) {

        if (data.modelSeries == "GSX" || data.deltaT == null) {
            binding.currentDelta.visibility = View.GONE
            binding.deltaText.visibility = View.GONE
            binding.updateDate.visibility = View.GONE
        } else {
            binding.currentDelta.visibility = View.VISIBLE
            binding.deltaText.visibility = View.VISIBLE
            binding.updateDate.visibility = View.VISIBLE
            binding.updateDate.text =
                "Last Updated: ${if (data.dataTimestamp != null) DateFormatUtils.dateFormatterDevice(data.dataTimestamp) else "--"}"
        }

//        binding.currentDelta.clearSections()

        data.deltaT?.toFloat()
            ?.let { it1 -> binding.currentDelta.speedTo(it1) }
        binding.deltaText.text = data.deltaT.toString()

    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            mMap = map
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isMapToolbarEnabled = false
            drawFarm()

        }
    }

    private fun drawFarm() {
        if (myFarm != null) {
            val points = myFarm?.farmJson
            mMap?.clear()
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
                    val marker = mMap?.addMarker(
                        MarkerOptions().position(
                            latLng
                        )
                            .icon(BitmapDescriptorFactory.fromResource(com.waycool.addfarm.R.drawable.circle_green))
                            .anchor(0.5f, .5f)
                            .draggable(false)
                            .flat(true)
                    )
                }
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points), 20
                    )
                )
                val area: Double =
                    getArea(points) / 4046.86
                binding.farmAreaSingleFarm.text = (String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    area
                )).trim { it <= ' ' } + " Acre"
            }
        }
    }


    private fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
        val builder = LatLngBounds.builder()
        for (ll in points) {
            builder.include(ll)
        }
        return builder.build()
    }

    private fun getArea(latLngs: List<LatLng?>?): Double {
        return SphericalUtil.computeArea(latLngs)
    }

    private fun checkRole() {
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            isPremium = it.data?.subscription
            if (it.data?.roleId == 31) {
                binding.ClYourForm.visibility = View.GONE
                binding.tvEditMyCrops.visibility = View.GONE
                binding.editFarmFarmsSingle.visibility = View.GONE
                binding.MyDevice.visibility = View.GONE
                binding.ivViewAll.visibility = View.GONE
                binding.addDeviceFree.visibility = View.GONE
                binding.tvAddFrom.visibility = View.GONE
                binding.cardAddForm.visibility = View.GONE
            } else {
                binding.ClYourForm.visibility = View.VISIBLE
                //binding.tvEditMyCrops.visibility = View.VISIBLE
                binding.editFarmFarmsSingle.visibility = View.VISIBLE
                binding.MyDevice.visibility = View.VISIBLE
                binding.ivViewAll.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("FarmDetailsFragment")
    }
}