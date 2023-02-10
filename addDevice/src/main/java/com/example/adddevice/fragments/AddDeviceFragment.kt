package com.example.adddevice.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.adddevice.R
import com.example.adddevice.adapter.SelectCropAdapter
import com.example.adddevice.databinding.FragmentAddDeviceBinding
import com.example.adddevice.viewmodel.AddDeviceViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.zxing.integration.android.IntentIntegrator
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AddDeviceFragment : Fragment(), OnMapReadyCallback {
    private var isQRScanned: Boolean = false
    private var longitutde: String? = null
    private var latitude: String? = null
    private var currentMarker: Marker? = null
    private var _binding: FragmentAddDeviceBinding? = null
    private val binding get() = _binding!!
    private var nickName: String = ""
    private var myFarm: MyFarmsDomain? = null
    private var mMap: GoogleMap? = null

    private var scanResult: String? = null
    private var plotId: Int? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .build()

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            locationResult.lastLocation?.let {
                latitude = String.format(Locale.ENGLISH, "%.5f", it.latitude)
                longitutde = String.format(Locale.ENGLISH, "%.5f", it.longitude)
                myLocationMarker(LatLng(it.latitude, it.longitude))
                binding.latitude.text = "${it.latitude} , ${it.longitude}"
                updateBounds(LatLng(it.latitude, it.longitude))
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        if (checkPermissions()) {
            getLocation()
        }
    }

    private val viewModel by lazy { ViewModelProvider(this)[AddDeviceViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myFarm = if (arguments != null)
            arguments?.getParcelable("farm")
        else
            requireActivity().intent?.extras?.getParcelable("farm")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDeviceBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_adddevice) as SupportMapFragment
        mapFragment.requireView().isClickable = false
        mapFragment.getMapAsync(this)
        binding.tvScanned.isSelected = true

        binding.submit.setOnClickListener {
            nickName = binding.device1.text.toString().trim()
            if (longitutde == null || latitude == null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastCheckInternet = TranslationsManager().getString("error_current_location")
                    if(!toastCheckInternet.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastCheckInternet,
                            LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Error getting current Location",
                        LENGTH_SHORT
                    ) }}}

            } else if (checkDistanceBetweenLatLng(
                    myFarm?.farmCenter?.get(0),
                    latitude?.toDouble()?.let { it1 ->
                        longitutde?.toDouble()
                            ?.let { it2 -> LatLng(it1, it2) }
                    }
                ) > 500
            ) {

                CoroutineScope(Dispatchers.Main).launch {
                    val toastCheckInternet = TranslationsManager().getString("device_location")
                    if(!toastCheckInternet.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastCheckInternet,
                            LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Device Location is far from your Farm",
                        LENGTH_SHORT
                    ) }}}


            } else if (nickName.isEmpty()) {
                binding.device1.error = "Device Name should not be empty"
                return@setOnClickListener
            } else if (scanResult.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastCheckInternet = TranslationsManager().getString("please_scan")
                    if(!toastCheckInternet.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastCheckInternet,
                            LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please scan the Device QR",
                        LENGTH_SHORT
                    ) }}}


            } else {
                val  eventBundle=Bundle()
                eventBundle.putString("deviceName",binding.device1.text.toString())
                eventBundle.putString("scanQr",isQRScanned.toString())
                eventBundle.putString("deviceNumber",scanResult.toString())
                EventItemClickHandling.calculateItemClickEvent("add_device_register",eventBundle)
                val map = mutableMapOf<String, Any>()
                map["device_name"] = binding.device1.text
                map["farm_id"] = myFarm?.id!!
                if (latitude != null)
                    map["device_lat"] = latitude!!
                if (longitutde != null)
                    map["device_long"] = longitutde!!
                map["device_number"] = scanResult!!
                map["is_device_qr"] = if (isQRScanned) 1 else 0
                activityDevice(map)
            }

        }

        binding.btScanner.setOnClickListener {
            val intentIntegrator = IntentIntegrator.forSupportFragment(this)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setPrompt("Scan")
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()
        }
        translationAddDevice()
        binding.topAppBar.setOnClickListener{
            activity?.finish()
//            val isSuccess = findNavController().navigateUp()
//            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun checkDistanceBetweenLatLng(latLng1: LatLng?, latLng2: LatLng?): Float {
        if (latLng1 == null || latLng2 == null)
            return 1000f
        val distance = FloatArray(2)
        Location.distanceBetween(
            latLng1.latitude,
            latLng1.longitude,
            latLng2.latitude,
            latLng2.longitude,
            distance
        )
        Log.d("Add Device", "Distance Calculated: ${distance[0]}")
        return distance[0]
    }

    private fun activityDevice(map: MutableMap<String, Any>) {
        viewModel.activateDevice(map).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    findNavController().navigateUp()
                    CoroutineScope(Dispatchers.Main).launch {
                        val toastCheckInternet = TranslationsManager().getString("device_added")
                        if(!toastCheckInternet.isNullOrEmpty()){
                            context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toastCheckInternet,
                                LENGTH_SHORT
                            ) }}
                        else {context?.let { it1 -> ToastStateHandling.toastSuccess(it1,"Device added successfully",
                            LENGTH_SHORT
                        ) }}}

                }
                is Resource.Error -> {

                    CoroutineScope(Dispatchers.Main).launch {
                        val toastServerError = TranslationsManager().getString("server_error")
                        if(!toastServerError.isNullOrEmpty()){
                            context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                                Toast.LENGTH_SHORT
                            ) }}
                        else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Server Error Occurred",
                            Toast.LENGTH_SHORT
                        ) }}}
                }
                is Resource.Loading -> {
                    viewModel.viewModelScope.launch {
                        val toastLoading = TranslationsManager().getString("alert_valid_number")
                        if(!toastLoading.isNullOrEmpty()){
                            context?.let { it1 -> ToastStateHandling.toastError(it1,toastLoading,
                                Toast.LENGTH_SHORT
                            ) }}

                }
            }
        }
    }}


    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        scanResult = intentResult?.contents

        if (scanResult != null) {
            isQRScanned = true
//            binding.tvScanned.text = "QR Scanned."
            TranslationsManager().loadString("str_scanned_device", binding.tvScanned,"QR Scanned.")

            binding.tvScanned.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_tick
                ), null, null, null
            )
            binding.tvScanned.setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    com.waycool.uicomponents.R.color.white
                )
            )
            binding.btScanner.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                com.waycool.uicomponents.R.color.primaryColor
            )
            binding.imeiAddress.setText(scanResult)

            viewModel.verifyQR(scanResult!!, 1).observe(viewLifecycleOwner) {
                if (it.data?.data == "GSX") {
                    val adapter = SelectCropAdapter()
                    binding.mycropsRv.adapter = adapter
                    binding.plotlCl.visibility = View.VISIBLE
                    viewModel.getMyCrop2().observe(viewLifecycleOwner) { crops ->
                        val cropList = crops?.data?.filter { plot -> plot.farmId == myFarm?.id }
                        if (cropList.isNullOrEmpty()) {
                            binding.plotlCl.visibility = View.GONE
                        } else
                            adapter.submitList(cropList)
                    }
                    adapter.onItemClick = { plot ->
                        plotId = plot?.id
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)

                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )

                fusedLocationProviderClient?.lastLocation!!
                    .addOnSuccessListener {
                        if (it != null) {
                            latitude = String.format(Locale.ENGLISH, "%.5f", it.latitude)
                            longitutde = String.format(Locale.ENGLISH, "%.5f", it.longitude)
                            myLocationMarker(LatLng(it.latitude, it.longitude))
                            binding.latitude.text = "${it.latitude} , ${it.longitude}"
                            updateBounds(LatLng(it.latitude, it.longitude))
                        }
                    }


            } else {

                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)

                val locationResponseTask: Task<LocationSettingsResponse> =
                    LocationServices.getSettingsClient(requireContext().applicationContext)
                        .checkLocationSettings(builder.build())
                locationResponseTask.addOnCompleteListener {
                    try {
                        val response: LocationSettingsResponse =
                            it.getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            val apiException: ResolvableApiException = e as ResolvableApiException
                            try {
                                apiException.startResolutionForResult(
                                    requireActivity(),
                                    REQUEST_CODE_GPS
                                )
                            } catch (sendIntent: IntentSender.SendIntentException) {
                                sendIntent.printStackTrace()
                            }
                        }
                    }

                }
            }
        } else {
            requestPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    companion object {
        private const val REQUEST_CODE_GPS = 1011
    }

    private fun updateBounds(latLng: LatLng) {
        if (myFarm != null) {
            val points = myFarm?.farmJson?.toMutableList()
            points?.add(latLng)
            if (!points.isNullOrEmpty()) {
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points), 10
                    )
                )
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

//        requestPermissions(
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            permissionId
//        )
        //requestPermissions(String[] {android.Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("registerresponse2", "test $requestCode")
        if (requestCode == 2) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            mMap = map
            getLocation()

            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isMapToolbarEnabled = false
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
//                    for (latLng in points) {
//                        val marker = map.addMarker(
//                            MarkerOptions().position(
//                                latLng
//                            )
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green))
//                                .anchor(0.5f, .5f)
//                                .draggable(false)
//                                .flat(true)
//                        )
//                    }
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            getLatLnBounds(points), 20
                        )
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

    private fun myLocationMarker(latLng: LatLng) {
        if (currentMarker != null) {
            currentMarker?.setPosition(latLng)
        } else {
            if (mMap != null) {
                val circleDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_weather_device_small)
                val markerIcon = circleDrawable?.let { getMarkerIconFromDrawable(it) }

                currentMarker = mMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .flat(false)
                        .icon(markerIcon)
                        .draggable(false)
                )
            }
        }
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    fun translationAddDevice() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("str_add_device")
            binding.topAppBar.title = title
            var areaHint = TranslationsManager().getString("e_g_50")
            binding.imeiAddress.hint =areaHint

            var zone = TranslationsManager().getString("device_hint")
            binding.device1.hint =zone
        }
        TranslationsManager().loadString("str_device_name", binding.textView,"Device Name")
        TranslationsManager().loadString("str_device_details", binding.textView2,"Device Details")
        TranslationsManager().loadString("str_scan", binding.tvScanned,"Sacn QR code")
        TranslationsManager().loadString("device_serial_number", binding.textView3,"Device Serial Number")
        TranslationsManager().loadString("str_register", binding.submit,"Register")
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AddDeviceFragment")
    }
}