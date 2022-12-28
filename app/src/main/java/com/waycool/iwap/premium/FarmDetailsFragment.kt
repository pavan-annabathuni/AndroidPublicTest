package com.waycool.iwap.premium

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ekn.gruzer.gaugelibrary.Range
import androidx.navigation.fragment.findNavController
import com.example.addcrop.AddCropActivity
import com.example.adddevice.AddDeviceActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.example.ndvi.MainActivityNdvi
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.chip.Chip
import com.google.maps.android.SphericalUtil
import com.waycool.data.Local.utils.TypeConverter
import com.github.anastr.speedviewlib.components.Section
import com.github.anastr.speedviewlib.components.indicators.Indicator
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.TokenViewModel
import com.waycool.iwap.databinding.FragmentFarmDetails2Binding
import java.util.*
import kotlin.collections.ArrayList


class FarmDetailsFragment : Fragment(), ViewDeviceFlexListener, OnMapReadyCallback {
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
        viewDevice.farmDetailsDelta().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.data!!.isNotEmpty()) {
                        deltaAdapter.setMovieList(it.data?.data)
//                        binding.soilMoistureOne.progress=60
//                        Log.d(TAG, "initiFarmDeltT: ${it.data!!.data}")
//                        deltaAdapter.notifyDataSetChanged()
                        deltaTomAdapter.setMovieList(it.data?.data)
//                        deltaAdapter.update(getSprayingItems(sprayingTime.getToday()))
//                        Log.d(TAG, "initiFarmDeltT: ${it.data!!.data[0].Today} ")

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
                        val response = it.data!!.data as ArrayList<ViewDeviceData>
                        binding.deviceFarm.adapter = viewDeviceListAdapter
                        viewDeviceListAdapter.setMovieList(response)

                        binding.currentDelta.clearSections()
//                        binding.kpaOne.text=response[0]. .soilMoisture1+" kPa"
//                        binding.currentDelta.setIndicator(Indicator.Indicators.KiteIndicator)
                        binding.currentDelta.maxSpeed = 15F
                        binding.currentDelta.tickNumber = 0
                        binding.currentDelta.marksNumber = 0
                        binding.currentDelta.speedTo(response[0].delta_t!!.toFloat())
                        binding.deltaText.text = it.data?.data!![0].delta_t.toString()

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

    private fun initMyObserve() {
        tokenCheckViewModel.getDasBoard().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.subscription?.iot == true) {
                        binding.farmdetailsPremiumCl.visibility = View.VISIBLE
                        binding.cardMYDevice.visibility = View.VISIBLE
                        binding.freeAddDeviceCv.visibility = View.GONE
                        initObserveDevice()
                        initiFarmDeltT()
                    } else {
                        binding.farmdetailsPremiumCl.visibility = View.GONE
                        binding.cardMYDevice.visibility = View.GONE
                        binding.freeAddDeviceCv.visibility = View.VISIBLE
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
        binding.tvMyCrops.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.MyDevice.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("farm", myFarm)
            findNavController().navigate(R.id.action_farmDetailsFragment4_to_navigation_adddevice, bundle)
        }
        binding.tvNdviBanner.setOnClickListener {
            val intent = Intent(activity, MainActivityNdvi::class.java)
            startActivity(intent)
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
            findNavController().navigate(R.id.action_farmDetailsFragment4_to_navigation_adddevice, bundle)
        }
    }

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
            it.tvPressureDegree.text = data.pressure.toString() + " hPa"


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
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
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
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
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
                    bundle.putString("toolbar","Humidity")
                    bundle.putString("temp_value", data.humidity)
                    bundle.putString("date_time", data.dataTimestamp)
                    findNavController().navigate(
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
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
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
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
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
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
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
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
                        R.id.action_farmDetailsFragment2_to_graphsFragment,
                        bundle
                    )

                }
            }
        }



    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
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

    fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
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