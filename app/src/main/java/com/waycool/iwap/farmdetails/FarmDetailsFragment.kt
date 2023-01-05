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
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.chip.Chip
import com.google.maps.android.SphericalUtil
import com.github.anastr.speedviewlib.components.Section
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.TokenViewModel
import com.waycool.iwap.databinding.FragmentFarmDetails2Binding
import com.waycool.iwap.premium.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class FarmDetailsFragment : Fragment(), ViewDeviceFlexListener, OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var _binding: FragmentFarmDetails2Binding? = null
    private val binding get() = _binding!!

    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val tokenCheckViewModel by lazy { ViewModelProvider(this)[TokenViewModel::class.java] }

    private lateinit var myCropAdapter: MyCropsAdapter

    //    private var myFarmPremiumAdapter = MyFarmPremiumAdapter(this)
    var viewDeviceListAdapter = ViewDeviceListAdapter(this)
    private var myFarm: MyFarmsDomain? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@FarmDetailsFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFarmDetails2Binding.inflate(inflater, container, false)
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
        myCrop()
        farmDetailsObserve()
        translationSoilTesting()


        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
//            soilTestingLabsAdapter.upDateList()
        }

//        val progressbar: ProgressBar = findViewById(R.id.progressbar) as ProgressBar
//        val color = -0xff0100
//        progressbar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        progressbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)


    }
    fun translationSoilTesting() {
        CoroutineScope(Dispatchers.Main).launch {
//            val title = TranslationsManager().getString("add_crop")
//            binding.toolbarTextFarm.text = title
//            var NickNamehint = TranslationsManager().getString("e_g_crop_name")
//            binding.etNickName.hint =NickNamehint
//            var areaHint = TranslationsManager().getString("e_g_50")
//            binding.etAreaNumber.hint =areaHint
//            var hitnPlant = TranslationsManager().getString("e_g_50")
//            binding.etNoOfAcre.hint =hitnPlant
        }
        TranslationsManager().loadString("farm", binding.tvMyform)
        TranslationsManager().loadString("add_crop_info", binding.tvYourForm)
        TranslationsManager().loadString("add_crop", binding.tvAddFrom)
        TranslationsManager().loadString("str_edit", binding.editFarmFarmsSingle)
        TranslationsManager().loadString("str_edit", binding.editFarmFarmsSingle)
        TranslationsManager().loadString("farm_details", binding.tvMyformDetails)
        TranslationsManager().loadString("water_source", binding.tvWaterSource)
        TranslationsManager().loadString("pump_hp", binding.tvPump)
        TranslationsManager().loadString("pump_type", binding.tvShowingDateTotalFoem)
        TranslationsManager().loadString("pump_size", binding.tvPempSize)
        TranslationsManager().loadString("pipe_height", binding.tvPempHeight)
        TranslationsManager().loadString("pump_flow", binding.tvPumpFlowRate)
        TranslationsManager().loadString("submersible", binding.totalFormDate)
        TranslationsManager().loadString("str_mycrops", binding.myCropsTitle)
        TranslationsManager().loadString("add_crops", binding.tvMyCrops)
        TranslationsManager().loadString("", binding.titleMyDevice)



    }

    private fun farmDetailsObserve() {
        binding.toolbarTextFarm.text = myFarm?.farmName
        binding.tvPempDate.text = myFarm?.farmPumpHp
        binding.totalFormDate.text = myFarm?.farmPumpType
        binding.totalHeightInches.text = myFarm?.farmPumpPipeSize
        binding.tvPumpFlowRateNUmber.text = myFarm?.farmPumpFlowRate
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

    private fun initiFarmDeltT() {
        var deltaAdapter = DeltaAdapter(requireContext())
        var deltaTomAdapter = DeltaTomAdapter(requireContext())
        binding.sparayingRv.adapter = deltaAdapter
        binding.sparayingRv2.adapter = deltaTomAdapter
        if (myFarm?.id != null) {
            viewDevice.farmDetailsDelta(myFarm?.id!!).observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {

                            deltaAdapter.setMovieList(it.data?.data?.Today)
//                        binding.soilMoistureOne.progress=60
//                        Log.d(TAG, "initiFarmDeltT: ${it.data!!.data}")
//                        deltaAdapter.notifyDataSetChanged()
                            deltaTomAdapter.setMovieList(it.data?.data?.Tomorrow)
//                        deltaAdapter.update(getSprayingItems(sprayingTime.getToday()))
//                        Log.d(TAG, "initiFarmDeltT: ${it.data!!.data[0].Today} ")



                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(requireContext(), "Error", Toast.LENGTH_SHORT)
                    }
                    is Resource.Loading -> {
                        ToastStateHandling.toastWarning(
                            requireContext(),
                            "Loading",
                            Toast.LENGTH_SHORT
                        )

                    }
                }
            }
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
                binding.cardAddForm.visibility = View.VISIBLE
            }
//                        if (it.data?.size!! < 8) {
//                            binding.addLl.visibility = View.VISIBLE
//                        } else binding.addLl.visibility = View.GONE
        }
    }

    private fun initObserveDevice() {
        viewDevice.getIotDevice().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.data != null) {
                        val response = it.data!!.data.filter { it.farmId == myFarm?.id }

                        binding.deviceFarm.adapter = viewDeviceListAdapter
                        viewDeviceListAdapter.setMovieList(response as ArrayList<ViewDeviceData>)

                        if (response.isNullOrEmpty()) {
                            binding.farmdetailsPremiumCl.visibility = View.GONE
                            binding.cardMYDevice.visibility = View.GONE
                            binding.freeAddDeviceCv.visibility = View.VISIBLE
                        }
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
                        initiFarmDeltT()
                    } else {
                        binding.farmdetailsPremiumCl.visibility = View.GONE
                        binding.cardMYDevice.visibility = View.GONE
                        binding.freeAddDeviceCv.visibility = View.VISIBLE
                        binding.ndviCl.visibility = View.GONE

                    }
                }
                is Resource.Loading -> {


                }
                is Resource.Error -> {
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

        binding.callDevice.setOnClickListener() {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.messageDevice.setOnClickListener() {
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
            startActivity(intent)
        }

        binding.editFarmFarmsSingle.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            bundle.putBoolean("isedit", true)
//            findNavController().navigate(R.id.action_farmDetailsFragment4_to_nav_add_farm, bundle)

            val intent=Intent(requireActivity(),AddFarmActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun viewDevice(data: ViewDeviceData) {

        setupDeltaT(data)
        if (data.model?.series == "GSX") {
            binding.cardTopParent.visibility = View.GONE
            binding.clTempView.visibility = View.GONE
        } else {
            binding.cardTopParent.visibility = View.VISIBLE
            binding.clTempView.visibility = View.VISIBLE
        }
        binding.let {

            it.totalAreea.text = data.iotDevicesData?.battery.toString()
            it.tvAddDeviceStart.text = data.model?.modelName.toString()
            it.tvTempDegree.text = data.temperature.toString() + " \u2103"
            it.tvWindDegree.text = data.rainfall.toString() + " mm"
            it.tvHumidityDegree.text = data.humidity.toString() + " %"
            it.tvWindSpeedDegree.text = data.windspeed.toString() + " Km/h"
            if (data.leafWetness != null && data.leafWetness!!.equals(1)) {
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
            binding.kpaOne.text = "${data.soilMoisture1} kPa"
            binding.kpaTwo.text = "${data.soilMoisture2} kPa"

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
            binding.soilMoistureOne.maxSpeed = 60F
            binding.soilMoistureTwo.maxSpeed = 60F
//            binding.soilMoistureOne.ticks  = listOf(10.0F)
//            binding.soilMoistureTwo.ticks = 10
//            binding.soilMoistureOne.speedTo(data.soilMoisture1!!.toFloat())
//            binding.soilMoistureTwo.speedTo(data.soilMoisture2!!.toFloat())


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
               initObserveDevice()
            }
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
                    bundle.putString("temp_value", data.rainfall)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
//                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
                    bundle.putString("toolbar", "Humidity")
                    bundle.putString("temp_value", data.humidity)
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
                    bundle.putString("temp_value", data.windspeed)
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
                    bundle.putString("value", "leaf_wetness")
                    bundle.putString("toolbar", "Leaf wetness")

                    bundle.putString("temp_value", data.leafWetness)
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
                    bundle.putString("temp_value", data.pressure)
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
                    bundle.putString("value", "soil_moisture_1")
                    bundle.putString("toolbar", "Soil Moisture Top")
                    bundle.putString("temp_value", data.soilMoisture1.toString())
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
                    bundle.putString("value", "soil_moisture_2")
                    bundle.putString("toolbar", "Soil Moisture Bottom")
                    bundle.putString("temp_value", data.soilMoisture2?.toString())
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
                    bundle.putString("temp_value", data.lux)
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
                    bundle.putString("temp_value", data.soilTemperature1)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
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
//                        R.id.action_farmDetailsFragment4_to_graphsFragment3,
//                        bundle
//                    )
//
//                }
//
//            }
        }


    }

    private fun setupDeltaT(data: ViewDeviceData) {

        if (data.model?.series == "GSX") {
            binding.currentDelta.visibility = View.GONE
            binding.deltaText.visibility = View.GONE
            binding.updateDate.visibility = View.GONE
        } else {
            binding.currentDelta.visibility = View.VISIBLE
            binding.deltaText.visibility = View.VISIBLE
            binding.updateDate.visibility = View.VISIBLE
            binding.updateDate.text="Last Updated: ${data.dataTimestamp}"
        }

        binding.currentDelta.clearSections()
        binding.currentDelta.maxSpeed = 15F
        binding.currentDelta.tickNumber = 0
        binding.currentDelta.marksNumber = 0
        data.deltaT?.toFloat()
            ?.let { it1 -> binding.currentDelta.speedTo(it1) }
        binding.deltaText.text = data.deltaT.toString()

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
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            mMap = map
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            map!!.uiSettings.setAllGesturesEnabled(false)
            map!!.uiSettings.isMapToolbarEnabled = false
            if (myFarm != null) {
                val points = myFarm?.farmJson
                if (points != null) {
                    if (points.size >= 3) {
                        map.addPolygon(
                            PolygonOptions().addAll(points).fillColor(Color.argb(100, 58, 146, 17))
                                .strokeColor(
                                    Color.argb(255, 255, 255, 255)
                                )
                        )
                    }
                    for (latLng in points) {
                        val marker = map.addMarker(
                            MarkerOptions().position(
                                latLng
                            )
                                .icon(BitmapDescriptorFactory.fromResource(com.waycool.addfarm.R.drawable.circle_green))
                                .anchor(0.5f, .5f)
                                .draggable(false)
                                .flat(true)
                        )
                    }
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            getLatLnBounds(points), 20
                        )
                    )
                    val area: Double =
                        getArea(points) / 4046.86
                    binding.farmAreaSingleFarm.setText(
                        (String.format(
                            Locale.ENGLISH,
                            "%.2f",
                            area
                        )).trim { it <= ' ' } + " Acre"
                    )
                }
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

}