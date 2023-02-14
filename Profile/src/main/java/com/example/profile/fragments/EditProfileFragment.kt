package com.example.profile.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.profile.databinding.FragmentEditProfileBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*


class EditProfileFragment : Fragment() {
    private var selecteduri: Uri? = null
    val requestImageId = 1
    lateinit var field: java.util.HashMap<String, String>
    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
    }
    private lateinit var title:String
    private lateinit var submit:String
    private lateinit var lat:String
    lateinit var mLocationRequest: LocationRequest
    private lateinit var long:String

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

    private var fusedLocationProviderClient: FusedLocationProviderClient?=null
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
        binding = FragmentEditProfileBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        mLocationRequest = LocationRequest()
        onClick()
        observerName()
        translation()
        binding.submit.setOnClickListener {
            editProfile()
        }

        viewModel.viewModelScope.launch {
            title = TranslationsManager().getString("str_farmer_profile")
            binding.topAppBar.title = title
            submit = TranslationsManager().getString("str_submit")
            binding.submit.text = submit

        }
        return binding.root
    }

    private fun observerName() {

        viewModel.viewModelScope.launch {
            viewModel.getUserProfileDetails().observe(viewLifecycleOwner) {
                binding.tvName.setText(it.data?.name)
                binding.tvPhoneNo.setText("+91 ${it.data?.phone}")
                binding.tvAddress1.setText(it.data?.profile?.address)
                binding.tvAddress2.setText(it.data?.profile?.village)
                binding.tvPincode.setText(it.data?.profile?.pincode)
                binding.tvState.setText(it.data?.profile?.state)
                binding.tvCity.setText(it.data?.profile?.district)

            }
        }
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            if (it.data?.profile?.remotePhotoUrl != null && selecteduri == null) {
                Glide.with(this).load(it.data?.profile?.remotePhotoUrl).into(binding.imageView)
            }
            lat = String.format("%.5f",it.data?.profile?.lat?.toDouble())
            long = String.format("%.5f",it.data?.profile?.long?.toDouble())

        }

//        if (selecteduri != null) {
//            viewModel.viewModelScope.launch {
//            val toast = TranslationsManager().getString("profile_updated")
//                if(!toast.isNullOrEmpty())
//            context?.let { ToastStateHandling.toastSuccess(it,toast, Toast.LENGTH_SHORT) }
//                else context?.let { ToastStateHandling.toastSuccess(it,"Profile Updated", Toast.LENGTH_SHORT) }
//        }}
    }

    private fun editProfile() {
        field = HashMap()
        val name: String = binding.tvName.text.toString()
        val address: String = binding.tvAddress1.text.toString()
        val village = binding.tvAddress2.text.toString()
        val pincode = binding.tvPincode.text.toString()
        val state = binding.tvState.text.toString()
        val city = binding.tvCity.text.toString()
        field.put("name",name)
        field.put("address",address)
        field.put("village",village)
        field.put("pincode",pincode)
        field.put("state",state)
        field.put("district",city)
        field.put("latitude",lat)
        field.put("longitude",long)

        /* Checking all fields are not empty */
        if (name.isNotEmpty() && address.isNotEmpty() && village.isNotEmpty() && pincode.isNotEmpty()
            && state.isNotEmpty() && city.isNotEmpty()
        ) {viewModel.viewModelScope.launch {
            val toast = TranslationsManager().getString("profile_updated")
        viewModel.getProfileRepository(field)
                    .observe(viewLifecycleOwner) {
                        when(it){

                            is Resource.Success->{
                                context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toast, Toast.LENGTH_SHORT) }
                                findNavController().navigateUp()
                            }
                            is Resource.Loading->{}
                            is Resource.Error->{
                             AppUtils.translatedToastServerErrorOccurred(context)                         }
                        }
                        Log.d("ProfileUpdate", "editProfile: $it")

                    }}

            if (selecteduri != null) {

                val fileDir = context?.filesDir
                val file: File = File(fileDir, ".png")
                val inputStream = context?.contentResolver?.openInputStream(selecteduri!!)
                val openInput = FileOutputStream(file)
                inputStream!!.copyTo(openInput)

                val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val profileImageBody: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "profile_pic",
                        file.name, requestFile
                    )
                    viewModel.getUserProfilePic(profileImageBody).observe(viewLifecycleOwner) {
                }
            }
        } else {
            viewModel.viewModelScope.launch {
            val toast = TranslationsManager().getString("str_fill_all_fields")
                if(toast.isNullOrEmpty())
            context?.let { ToastStateHandling.toastError(it, "Please Fill All Fields", Toast.LENGTH_SHORT) }
                else context?.let { ToastStateHandling.toastError(it,toast, Toast.LENGTH_SHORT) }
        }}
    }

    private fun onClick() {
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.addImage.setOnClickListener {
            mGetContent.launch("image/*")
        }
        binding.imgAutoText.setOnClickListener {

            getLocation()
        }
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
                                    requireActivity(), REQUEST_CODE_GPS
                                )
                            } catch (sendIntent: IntentSender.SendIntentException) {
                                sendIntent.printStackTrace()
                            }
                        }}
                }
            }
        } else {
            requestPermission()
        }
    }

    private fun getGeocodeFromLocation(it: Location) {
         lat = String.format(Locale.ENGLISH, "%.4f", it.latitude)
        long = String.format(Locale.ENGLISH, "%.4f", it.longitude)
        viewModel.getReverseGeocode("${it.latitude},${it.longitude}")
            .observe(viewLifecycleOwner) {
                if (it.results.isNotEmpty()) {
                    val result = it.results[0]
                    if (result.subLocality != null)
                        binding.tvAddress2.setText("${result.subLocality}")
                    else
                        binding.tvAddress2.setText("${result.locality}")
                    binding.tvState.setText("${result.state}")

                    binding.tvAddress1.setText("${result.formattedAddress ?: ""}")
                    binding.tvAddress1.setSelection(0)
                    binding.tvCity.setText("${result.district}")

                    binding.tvPincode.setText(result.pincode ?: "")
                    Log.d("locationCheckScucces", "location: ${result.formattedAddress}")
                }
            }
    }

    /*checking location is on or not */
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /*Checking location permission */
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

    //using upCrop to upload image and crop image//
    private var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri.let {
            selecteduri = it
            binding.imageView.setImageURI(it)
            val pic = File(requireActivity().externalCacheDir, "pest.jpg")

            val options: UCrop.Options = UCrop.Options()
            options.setCompressionQuality(100)
            options.setMaxBitmapSize(10000)

            if (selecteduri != null)
                UCrop.of(selecteduri!!, Uri.fromFile(pic))
                    .withAspectRatio(1F, 1F)
                    .withMaxResultSize(1000, 1000)
                    .withOptions(options)
                    .start(requireContext(), this)
            Log.d("ProfilePicImage2", "editProfile: $selecteduri")

        }

    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == AppCompatActivity.RESULT_OK && requestCode == requestImageId)
            {

        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val uri: Uri? = data?.let { UCrop.getOutput(it) }
            binding.imageView.setImageURI(uri)
            selecteduri = uri
            Log.d("ProfilePicImage2", "editProfile: $selecteduri")
        }
    }

    private fun translation(){
        TranslationsManager().loadString("str_farmer_name",binding.textView3,"Farmer Name ")
        TranslationsManager().loadString("str_mobile_number",binding.textView4,"Mobile Number")
        TranslationsManager().loadString("str_addressline_1",binding.textView5,"Address Line 1")
        TranslationsManager().loadString("str_city",binding.textView6,"City")
        TranslationsManager().loadString("str_state",binding.textView8,"State")
        TranslationsManager().loadString("str_pincode",binding.textView9,"Pincode")
        TranslationsManager().loadString("str_district",binding.textView7,"District")

    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val REQUEST_CODE_GPS = 1011
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("EditProfileFragment")
    }
}
