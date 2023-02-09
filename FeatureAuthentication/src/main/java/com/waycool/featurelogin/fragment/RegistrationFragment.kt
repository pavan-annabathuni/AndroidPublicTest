package com.waycool.featurelogin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.waycool.core.utils.AppSecrets
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.KnowYourPremiumServicesAdapter
import com.waycool.featurelogin.adapter.KnowYourServicesAdapter
import com.waycool.featurelogin.databinding.FragmentRegistrationBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.databinding.ToolbarLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.changer.audiowife.AudioWife
import java.util.*


class RegistrationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude: String = ""
    var longitutde: String = ""
    var address: String? = null
    var village: String? = null
    var state:String? = null
    var district:String? = null
    var pincode:String? = null
    lateinit var knowAdapter: KnowYourServicesAdapter
    lateinit var premiumAdapter: KnowYourPremiumServicesAdapter
    var mobileNumber: String? = ""
    lateinit var mContext: Context
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    lateinit var query: HashMap<String, String>
    lateinit var audioWife: AudioWife
    var mediaPlayer: MediaPlayer? = null

    lateinit var placesClient: PlacesClient

    private val blockCharacterSet = "@~#^|$%&*!-<>+$*()[]{}/,';:?"
    private var audioUrl: String? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    //The Priority of the location request.
    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .build()

    //A callback for receiving notifications from the FusedLocationProviderClient.
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //Called when a new LocationResult is available.
            super.onLocationResult(locationResult)
            //get location of user
            getLocation()
            locationResult.lastLocation?.let {
                //Removes all location updates for the given listener
                removeLocationCallback()
                //get details such as district, sub locality and locality
                getGeocodeFromLocation(it)
            }
        }
    }

    private fun removeLocationCallback() {
        //Removes all location updates for the given callback.
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    // The InputFilter Interface has one method, filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) , and it provides you with all the information you need to know about which characters were entered into the EditText it is assigned to
    private val filter: InputFilter =
        InputFilter { source, start, end, dest, dstart, dend ->
            //The InputFilter implementation takes input in form of CharSequence and checks if it contains any characters present in the blockCharacterSet. If it does, it returns an empty string, otherwise it returns null.
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }


    /*registerForActivityResult() takes an ActivityResultContract and an ActivityResultCallback and returns an ActivityResultLauncher which you'll use to launch the other activity. */
    /*RequestMultiplePermissions-requesting multiple permission at a time.*/
    //registerForActivityResult method creates an instance of ActivityResultLauncher with RequestMultiplePermissions contract
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        /*The code checks if all the requested permissions were granted by iterating over the values of the map and setting a flag allAreGranted to false if any of the permissions were not granted. */
        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        //If all permissions were granted, the code calls the getLocation() method.
        if (allAreGranted) {
            getLocation()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        //Network Error state layout binding
        apiErrorHandlingBinding = binding.errorState
        TranslationsManager().loadString("txt_internet_problem",apiErrorHandlingBinding.tvInternetProblem,"There is a problem with Internet.")
        TranslationsManager().loadString("txt_check_net",apiErrorHandlingBinding.tvCheckInternetConnection,"Please check your Internet connection")
        TranslationsManager().loadString("txt_tryagain",apiErrorHandlingBinding.tvTryAgainInternet,"TRY AGAIN")
        //method to set translations
        setTranslations()
        binding.registerDoneBtn.isEnabled = true

        Places.initialize(requireActivity().applicationContext, AppSecrets.getMapsKey())
        placesClient = Places.createClient(requireContext())

        //make network call to check availability of internet
        networkCall()
        //check availability of internet on click of "TRY AGAIN" button
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        //declaring and initializing toolbar
        val toolbarLayoutBinding: ToolbarLayoutBinding = binding.toolbar

        //setting translations for text heading of toolbar
        CoroutineScope(Dispatchers.Main).launch {
            val profile = TranslationsManager().getString("profile")
            if (!profile.isNullOrEmpty()) {
                toolbarLayoutBinding.toolbarTile.text = profile
            } else {
                toolbarLayoutBinding.toolbarTile.text = "Profile"

            }
        }
        //click on back button present on toolbar
        toolbarLayoutBinding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack(R.id.loginFragment, false)
        }

        //getting these arguments from login fragment
        //if argument "mobile_number" is not null then assign the values name and mobile number to the respective variables
        if (arguments?.getString("mobile_number") != null) {
            mobileNumber = arguments?.getString("mobile_number")
            binding.nameEt.setText(arguments?.getString("name"))
        }


        //Filtering all blockCharacterSet("@~#^|$%&*!-<>+$*()[]{}/,';:?") that are being entered in the Name EditText
        binding.nameEt.filters = arrayOf(filter)

        // FusedLocationProviderClient is part of the Google Play Services Location API and it provides access to the device's location or receive updates about changes to the device's location.
       // getFusedLocationProviderClient method is a static method of the LocationServices class and it returns an instance of FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        premiumAdapter =
            KnowYourPremiumServicesAdapter(
                context, RegistrationFragment()
            )
        binding.premiumFeaturesRecyclerView.setHasFixedSize(true)
        binding.premiumFeaturesRecyclerView.adapter = premiumAdapter
        binding.premiumFeaturesRecyclerView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.premiumFeaturesRecyclerView.setItemViewCacheSize(20)
        binding.premiumFeaturesRecyclerView.isDrawingCacheEnabled = true
        binding.premiumFeaturesRecyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        binding.premiumFeaturesRecyclerView.invalidate()

        knowAdapter = KnowYourServicesAdapter(
            context, RegistrationFragment()
        )
        binding.knowServicesRecyclerView.setHasFixedSize(true)
        binding.knowServicesRecyclerView.adapter = knowAdapter
        binding.knowServicesRecyclerView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.knowServicesRecyclerView.setItemViewCacheSize(20)
        binding.knowServicesRecyclerView.isDrawingCacheEnabled = true
        binding.knowServicesRecyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        binding.knowServicesRecyclerView.invalidate()


        //This code adds a TextWatcher to an EditText view
        binding.nameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {

            }

            //The afterTextChanged method of the TextWatcher is called after the text in the EditText has changed.
            override fun afterTextChanged(editable: Editable) {
                //The code in this method checks if the text in the nameEt EditText is not empty and if the text in the locationEt EditText is not empty. If both conditions are true, it sets the isEnabled property of the registerDoneBtn button to true, enabling the button.
                if (binding.nameEt.text.toString().trim().isNotEmpty()) {
                    binding.registerDoneBtn.isEnabled =
                        binding.locationEt.text.toString().trim().isNotEmpty()
                }
                //If either condition is false, it sets the isEnabled property of the registerDoneBtn button to false, disabling the button.
                else {
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })

        //This code adds a TextWatcher to an EditText view
        binding.locationEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            //The afterTextChanged method of the TextWatcher is called after the text in the EditText has changed.
            override fun afterTextChanged(p0: Editable?) {
                //The code in this method checks if the length of the text in the locationEt EditText is not equal to 0. If the length is not equal to 0, it sets the isEnabled property of the registerDoneBtn button to true if the length of the text in the nameEt EditText is not equal to 0
                if (binding.locationEt.text.toString().trim().length != 0) {
                    binding.registerDoneBtn.isEnabled =
                        binding.nameEt.text.toString().trim().length != 0
                }
                //If the length of the text in the locationEt EditText is equal to 0, it sets the isEnabled property of the registerDoneBtn button to false, disabling the button.
                else {
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })

        ///This code sets an OnClickListener on a button and calls the userCreater function when the button is clicked.
        binding.registerDoneBtn.setOnClickListener { userCreater() }
        //The code sets an OnClickListener on the ImageView(locationIv) so that when it is clicked, the getLocation function is called after a 500 millisecond delay and the visibility of two text views is changed.
        //uses a Handler to post a delayed runnable that calls the getLocation function. The textEnterManually view's visibility is set to View.GONE and the textDetecting view's visibility is set to View.VISIBLE.
        binding.locationIv.setOnClickListener {
            Handler(Looper.myLooper()!!).postDelayed({
                getLocation()
            }, 500)
            //                getLocation()
            binding.textEnterManually.visibility = View.GONE
            binding.textDetecting.visibility = View.VISIBLE
        }

        //The code sets an OnClickListener on an ImageView(nameMic) so that when it is clicked the speechToText function
        //uses a Handler to post a delayed runnable that calls the userModule function after a 700 millisecond delay.
        binding.nameMic.setOnClickListener { speechToText() }
        Handler(Looper.myLooper()!!).postDelayed({
            userModule()
        }, 700)


        //uses a Handler to post a delayed runnable that calls the getLocation function after a 400 millisecond delay.
        Handler(Looper.myLooper()!!).postDelayed({
            getLocation()
        }, 400)
        return binding.root
    }

    private fun setTranslations() {
        TranslationsManager().loadString(
            "welcome_to_outgrow",
            binding.titleTv,
            "Welcome to Outgrow"
        )
        TranslationsManager().loadString(
            "enter_profile_details",
            binding.farmerDetMsgTv,
            "The following details will help us to personalize your Outgrow app experience."
        )
        TranslationsManager().loadString("enter_name", binding.textName, "Enter your name")
        TranslationsManager().loadString(
            "enter_location",
            binding.textLocation,
            "Enter your location"
        )
        TranslationsManager().loadString(
            "detect_location",
            binding.textDetecting,
            "Detecting your location.."
        )
        TranslationsManager().loadString(
            "enter_manually",
            binding.textEnterManually,
            "Could not find your location.Enter Manually."
        )
        TranslationsManager().loadString(
            "know_your_services",
            binding.knowServicesTv,
            "Know Your Services"
        )
        TranslationsManager().loadString(
            "premium_features",
            binding.premiumFeaturesTv,
            "Premium Features"
        )
        TranslationsManager().loadString("submit", binding.registerDoneBtn, "Submit")

    }

    private fun networkCall() {
        //Check Internet availability
        //if available go to "IF" condition
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            //Visibility of Internet Error Screen set as VISIBLE
            binding.clInclude.visibility = View.VISIBLE
        }
        //if not available go to "ELSE" condition
        else {
            //Visibility of Internet Error Screen set as GONE
            binding.clInclude.visibility = View.GONE
        }
    }

    //Calling this method for opening the dialog on clicking any service item -(KnowYourServicesAdapter has this click call)
    //Also Calling this method for opening the dialog on clicking any premium service item -(KnowYourPremiumServicesAdapter has this click call)
    fun showServiceDialog(
        tittle: String?,
        desc: String?,
        audiourl: String?,
        type: String,
        imageUrl: String?,
        context: Context
    ) {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.fragmrnt_service_desc_layoutr)
        val headerTv = bottomSheetDialog.findViewById<TextView>(R.id.desc_item_name)
        val UserTYpeTV = bottomSheetDialog.findViewById<TextView>(R.id.desc_service_name)
        val descTV = bottomSheetDialog.findViewById<TextView>(R.id.desc_tv)
        val close = bottomSheetDialog.findViewById<ImageView>(R.id.privacy_close_btn)
        val icon = bottomSheetDialog.findViewById<ImageView>(R.id.image)
        val descImage: ImageView = bottomSheetDialog.findViewById<ImageView>(R.id.desc_image)!!
        val audioLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.audio_layout)
        val play = bottomSheetDialog.findViewById<ImageView>(R.id.play)
        val pause = bottomSheetDialog.findViewById<ImageView>(R.id.pause)
        val seekbar = bottomSheetDialog.findViewById<SeekBar>(R.id.media_seekbar)
        val totalTime = bottomSheetDialog.findViewById<TextView>(R.id.total_time)
        //Set data in bottom sheet
        headerTv!!.text = tittle ?: ""
        descTV!!.text = desc ?: ""
        if (type == "0") {
            icon!!.visibility = View.GONE
        } else {
            UserTYpeTV!!.text = "Subscription"
            icon!!.visibility = View.VISIBLE
        }
        if (audiourl == null) {
            audioLayout!!.visibility = View.GONE
        } else {
            audioLayout!!.visibility = View.VISIBLE
        }
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).into(descImage)
        }

        descTV.movementMethod = ScrollingMovementMethod()
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.show()

        //closes the bottom sheet on click on close icon present in UI
        close!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            audioWife.release()
        }

// AudioWife- A simple audio player wrapper for Android
        audioWife = AudioWife.getInstance()
        audioWife.addOnCompletionListener {
            //                audioWife.release();
        }

        bottomSheetDialog.setOnDismissListener {
            audioWife.release()
        }

        //play audio functionality of audio

        play!!.setOnClickListener { view ->
            //if audio url is not null go to "IF" condition
            if (audiourl != null) {
                if (pause != null) {
                    playAudio(context, audiourl, play, pause, seekbar!!, totalTime!!)
                }
            }
            //if audio url is  null go to "ELSE" condition
            else {
                //showing toast for "Audio file not found"
                CoroutineScope(Dispatchers.Main).launch {
                    val toastAudioNotFound = TranslationsManager().getString("audio_file")
                    if (!toastAudioNotFound.isNullOrEmpty()) {
                        context.let { it1 ->
                            ToastStateHandling.toastError(
                                it1, toastAudioNotFound,
                                Toast.LENGTH_SHORT
                            )
                        }
                    } else {
                        context.let { it1 ->
                            ToastStateHandling.toastError(
                                it1, "Audio file not found",
                                Toast.LENGTH_SHORT
                            )
                        }
                    }
                }

            }
            //Event Click on clicking the audio play button
            EventClickHandling.calculateClickEvent("Listen$tittle")
        }

    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //if checked permission is true then The location object will not be null
        if (checkPermissions()) {
            //if location enabled is true
            if (isLocationEnabled()) {
                /*The FusedLocationProviderClient provides several methods to retrieve device location information.*/
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
                /*The getLastLocation() method returns a Task that you can use to get a Location object with the latitude and longitude coordinates of a geographic location.
                The location object may be null in the following situations:Location is turned off in the device settings.*/
                fusedLocationProviderClient?.lastLocation!!
                    .addOnSuccessListener {
                        if (it != null) {
                            //get details such as district, sub locality and locality
                            getGeocodeFromLocation(it)

                        }
                    }
                //Request location updates once the permission is granted.
                //Requests location updates with the given request and results delivered to the given callback on the specified Looper.
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
            //if check permission method returns false then request for permission
            requestPermission()
        }
    }

    private fun getGeocodeFromLocation(it: Location) {
        latitude = String.format(Locale.ENGLISH, "%.5f", it.latitude)
        longitutde = String.format(Locale.ENGLISH, "%.5f", it.longitude)

        //get details such as locality,sub locality and district
        viewModel.getReverseGeocode("${it.latitude},${it.longitude}")
            .observe(viewLifecycleOwner) {
                binding.locationEt.setText("")
                if (it.results.isNotEmpty()) {
                    val result = it.results[0]
                    if (result.subLocality != null)
                    //append sub locality to location edit text
                        binding.locationEt.append("${result.subLocality},")
                    if (result.locality != null)
                    //append  locality to location edit text
                        binding.locationEt.append("${result.locality},")
                    if (result.district != null)
                    //append sub district to location edit text
                        binding.locationEt.append(" ${result.district}")
                    binding.locationEt.setSelection(0)
                    //The location has been detected so we set visibility GONE of textEnterManually and textDetecting
                    binding.textEnterManually.visibility = View.GONE
                    binding.textDetecting.visibility = View.GONE
//                    binding.locationEt.error= ""

                    address = result.formattedAddress.toString()
                    village = result.subLocality.toString()
                    pincode = result.pincode.toString()
                    state = result.state.toString()
                    district = result.district.toString()
                } else {
                    //If Location detection fails we set visibility of textEnterManually as VISIBLE
                    binding.textEnterManually.visibility = View.VISIBLE
                    binding.textDetecting.visibility = View.GONE
                }

            }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //check permissions for Location
    private fun checkPermissions(): Boolean {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            /*if permission for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION is granted return true */
            return true
        }
        /*if permission for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION is not granted return false */
        return false
    }

    //This code defines a function requestPermission which launches a request for multiple permissions using the requestPermissionLauncher instance of ActivityResultContracts.RequestMultiplePermissions. The permissions requested are for accessing coarse and fine location information.
    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    //This code is handling the result of a request for permissions to access the device's location. The requestCode of 2 is used to identify this specific request. If the grant results array is not empty and the first element (corresponding to the first requested permission) is equal to PackageManager.PERMISSION_GRANTED, then the getLocation() method is called.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    fun userCreater() {
        if (latitude.isNotEmpty() && longitutde.isNotEmpty()) {
            if (NetworkUtil.getConnectivityStatusString(context) == 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastCheckInternet = TranslationsManager().getString("check_your_interent")
                    if(!toastCheckInternet.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toastCheckInternet,
                            LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastSuccess(it1,"Please check your internet connection",
                        LENGTH_SHORT
                    ) }}}
            } else {
                query = HashMap()
                query["name"] = binding.nameEt.text.toString().trim()
                query["contact"] = mobileNumber.toString()
                query["lat"] = latitude
                query["long"] = longitutde
                query["lang_id"] = "1"
                query["email"] = ""
                if(!pincode.isNullOrEmpty()) {
                    query["pincode"] = pincode!!
                }
                if (village != null) {
                    query["village"] = village!!
                }
                if (address != null) {
                    query["address"] = address!!
                }
                if(!state.isNullOrEmpty()||!district.isNullOrEmpty()) {
                    query["state"] = state!!
                    query["district"] = district!!
                }
                query["sub_district_id"] = ""
                binding.progressBarSubmit.visibility = View.VISIBLE
                binding.registerDoneBtn.visibility = View.GONE
                viewModel.getUserData(query).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            binding.progressBarSubmit.visibility = View.GONE
                            binding.registerDoneBtn.visibility = View.VISIBLE

                            lifecycleScope.launch {
                                userLogin()
                            }
                        }
                        is Resource.Loading -> {
                            binding.progressBarSubmit.visibility = View.GONE
                            binding.registerDoneBtn.visibility = View.VISIBLE

                        }
                        is Resource.Error -> {
                            it.message?.let { it1 ->
                                context?.let { it2 ->
                                    ToastStateHandling.toastSuccess(
                                        it2,
                                        it1, Toast.LENGTH_SHORT
                                    )
                                }
                            }
                            binding.progressBarSubmit.visibility = View.VISIBLE
                            binding.registerDoneBtn.visibility = View.GONE

                        }
                    }


                }
                EventClickHandling.calculateClickEvent("Login_Name${binding.nameEt.text}")
                EventClickHandling.calculateClickEvent("Login_Location${address}")
            }
        } else {
            getLocation()

        }
    }

    //The function userLogin is used to perform a login operation by calling the login method from the viewModel object.
    suspend fun userLogin() {
        //The login method takes in several parameters such as the mobile number, FCM token, device model, and device manufacturer.
        viewModel.login(
            mobileNumber!!,
            viewModel.getFcmToken(),
            viewModel.getDeviceModel(),
            viewModel.getDeviceManufacturer()
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    //a Toast message with "Successfully Registered" is displayed.
                    viewModel.viewModelScope.launch {
                        val toastSuccessfullyRegistered = TranslationsManager().getString("successfully_registered")
                        if(!toastSuccessfullyRegistered.isNullOrEmpty()){
                            context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toastSuccessfullyRegistered,
                                Toast.LENGTH_SHORT
                            ) }}
                        else {context?.let { it1 -> ToastStateHandling.toastSuccess(it1,"Successfully Registered",
                            Toast.LENGTH_SHORT
                        ) }}}
                    val loginDataMaster = it.data
                    //if the status is true, the user token, mobile number, and logged-in status is saved in the viewModel
                    if (loginDataMaster?.status == true) {
                        viewModel.setUserToken(
                            loginDataMaster.data
                        )
                        viewModel.setMobileNumber(mobileNumber.toString())
                        viewModel.setIsLoggedIn(true)

                        //After a delay of 200ms, the function getUserDetails is called, and if the user data is present, the gotoMainActivity method is called.
                        Handler(Looper.myLooper()!!).postDelayed({
                            viewModel.getUserDetails().observe(viewLifecycleOwner) { user ->
                                if (user.data != null && user.data?.userId != null) {
                                    gotoMainActivity()
                                }
                            }
                        }, 200)

                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    //It shows a toast message with "Server Error Occurred" or with a translated message from the TranslationsManager class.
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
            }

        }
    }

    private fun gotoMainActivity() {
        val intent = Intent()
        intent.setClassName(requireContext(), "com.waycool.iwap.MainActivity")
        startActivity(intent)
        TranslationsManager().init()
        requireActivity().finish()
    }

    //get Module Master data
    private fun userModule() {
        //observing the data from the viewModel.getModuleMaster() LiveData
        viewModel.getModuleMaster().observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    // filters the list of modules based on their subscription status (0 = free, 1 = paid)
                    val freelist = it.data?.filter { it.subscription == 0 } as MutableList
                    val paidlist = it.data?.filter { it.subscription == 1 } as MutableList
                    //update the data in the adapters knowAdapter and premiumAdapter with the respective filtered lists.
                    knowAdapter.update(freelist)
                    premiumAdapter.update(paidlist)
                }

                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    //It shows a toast message with "Server Error Occurred" or with a translated message from the TranslationsManager class.
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
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        super.onAttach(requireActivity())
        mContext = context
    }

    //This code is for converting speech to text using the device's built-in speech recognition system.
    private fun speechToText() {
        //An Intent with action ACTION_RECOGNIZE_SPEECH is created and putExtra values are set for language model, language, and prompt.
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        try {
            //The Intent is then passed to startActivityForResult method with a request code REQUEST_CODE_SPEECH_INPUT to start the speech recognition system.
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            //In case of any exception, a toast error message is displayed.
            ToastStateHandling.toastError(
                requireContext(), " " + e.message,
                Toast.LENGTH_SHORT
            )
        }
    }

    //This code handles the result of the speech-to-text and GPS activities.
    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        //If the speech-to-text activity has a result of RESULT_OK and data, the first recognized speech is extracted and set as the text of the binding.nameEt EditText.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                val searchTag: String = Objects.requireNonNull(result).get(0) ?: ""
                binding.nameEt.setText(searchTag)

            }
        }
        //If the GPS activity has a result of RESULT_OK, the getLocation method is called.
        if (resultCode == REQUEST_CODE_GPS && resultCode == AppCompatActivity.RESULT_OK) {
            getLocation()
        }
    }


    //This code defines a function named playAudio that plays audio using the MediaPlayer API in Android. The function takes the following arguments:
    //
    //context: a reference to the current Context
    //path: the path to the audio file
    //play: an ImageView that represents the play button
    //pause: an ImageView that represents the pause button
    //mediaSeekbar: a SeekBar that displays the progress of the audio playback
    //totalTime: a TextView that displays the total duration of the audio
    private fun playAudio(
        context: Context,
        path: String,
        play: ImageView,
        pause: ImageView,
        mediaSeekbar: SeekBar,
        totalTime: TextView
    ) {

        //The function initializes a MediaPlayer object and sets an OnCompletionListener on it. When the audio playback completes, the mediaSeekbar is reset to 0 and the visibility of the pause and play ImageViews are changed.
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            mediaSeekbar.progress = 0
            pause.visibility = View.GONE
            play.visibility = View.VISIBLE
        }

        //The function also creates an instance of AudioWife and initializes it with the context, path, play ImageView, pause ImageView, mediaSeekbar, and totalTime TextView.
        //The play method of the AudioWife instance is called to start the audio playback.
        val audio = AudioWife.getInstance()
            .init(context, Uri.parse(path))
            .setPlayView(play)
            .setPauseView(pause)
            .setSeekBar(mediaSeekbar)
            .setRuntimeView(totalTime)
        audio.play()

    }

    override fun onDestroy() {
        super.onDestroy()
        // the method removes the location updates from the fusedLocationProviderClient by calling the removeLocationUpdates method with the locationCallback as its argument. This stops the location updates and prevents any further updates from being received.
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    //The two constants REQUEST_CODE_SPEECH_INPUT and REQUEST_CODE_GPS are defined within this companion object, and they can be accessed using the class name
    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 101
        private const val REQUEST_CODE_GPS = 1011
    }

    override fun onResume() {
        super.onResume()
        //Time Spent on this Screen
        EventScreenTimeHandling.calculateScreenTime("Registration")
    }
}
