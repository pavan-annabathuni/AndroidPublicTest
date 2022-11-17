package com.waycool.featurelogin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.data.utils.SharedPreferenceUtility
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.UserProfileKnowServiceAdapter
import com.waycool.featurelogin.adapter.UserProfilePremiumAdapter
import com.waycool.featurelogin.databinding.FragmentRegistrationBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ToolbarLayoutBinding
import com.waycool.uicomponents.utils.AppUtil
import kotlinx.coroutines.launch
import nl.changer.audiowife.AudioWife
import java.util.*


class RegistrationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationBinding
    var k: Int = 4
    var dummylist: MutableList<ModuleMasterDomain> = mutableListOf()
    var name: Array<String> = arrayOf("Mandi", "Crop Health", "Crop Information", "News")
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private val REQUEST_CODE_SPEECH_INPUT = 101
    var latitude: String = ""
    var longitutde: String = ""
    var village: String? = ""
    var state = ""
    var district = ""
    var subDistrict = ""
    var pincode = ""
    lateinit var knowAdapter: UserProfileKnowServiceAdapter
    lateinit var premiumAdapter: UserProfilePremiumAdapter
    var mobileNumber: String? = ""
    lateinit var mContext: Context
    val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    lateinit var query: HashMap<String, String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>
    lateinit var audioWife: AudioWife
    lateinit var mediaPlayer: MediaPlayer


    init {
        this.activityResultLauncher = registerForActivityResult(
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
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(layoutInflater)

        binding.registerDoneBtn.isEnabled = true

        val toolbarLayoutBinding: ToolbarLayoutBinding =
            ToolbarLayoutBinding.inflate(layoutInflater)
        binding.toolbar.toolbarTile.text = "Profile"
        binding.toolbar.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }

        if (arguments?.getString("mobile_number") != null) {
            mobileNumber = arguments?.getString("mobile_number")
//            Toast.makeText(requireContext(), mobileNumber, Toast.LENGTH_SHORT).show()

            binding.nameEt.setText(arguments?.getString("name"))
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        premiumAdapter = UserProfilePremiumAdapter(
            dummylist,
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
            dummylist,
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
                    if (binding.locationEt.text.toString().trim().length != 0) {
                        binding.registerDoneBtn.isEnabled = true
                    } else {
                        binding.registerDoneBtn.isEnabled = false
                    }
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
                    if (binding.nameEt.text.toString().trim().length != 0) {
                        binding.registerDoneBtn.isEnabled = true
                    } else {
                        binding.registerDoneBtn.isEnabled = false
                    }
                } else {
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })
        binding.registerDoneBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                AppUtil.Toast(context, "cliked")
                userCreater()
            }
        })
        binding.locationIv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Handler(Looper.myLooper()!!).postDelayed({
                    getLocation()
                }, 500)
//                getLocation()
                binding.locationTextlayout.helperText = "Detecting your location.."
            }
        })
        binding.nameMic.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                speechToText()
            }
        })
        Handler(Looper.myLooper()!!).postDelayed({
            userModule()
        }, 700)


        Handler(Looper.myLooper()!!).postDelayed({
            getLocation()
        }, 400)
        return binding.root
    }

    fun replaceFragmentwithoutbackstack(
        tittle: String,
        desc: String,
        url: String?,
        type: String,
        context: Context
    ) {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.fragmrnt_service_desc_layoutr)
        val headerTv = bottomSheetDialog.findViewById<TextView>(R.id.desc_item_name)
        val UserTYpeTV = bottomSheetDialog.findViewById<TextView>(R.id.desc_service_name)
        val descTV = bottomSheetDialog.findViewById<TextView>(R.id.desc_tv)
        val close = bottomSheetDialog.findViewById<ImageView>(R.id.privacy_close_btn)
        val icon = bottomSheetDialog.findViewById<ImageView>(R.id.image)
        val audioLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.audio_layout)
        val play = bottomSheetDialog.findViewById<ImageView>(R.id.play)
        val pause = bottomSheetDialog.findViewById<ImageView>(R.id.play)
        val seekbar = bottomSheetDialog.findViewById<SeekBar>(R.id.media_seekbar)
        val totalTime = bottomSheetDialog.findViewById<TextView>(R.id.total_time)
        headerTv!!.setText(tittle)
        descTV!!.setText(desc)
        if (type.equals("0")) {
            UserTYpeTV!!.setText("Free User")
            icon!!.visibility = View.GONE
        } else {
            UserTYpeTV!!.setText("Premium User")
            icon!!.visibility = View.VISIBLE
        }
        if (url == null) {
            audioLayout!!.visibility = View.GONE
        } else {
            audioLayout!!.visibility = View.VISIBLE
        }
        descTV.setMovementMethod(ScrollingMovementMethod())
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.show()
        close!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                bottomSheetDialog.dismiss()
            }
        })
        audioWife = AudioWife.getInstance()
        audioWife.addOnCompletionListener(MediaPlayer.OnCompletionListener {
            //                audioWife.release();
        })

        play!!.setOnClickListener { view ->
            if (url != null) {
                playAudio(url, play, pause!!, seekbar!!, totalTime!!)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Audio file not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            Toast.makeText(requireContext(), "completed", Toast.LENGTH_SHORT).show()
            seekbar!!.setProgress(0)
            pause!!.setVisibility(View.GONE)
            play.setVisibility(View.VISIBLE)
        })
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
                        }
                        if (location != null) {

                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val list: List<Address> =
                                geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                ) as List<Address>

                            if (list.isNotEmpty()) {
                                subDistrict = list[0].locality
                                village = list[0].subLocality
//                                district = list[0].subAdminArea
                                state = list[0].adminArea
                                pincode = list[0].postalCode
                            }
                            if (village == null || village!!.isEmpty()) {
                                if (subDistrict.isEmpty()) {
                                    if (district.isEmpty()) {

                                    } else {
                                        village = district
                                    }
                                } else {
                                    village = subDistrict
                                }
                            }
                        }
                        binding.locationEt.setText("$village, $district")
                        binding.locationTextlayout.helperText = ""
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

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
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

    fun userCreater() {
        if (latitude.length > 0 && longitutde.length > 0) {
            if (NetworkUtil.getConnectivityStatusString(context) == 0) {
                Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
            } else {
                query = HashMap()
                query.put("name", binding.nameEt.text.toString().trim())
                query.put("contact", mobileNumber.toString())
                query.put("lat", latitude)
                query.put("long", longitutde)
                query.put("lang_id", "1")
                query.put("email", "")
                query.put("pincode", pincode)
                village?.let { query.put("village", it) }
                query.put("address", "")
                query.put("state_id", "")
                query.put("district_id", "")
                query.put("sub_district_id", "")
                Log.d(
                    "register",
                    "test" + binding.nameEt.text.toString()
                        .trim() + " " + mobileNumber + " " + latitude + " " + longitutde + " " + pincode + " " + village
                );

                loginViewModel.getUserData(query).observe(this) {
                    //registerMaster.data.approved

                    when (it) {
                        is Resource.Success -> {
                            Toast.makeText(
                                activity,
                                "Successfully Registered",
                                Toast.LENGTH_SHORT
                            ).show()
                            lifecycleScope.launch {
                                userLogin()
                            }
                        }
                        is Resource.Loading -> {}
                        is Resource.Error -> {

                            Toast.makeText(
                                activity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }
        } else {
            getLocation();
            Toast.makeText(
                activity,
                "Again Click Submit Button",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    suspend fun userLogin() {

        loginViewModel.login(
            mobileNumber!!,
            loginViewModel.getFcmToken(),
            loginViewModel.getDeviceModel(),
            loginViewModel.getDeviceManufacturer()
        ).observe(this) {
            when (it) {
                is Resource.Success -> {
                    val loginDataMaster = it.data
                    if (loginDataMaster?.status == true) {
                        SharedPreferenceUtility.seUserToken(
                            context,
                            loginDataMaster.data
                        )
                        SharedPreferenceUtility.setMobileNumber(context, mobileNumber)
                        SharedPreferenceUtility.setLogin(context, "1")

                        loginViewModel.getUserDetails().observe(requireActivity()) {}
                        gotoMainActivity()
//                        requireActivity().setResult(RESULT_OK)
//                        requireActivity().finish()
                    }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {}
            }

        }
    }

    private fun gotoMainActivity() {
        val intent = Intent()
        intent.setClassName(requireContext(), "com.waycool.iwap.MainActivity")
        startActivity(intent)
        requireActivity().finish()
    }

    private fun userModule() {
        loginViewModel.getModuleMaster().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    dummylist.addAll(it.data as MutableList)
                    knowAdapter.notifyDataSetChanged()
                    premiumAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                        .show()

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
        val intent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
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
            Toast
                .makeText(
                    requireContext(), " " + e.message,
                    Toast.LENGTH_SHORT
                )
                .show()
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
                var searchTag: String = Objects.requireNonNull(result)!![0]
                binding.nameEt.setText(searchTag)

            }
        }
    }

    private fun playAudio(
        path: String,
        play: ImageView,
        pause: ImageView,
        mediaSeekbar: SeekBar,
        totalTime: TextView
    ) {
        Log.d("RecordView", "actiondown2")
        audioWife = AudioWife.getInstance()
        audioWife.init(requireContext(), Uri.parse(path))
            .setPlayView(play)
            .setPauseView(pause)
            .setSeekBar(mediaSeekbar)
            .setRuntimeView(totalTime)
        //                .setTotalTimeView(mTotalTime);
        audioWife.play()
    }
}