package com.example.profile.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.fragment.RegistrationFragment
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.internal.format
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap


class EditProfileFragment : Fragment() {
    private var longitutde: String?=null
    private  var latitude: String?=null
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
        // viewModel.getUserProfile()
        //viewModel.getUsers()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        mLocationRequest = LocationRequest()
        //getLocation()
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
        viewModel.response2.observe(viewLifecycleOwner) {
            binding.tvName.setText(it.name)
        }
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

        if (selecteduri != null) {
            context?.let { ToastStateHandling.toastSuccess(it, "Image Uploaded", Toast.LENGTH_SHORT) }
        }
    }

//    private fun observer() {
////        viewModel.status.observe(viewLifecycleOwner) {
////           // Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
////            Log.d("profile", "observer: $it ")
////        }
//    }

    fun editProfile() {
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

        if (name.isNotEmpty() && address.isNotEmpty() && village.isNotEmpty() && pincode.isNotEmpty()
            && state.isNotEmpty() && city.isNotEmpty()
        ) {
                viewModel.getProfileRepository(field)
                    .observe(viewLifecycleOwner) {
                        when(it){
                            is Resource.Success->{
                                context?.let { it1 -> ToastStateHandling.toastSuccess(it1, "Profile Updated", Toast.LENGTH_SHORT) }
                                findNavController().navigateUp()
                            }
                            is Resource.Loading->{}
                            is Resource.Error->{
                                context?.let { it1 -> ToastStateHandling.toastSuccess(it1, "Error", Toast.LENGTH_SHORT) }
                            }
                        }
                        Log.d("ProfileUpdate", "editProfile: $it")

                    }

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

                Log.d("selecteduri", "editProfile: $selecteduri")

                    viewModel.getUserProfilePic(profileImageBody).observe(viewLifecycleOwner) {
                        Log.d("selecteduri", "editProfile: ${it.data?.profile_pic}")

                }
            }



        } else {
            context?.let { ToastStateHandling.toastError(it, "Please Fill All Fields", Toast.LENGTH_SHORT) }
        }
    }

    private fun onClick() {
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.addImage.setOnClickListener {
            mGetContent.launch("image/*")

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            if (intent.resolveActivity(requireActivity().packageManager) != null) {
//                startActivityForResult(intent, requestImageId)
//                Log.d("PROFILE", "onClick: $requestImageId")
//           }
        }
        binding.imgAutoText.setOnClickListener() {

            getLocation()
//            val pla: List<Place.Field> =
//                Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS)
//            val i: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, pla)
//                .setCountry("In")
//                .setTypeFilter(TypeFilter.ESTABLISHMENT)
//                .build(context)
//            startActivityForResult(i, 101)
        }
//        binding.tvCity.setOnClickListener() {
//            val pla: List<Place.Field> =
//                Arrays.asList(Place.Field.ADDRESS, Place.Field.ID, Place.Field.ADDRESS_COMPONENTS)
//            val i: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, pla)
//                .setCountry("In")
//                //.setTypeFilter(TypeFilter.CITIES)
//                .setTypeFilter(TypeFilter.CITIES)
//                .build(context)
//            startActivityForResult(i, 102)
//
//        }


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

//        if (requestCode == 101 && resultCode == RESULT_OK) {
//            val place: Place = Autocomplete.getPlaceFromIntent(data)
//            val value = place.address
//            val lstValues: List<String> = value.split(",").map { it -> it.trim() }
//            val size = lstValues.size - 2
//            binding.tvAddress1.setText("${place.name},${lstValues[0]},${lstValues[1]}")
//            binding.tvAddress2.setText(lstValues[2])
//            val strState = lstValues[size].replace("[0-9]".toRegex(), "");
//            binding.tvState.setText(strState)
//
//            binding.tvCity.setText(lstValues[size - 1])
//            val str = lstValues[size].replace("[^\\d.]".toRegex(), "");
//            binding.tvPincode.setText(str)
//        } else if (requestCode == 102 && resultCode == RESULT_OK) {
//            val place: Place = Autocomplete.getPlaceFromIntent(data)
//            val values = place.address
//
//            val lstValues: List<String> = values.split(",").map { it -> it.trim() }
//            binding.tvCity.setText(lstValues[0])
//            binding.tvState.setText(lstValues[1])
//
//        } else
            if (resultCode == AppCompatActivity.RESULT_OK && requestCode == requestImageId)
            {
            // Toast.makeText(context, "$requestCode", Toast.LENGTH_SHORT).show()
//            val selectedImage: Uri? = data?.data// handle chosen image
//            val pic = File(requireContext().cacheDir, "pic")
            // pic.mkdirs()
//            pic.createNewFile()
//            val options: UCrop.Options = UCrop.Options()
//            options.setCompressionQuality(100)
//            options.setMaxBitmapSize(10000)
//
//            selecteduri = selectedImage


//            if (selectedImage != null)
//                UCrop.of(selectedImage, Uri.fromFile(pic))
//                    .withAspectRatio(1F, 1F)
//                    .withMaxResultSize(1000, 1000)
//                    .withOptions(options)
//                    .start(requireActivity())
            Log.d("ProfilePicImage", "editProfile: $resultCode")
            Log.d("ProfilePicImage", "editProfile: $requestCode")
//            val file = selectedImage?.toFile()
//            val profileImage: RequestBody = RequestBody.create(
//                "image/jpg".toMediaTypeOrNull(),
//                file!!
//            )
//
//            val profileImageBody: MultipartBody.Part =
//                MultipartBody.Part.createFormData(
//                    "image",
//                    file.name, profileImage
//                )
//            viewModel.viewModelScope.launch {
//                selecteduri?.let { viewModel.getUserProfilePic(profileImageBody) }
//            }

            //Toast.makeText(context, "Image Uploaded", Toast.LENGTH_LONG).show()


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
}
