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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.UserProfileKnowServiceAdapter
import com.waycool.featurelogin.adapter.UserProfilePremiumAdapter
import com.waycool.featurelogin.databinding.FragmentRegistrationBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.databinding.ToolbarLayoutBinding
import kotlinx.coroutines.launch
import nl.changer.audiowife.AudioWife
import java.util.*


class RegistrationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationBinding
    var k: Int = 4

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude: String = ""
    var longitutde: String = ""
    var address: String? = ""
    var village: String? = ""
    var state = ""
    var district = ""
    var pincode = ""
    lateinit var knowAdapter: UserProfileKnowServiceAdapter
    lateinit var premiumAdapter: UserProfilePremiumAdapter
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

    private val filter: InputFilter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        apiErrorHandlingBinding = binding.errorState

        binding.registerDoneBtn.isEnabled = true

        Places.initialize(requireActivity().applicationContext, AppSecrets.getMapsKey())
        placesClient = Places.createClient(requireContext())

        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }
        val toolbarLayoutBinding: ToolbarLayoutBinding = binding.toolbar
        toolbarLayoutBinding.toolbarTile.text = "Profile"
        toolbarLayoutBinding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack(R.id.loginFragment, false)
        }

        if (arguments?.getString("mobile_number") != null) {
            mobileNumber = arguments?.getString("mobile_number")
            binding.nameEt.setText(arguments?.getString("name"))
        }

        binding.nameEt.filters = arrayOf(filter)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        premiumAdapter = UserProfilePremiumAdapter(
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

        knowAdapter = UserProfileKnowServiceAdapter(
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
        binding.nameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (binding.nameEt.text.toString().trim().isNotEmpty()) {
                    binding.registerDoneBtn.isEnabled =
                        binding.locationEt.text.toString().trim().isNotEmpty()
                } else {
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })
        binding.locationEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.locationEt.text.toString().trim().length != 0) {
                    binding.registerDoneBtn.isEnabled =
                        binding.nameEt.text.toString().trim().length != 0
                } else {
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })
        binding.registerDoneBtn.setOnClickListener { userCreater() }
        binding.locationIv.setOnClickListener {
            Handler(Looper.myLooper()!!).postDelayed({
                getLocation()
            }, 500)
            //                getLocation()
            binding.locationTextlayout.helperText = "Detecting your location.."
        }
        binding.nameMic.setOnClickListener { speechToText() }
        Handler(Looper.myLooper()!!).postDelayed({
            userModule()
        }, 700)


        Handler(Looper.myLooper()!!).postDelayed({
            getLocation()
        }, 400)
        return binding.root
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
        } else {
            binding.clInclude.visibility = View.GONE
        }
    }

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
        headerTv!!.text = tittle ?: ""
        descTV!!.text = desc ?: ""
        if (type.equals("0")) {
//            UserTYpeTV!!.setText("Free User")
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
        close!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            audioWife.release()
        }
        audioWife = AudioWife.getInstance()
        audioWife.addOnCompletionListener {
            //                audioWife.release();
        }

        bottomSheetDialog.setOnDismissListener {
            audioWife.release()
        }
        play!!.setOnClickListener { view ->
            if (audiourl != null) {
                if (pause != null) {
                    playAudio(context, audiourl, play, pause, seekbar!!, totalTime!!)
                }
            } else {
                context?.let {
                    ToastStateHandling.toastError(
                        it,
                        "Audio file not found",
                        Toast.LENGTH_SHORT
                    )
                }

            }
        }

    }

//    @SuppressLint("MissingPermission")
//    private fun getLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//
//                mFusedLocationClient =
//                    LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
//
//                binding.locationTextlayout.helperText = "Detecting your location.."
//
//                mFusedLocationClient.lastLocation
//                    .addOnSuccessListener { location: Location? ->
//                        if (location != null) {
//                            latitude = String.format(Locale.ENGLISH, "%.5f", location.latitude)
//                            longitutde = String.format(Locale.ENGLISH, "%.5f", location.longitude)
//
//                            viewModel.getReverseGeocode("${location.latitude},${location.longitude}")
//                                .observe(viewLifecycleOwner) {
//                                    binding.locationEt.setText("")
//                                    if (it.results.isNotEmpty()) {
//                                        val result = it.results[0]
//                                        if (result.subLocality != null)
//                                            binding.locationEt.append("${result.subLocality},")
//                                        if (result.locality != null)
//                                            binding.locationEt.append("${result.locality},")
//                                        if (result.district != null)
//                                            binding.locationEt.append(" ${result.district}")
//                                        binding.locationEt.setSelection(0)
//                                        binding.locationTextlayout.helperText = ""
//
//                                        address = result.formattedAddress.toString()
//                                        village = result.subLocality.toString()
//                                        pincode = result.pincode.toString()
//                                        state = result.state.toString()
//                                        district = result.district.toString()
//                                    } else {
////                                        binding.locationEt.setText("$village, $district")
//                                        binding.locationTextlayout.helperText =
//                                            "Could not find your location. " +
//                                                    "Enter Manually."
//                                    }
//
//                                }
//
//                        }
//                    }
//                    .addOnFailureListener {
//                        it.message?.let { it1 ->
//                            ToastStateHandling.toastError(requireContext(),
//                                it1,Toast.LENGTH_SHORT)
//                        }
//                        Log.d("Registration", "" + it.message)
//                    }
//                    .addOnCanceledListener {
//                        ToastStateHandling.toastError(requireContext(),
//                            "Cancelled",Toast.LENGTH_SHORT)
//
//                    }
//            } else {
//                ToastStateHandling.toastError(requireContext(),
//                    "Please turn on location",Toast.LENGTH_SHORT)
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermission()
//        }
//    }

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
        latitude = String.format(Locale.ENGLISH, "%.5f", it.latitude)
        longitutde = String.format(Locale.ENGLISH, "%.5f", it.longitude)

        viewModel.getReverseGeocode("${it.latitude},${it.longitude}")
            .observe(viewLifecycleOwner) {
                binding.locationEt.setText("")
                if (it.results.isNotEmpty()) {
                    val result = it.results[0]
                    if (result.subLocality != null)
                        binding.locationEt.append("${result.subLocality},")
                    if (result.locality != null)
                        binding.locationEt.append("${result.locality},")
                    if (result.district != null)
                        binding.locationEt.append(" ${result.district}")
                    binding.locationEt.setSelection(0)
                    binding.locationTextlayout.helperText = ""

                    address = result.formattedAddress.toString()
                    village = result.subLocality.toString()
                    pincode = result.pincode.toString()
                    state = result.state.toString()
                    district = result.district.toString()
                } else {
//                                        binding.locationEt.setText("$village, $district")
                    binding.locationTextlayout.helperText =
                        "Could not find your location. " +
                                "Enter Manually."
                }

            }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(
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
        if (requestCode == 2) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    fun userCreater() {
        if (latitude.isNotEmpty() && longitutde.isNotEmpty()) {
            if (NetworkUtil.getConnectivityStatusString(context) == 0) {
                context?.let {
                    ToastStateHandling.toastError(
                        it,
                        "Please check your Internet connection",
                        Toast.LENGTH_LONG
                    )
                }
            } else {
                query = HashMap()
                query["name"] = binding.nameEt.text.toString().trim()
                query["contact"] = mobileNumber.toString()
                query["lat"] = latitude
                query["long"] = longitutde
                query["lang_id"] = "1"
                query["email"] = ""
                query["pincode"] = pincode
                if (village != null) {
                    query["village"] = village!!
                }
                if (address != null) {
                    query["address"] = address!!
                }
                query["state"] = state
                query["district"] = district
                query["sub_district_id"] = ""
                binding.progressBarSubmit.visibility = View.VISIBLE
                binding.registerDoneBtn.visibility = View.GONE
                viewModel.getUserData(query).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            binding.progressBarSubmit.visibility = View.GONE
                            binding.registerDoneBtn.visibility = View.VISIBLE
                            context?.let { it1 ->
                                ToastStateHandling.toastSuccess(
                                    it1,
                                    "SuccessFully Registered",
                                    Toast.LENGTH_SHORT
                                )
                            }
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
            }
        } else {
            getLocation()

        }
    }

    suspend fun userLogin() {
        viewModel.login(
            mobileNumber!!,
            viewModel.getFcmToken(),
            viewModel.getDeviceModel(),
            viewModel.getDeviceManufacturer()
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val loginDataMaster = it.data
                    if (loginDataMaster?.status == true) {
                        viewModel.setUserToken(
                            loginDataMaster.data
                        )
                        viewModel.setMobileNumber(mobileNumber.toString())
                        viewModel.setIsLoggedIn(true)
                        viewModel.getUserDetails().observe(viewLifecycleOwner) { user ->
                            if (user.data != null && user.data?.userId != null) {

                                gotoMainActivity()
                            }
                        }
/*                        viewModel.getUserDetails().observe(viewLifecycleOwner) {
                            gotoMainActivity()
                        }*/

                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {

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

    private fun userModule() {
        viewModel.getModuleMaster().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    val freelist = it.data?.filter { it.subscription == 0 } as MutableList
                    val paidlist = it.data?.filter { it.subscription == 1 } as MutableList
                    knowAdapter.update(freelist)
                    premiumAdapter.update(paidlist)
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
//                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
                is Resource.Error -> {
                    ToastStateHandling.toastError(
                        requireContext(),
                        "Error: ${it.message}",
                        Toast.LENGTH_SHORT
                    )

                }
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        super.onAttach(requireActivity())
        mContext = context
    }

    private fun speechToText() {
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
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            ToastStateHandling.toastError(
                requireContext(), " " + e.message,
                Toast.LENGTH_SHORT
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                val searchTag: String = Objects.requireNonNull(result)!![0]
                binding.nameEt.setText(searchTag)

            }
        }
        if (resultCode == REQUEST_CODE_GPS && resultCode == AppCompatActivity.RESULT_OK) {
            getLocation()
        }
    }

    private fun playAudio(
        context: Context,
        path: String,
        play: ImageView,
        pause: ImageView,
        mediaSeekbar: SeekBar,
        totalTime: TextView
    ) {

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            mediaSeekbar.progress = 0
            pause.visibility = View.GONE
            play.visibility = View.VISIBLE
        }

        Log.d("Audio", "audioPlayer: $audioUrl")
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
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 101
        private const val REQUEST_CODE_GPS = 1011
    }
}
