package com.example.adddevice.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.adddevice.R
import com.example.adddevice.adapter.SelectCropAdapter
import com.example.adddevice.databinding.FragmentAddDeviceBinding
import com.example.adddevice.viewmodel.AddDeviceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.zxing.integration.android.IntentIntegrator
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.Resource
import java.util.*


class AddDeviceFragment : Fragment(), OnMapReadyCallback {
    private var isQRScanned: Boolean = false
    private var longitutde: String? = null
    private var latitude: String? = null
    private var currentMarker: Marker? = null
    private var _binding: FragmentAddDeviceBinding? = null
    private val binding get() = _binding!!
    var nickName: String = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var myFarm: MyFarmsDomain? = null
    private var mMap: GoogleMap? = null

    private var scanResult: String? = null
    private var plotId: Int? = null

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->


        Log.d("permission", "test" + result)
        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        if (allAreGranted) {
            getLocation()
        }
    }

    private val viewModel by lazy { ViewModelProvider(this)[AddDeviceViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            myFarm = arguments?.getParcelable<MyFarmsDomain>("farm")
        else
            myFarm = requireActivity().intent?.extras?.getParcelable("farm")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDeviceBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener() {
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

        binding.submit.setOnClickListener {
            nickName = binding.device1.text.toString().trim()
            if (nickName.isEmpty()) {
                binding.device1.error = "Device Name should not be empty"
                return@setOnClickListener
            } else if (scanResult.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "please scan the Device QR",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                val map = mutableMapOf<String, Any>()
                map["device_name"] = binding.device1.text
                map["farm_id"] = myFarm?.id!!
                if (latitude != null)
                    map.put("device_lat", latitude!!)
                if (longitutde != null)
                    map.put("device_long", longitutde!!)
                map["device_number"] = scanResult!!
                map["is_device_qr"] = if (isQRScanned) 1 else 0
                activityDevice(map)
            }
        }

        binding.btScanner.setOnClickListener() {
            val intentIntegrator = IntentIntegrator.forSupportFragment(this)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setPrompt("Scan")
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()
        }
    }

    private fun activityDevice(map: MutableMap<String, Any>) {
        viewModel.activateDevice(map).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    findNavController().navigateUp()
                    Toast.makeText(
                        requireContext(),
                        "Device added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Error -> {

                    Toast.makeText(
                        requireContext(),
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        ContentValues.TAG,
                        "postAddCropExption: ${it.message.toString()}"
                    )
                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        scanResult = intentResult?.contents

        if (scanResult != null) {
            isQRScanned = true
            binding.tvScanned.text = "QR Scanned."
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

                mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
                mFusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            latitude = String.format(Locale.ENGLISH, "%.5f", location.latitude)
                            longitutde = String.format(Locale.ENGLISH, "%.5f", location.longitude)
                            myLocationMarker(LatLng(location.latitude, location.longitude))
                            binding.latitude.text = "${location.latitude} , ${location.longitude}"
                            updateBounds(LatLng(location.latitude, location.longitude))
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnCanceledListener {
                        Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()

                    }
            } else {
                Toast.makeText(context, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
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
        Log.d("registerresponse2", "test" + requestCode)
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
                    for (latLng in points) {
                        val marker = map.addMarker(
                            MarkerOptions().position(
                                latLng
                            )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green))
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
            if (mMap != null)
                currentMarker = mMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .flat(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location))
                        .draggable(false)
                )
        }
    }
}