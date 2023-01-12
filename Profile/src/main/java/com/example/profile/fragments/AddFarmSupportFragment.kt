package com.example.profile.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.profile.R
import com.example.profile.databinding.FragmentAddFarmSupportBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.fragment.RegistrationFragment
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern


class AddFarmSupportFragment : Fragment() {
    private lateinit var binding: FragmentAddFarmSupportBinding

    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
    }
    var lat = 12.22
    var long = 78.22
    var pinCode = 1
    var village = ""
    var address = ""
    var state = ""
    var district = ""
    var accountId: Int? = null
    private lateinit var submit: String

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .build()

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            getLocation()
            locationResult.lastLocation?.let {
                removeLocationCallback()
                getGeocodeFromLocation(it)
            }
        }
    }

    private fun removeLocationCallback() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        if (allAreGranted) {
            getLocation()
        }
    }


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
        binding = FragmentAddFarmSupportBinding.inflate(inflater)
        onClick()
        getLocation()
        binding.imgLocation.setOnClickListener() {
            getLocation()
        }
        binding.farmManger.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.text_border)
        translation()
        return binding.root

    }

    private fun onClick() {
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountId = it.data?.accountId
        }
        var roleid = 30
        binding.topAppBar.setNavigationOnClickListener() {
            this.findNavController().navigateUp()
        }
        binding.farmManger.setOnClickListener() {
            binding.farmManger.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.text_border_gray)
            binding.mandiBench.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.text_border)
            binding.image1.visibility = View.VISIBLE
            binding.image2.visibility = View.GONE
            roleid = 30
        }
        binding.mandiBench.setOnClickListener() {
            binding.mandiBench.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.text_border_gray)
            binding.farmManger.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.text_border)
            binding.image2.visibility = View.VISIBLE
            binding.image1.visibility = View.GONE
            roleid = 31
        }
        binding.submit.setOnClickListener() {
            var contact: Long? = null
            val name = binding.tvName.text.toString()

            val lat2 = binding.tvLat.text
            var long2 = binding.tvLong.text
            if (!binding.mobilenoEt.text.isNullOrEmpty()) {
                contact = binding.mobilenoEt.text.toString().toLong()
            }

            if (name.isNullOrEmpty() || lat2.isNullOrEmpty() || long2.isNullOrEmpty()) {
                context?.let { it1 ->
                    ToastStateHandling.toastError(
                        it1,
                        "Fill all Fields",
                        Toast.LENGTH_SHORT
                    )
                }
            } else if (binding.mobilenoEt.text.toString()
                    .isEmpty() || binding.mobilenoEt.text.toString().length != 10
            ) {
                binding.mobileNo.error = "Enter Valid Mobile Number"
            }else if(checkForValidMobileNumber(binding.mobilenoEt.toString())){
                binding.mobileNo.error = "Enter Valid Mobile Number"
            } else {
                binding.mobileNo.isErrorEnabled = false
                viewModel.updateFarmSupport(
                    accountId!!,
                    name, contact!!, lat, long, roleid, pinCode,
                    village, address, state, district
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            findNavController().navigateUp()
                        }
                        is Resource.Error -> {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1,
                                    "Enter Valid Mobile Number",
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                        is Resource.Loading -> {}
                    }
                }
                // findNavController().navigateUp()
            }
        }
    }

    private fun checkForValidMobileNumber(mobileno:String):Boolean{
        val pattern = Pattern.compile("^[6-9]\\d{9}\$")
        val matcher = pattern.matcher(mobileno)
        return matcher.find()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
                fusedLocationProviderClient?.lastLocation!!
                    .addOnSuccessListener {
                        if (it != null) {
                            getGeocodeFromLocation(it)

                        }
                    }
                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } else {

                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)

                val locationResponseTask: Task<LocationSettingsResponse> =
                    LocationServices.getSettingsClient(requireContext().applicationContext)
                        .checkLocationSettings(builder.build())
                locationResponseTask.addOnCompleteListener {
                    try {
                        val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
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

//                context?.let {
//                    ToastStateHandling.toastError(
//                        it,
//                        "Please turn on location",
//                        Toast.LENGTH_LONG
//                    )
//                }
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun getGeocodeFromLocation(it: Location) {
        lat = String.format("%.6f", it.latitude).toDouble()
        long = String.format("%.6f", it.longitude).toDouble()
        binding.tvLat.setText(lat.toString())
        binding.tvLong.setText(long.toString())

        viewModel.getReverseGeocode("${it.latitude},${it.longitude}")
            .observe(viewLifecycleOwner) {
                if (it.results.isNotEmpty()) {
                    val result = it.results[0]
                    address = result.formattedAddress.toString()
                    village = result.subLocality.toString()
                    pinCode = result.pincode.toString().toInt()
                    state = result.state.toString()
                    district = result.district.toString()
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

    private fun translation() {
        TranslationsManager().loadString("str_farmer_name", binding.textView1,"Farmer Name ")
        TranslationsManager().loadString("str_role_type", binding.textView3,"Role Type")
        TranslationsManager().loadString("str_farm_location", binding.textView5,"Farm Location Coordinates")
        TranslationsManager().loadString("str_mobile_number", binding.textView4,"Mobile Number")
        TranslationsManager().loadString("str_farmer", binding.tvFarmer,"Farm Manger")
        TranslationsManager().loadString("str_farmer_support", binding.tvFarmerSupport,"Farmer Support")
//        TranslationsManager().loadString("delete_farm_support",areYouSure)

        viewModel.viewModelScope.launch {
            var title = ""
            submit = TranslationsManager().getString("str_submit")
            binding.submit.text = submit
//            title = TranslationsManager().getString("str_submit")
//            binding.topAppBar.title = title
            binding.tvName.hint = TranslationsManager().getString("str_farmer_name")
            binding.mobilenoEt.hint = TranslationsManager().getString("str_mobile_number")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    companion object {
        private const val REQUEST_CODE_GPS = 1011
    }
}