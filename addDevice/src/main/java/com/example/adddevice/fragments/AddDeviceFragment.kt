package com.example.adddevice.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.adddevice.R
import com.example.adddevice.databinding.FragmentAddDeviceBinding
import com.example.adddevice.viewmodel.AddDeviceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.waycool.data.utils.Resource
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class AddDeviceFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentAddDeviceBinding? = null
    private val binding get() = _binding!!
    var nickName: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //    private lateinit var binding: FragmentAddDeviceBinding
    private lateinit var googleMap: GoogleMap
    private var tileOverlayTransparent: TileOverlay? = null
    var spinner1 = arrayOf("Outgrow GWX")
    var spinner2 = arrayOf("GWX 007", "GWX 008")
    var contactNumber: String = ""
    private val viewModel by lazy { ViewModelProvider(this)[AddDeviceViewModel::class.java] }
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
//        binding = FragmentAddDeviceBinding.inflate(inflater)
        _binding = FragmentAddDeviceBinding.inflate(inflater, container, false)

//        spinners()
        binding.topAppBar.setNavigationOnClickListener() {
            this.findNavController().navigateUp()
        }
        scanner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.Map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
        isLocationPermissionGranted()

//        viewModel.getUserDetails().observe(viewLifecycleOwner) {
////                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
////                    account=it.data.account
//            contactNumber= it.data?.phone.toString()
////            binding.mobileNo.text="+91 $contactNumber"
//            accountID=it.data?.accountId
//
//        }
        binding.submit.setOnClickListener {
//            activityDevice(11,"867542059649031")
        }
    }

    private fun activityDevice(map: MutableMap<String, Any> = mutableMapOf<String, Any>()) {
        //867542059649031
        viewModel.activateDevice(map).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
//                    val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
//                    var editor = sharedPreference.edit()
//                    editor.putString("username","Anupam")
//                    editor.putLong("l",100L)
//                    editor.commit()
                    activity?.finish()
//                    findNavController().navigateUp()
                    Toast.makeText(requireContext(), "Device is Created", Toast.LENGTH_SHORT).show()

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

//    override fun onMapReady(map: GoogleMap?) {
//
//
//    }

    //    private fun spinners(){
//        val arrayAdapter = ArrayAdapter<String>(requireContext(),R.layout.item_spinner,spinner1)
//        binding.spinner1.adapter =arrayAdapter
//        val arrayAdapter2 = ArrayAdapter<String>(requireContext(),R.layout.item_spinner,spinner2)
//        binding.spinner3.adapter =arrayAdapter2
//    }
    private fun scanner() {
        binding.btScanner.setOnClickListener() {
            val intentIntegrator = IntentIntegrator.forSupportFragment(this)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setPrompt("Scan")
//            intentIntegrator.setCameraId(0);
//            intentIntegrator.setBarcodeImageEnabled(false);
//            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setOrientationLocked(false)
//            intentIntegrator.captureActivity
//            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()
        }

    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        Log.d("scanner", "onActivityResult: $intentResult ")
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                Toast.makeText(context, "Scanned ${intentResult.contents}", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "onActivityResultGSCg: ${intentResult.contents} ")
//                messageText.setText(intentResult.contents)
//                messageFormat.setText(intentResult.formatName)
                viewModel.getUserDetails().observe(viewLifecycleOwner) {
                    var accountId = it.data?.accountId
                            Log.d(TAG, "onViewCreatedjbdvjb: $accountId")
                            if (accountId != null) {
                                var serial_no = intentResult.contents
                                Log.d(TAG, "onActivityResultjgdsvfdhjs: $serial_no")
                                val map = mutableMapOf<String, Any>()
                                map.put("account_no", accountId)
                                map.put("device_name", binding.device1.text)
                                map.put("farm_id",25)
                                map.put("device_lat",12.930220)
                                map.put("device_long",77.686267)
                                Log.d(TAG, "onActivityResultLatitude: $binding.latitude.text")
                                map.put("device_number", serial_no)
                                map.put("device_version", "praaf")
                                binding.submit.setOnClickListener {
                                    nickName = binding.device1.text.toString().trim()
                                    if (nickName.isEmpty()) {
                                        binding.device1.error = "Device Name should not be empty"
                                        return@setOnClickListener
                                    } else if (nickName.isNotEmpty() && serial_no.isNotEmpty()) {
                                        activityDevice(map)
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "please scan the Device QR",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    Toast.makeText(
                                        context,
                                        "Scanned ${intentResult.contents}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    Log.d(
                                        "TAG",
                                        "onActivityResultDataFromLL: ${intentResult.contents}"
                                    )
//                messageText.setText(intentResult.contents)
//                messageFormat.setText(intentResult.formatName)
                                }
                            }



                }

            }
        } else {
            // the content and format of scan message
            Toast.makeText(context, "Scanned", Toast.LENGTH_SHORT).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            Log.d("checkLocation", "isLocationPermissionGranted:1 ")
            false
        } else {
            Log.d("checkLocation", "isLocationPermissionGranted:2 ")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // use your location object
                        // get latitude , longitude and other info from this
                        binding.latitude.text=location.latitude.toString()
                        binding.longitude.text=location.longitude.toString()
                        Log.d("checkLocation", "isLocationPermissionGranted: $location")
//                        getAddress(location.latitude, location.longitude)
                        Log.d(TAG, "isLocationPermissionGrantedLotudetude: ${location.latitude}")
                        Log.d(TAG, "isLocationPermissionGrantedLotudetude: ${location.longitude}")
//                        binding.

//                        checkSoilTestViewModel.getSoilTest(1, location.latitude, location.longitude)
//                        bindObserversCheckSoilTest()

                        val latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)

                    }
                }
            true
        }
    }

    override fun onMapReady(map: GoogleMap) {
        map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        map.let {
            googleMap = it!!

            var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                    /* Define the URL pattern for the tile images */
                    val url = "http://my.image.server/images/$zoom/$x/$y.png"
                    return if (!checkTileExists(x, y, zoom)) {
                        null
                    } else try {
                        URL(url)
                    } catch (e: MalformedURLException) {
                        throw AssertionError(e)
                    }
                }

                /*
                 * Check that the tile server supports the requested x, y and zoom.
                 * Complete this stub according to the tile range you support.
                 * If you support a limited range of tiles at different zoom levels, then you
                 * need to define the supported x, y range at each zoom level.
                 */
                private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                    val minZoom = 12
                    val maxZoom = 16
                    return zoom in minZoom..maxZoom
                }

            }

            tileOverlayTransparent = googleMap.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider)
            )
        }

    }
}