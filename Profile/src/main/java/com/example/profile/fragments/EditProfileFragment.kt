package com.example.profile.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.profile.databinding.FragmentEditProfileBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.waycool.data.translations.TranslationsManager
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap


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
    private lateinit var long:String

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
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
                binding.tvName.setText(it.data?.data?.name)
                binding.tvPhoneNo.setText("+91 ${it.data?.data?.contact}")
                binding.tvAddress1.setText(it.data?.data?.profile?.address)
                binding.tvAddress2.setText(it.data?.data?.profile?.village)
                binding.tvPincode.setText(it.data?.data?.profile?.pincode)
                binding.tvState.setText(it.data?.data?.profile?.state)
                binding.tvCity.setText(it.data?.data?.profile?.district)

            }
        }
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            if (it.data?.profile?.remotePhotoUrl != null && selecteduri == null) {
                Glide.with(this).load(it.data?.profile?.remotePhotoUrl).into(binding.imageView)
            }
        }

        if (selecteduri != null) {
            Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
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
        field.put("lat",lat)
        field.put("long",long)


        if (name.isNotEmpty() && address.isNotEmpty() && village.isNotEmpty() && pincode.isNotEmpty()
            && state.isNotEmpty() && city.isNotEmpty()
        ) {

            viewModel.viewModelScope.launch {
                viewModel.getProfileRepository(field)
                    .observe(viewLifecycleOwner) {
                        Log.d("ProfileUpdate", "editProfile: $it")
                        Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    }
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

                Log.d("selecteduri", "editProfile: $profileImageBody")

                viewModel.viewModelScope.launch {
                    viewModel.getUserProfilePic(profileImageBody).observe(viewLifecycleOwner) {
                        Log.d("selecteduri", "editProfile: ${it.data?.profile_pic}")
                    }
                }
            }

            this.findNavController().navigateUp()

        } else {
            Toast.makeText(context, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
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

    private fun getLocation() {
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                //Toast.makeText(context, "${it.longitude} ${it.latitude}", Toast.LENGTH_LONG).show()

                  lat = String.format("%.5f",it.latitude)
                 long = String.format("%.5f",it.longitude)
                Toast.makeText(context, "${lat} ${long}", Toast.LENGTH_LONG).show()
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
                        }
                    }


            }
//                val bounds = RectangularBounds.newInstance(
//                    LatLng(-33.880490, it.latitude),
//                    LatLng(-33.858754,it.longitude)
//                )


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
            if (resultCode == AppCompatActivity.RESULT_OK && requestCode == requestImageId) {
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
//            val file = selecteduri?.toFile()
//            val profileImage: RequestBody = RequestBody.create(
//                "image/jpg".toMediaTypeOrNull(),
//                file!!
//            )
//
//            val profileImageBody: MultipartBody.Part =
//                MultipartBody.Part.createFormData(
//                    "profile_pic",
//                    file.name, profileImage
//                )
//            viewModel.viewModelScope.launch {
//                selecteduri?.let { viewModel.getUserProfilePic(profileImageBody) }
//            }
            Log.d("ProfilePicImage2", "editProfile: $selecteduri")
        }
    }

    private fun translation(){
        TranslationsManager().loadString("str_farmer_name",binding.textView3)
        TranslationsManager().loadString("str_mobile_number",binding.textView4)
        TranslationsManager().loadString("str_addressline_1",binding.textView5)
        TranslationsManager().loadString("str_city",binding.textView6)
        TranslationsManager().loadString("str_state",binding.textView8)
        TranslationsManager().loadString("str_pincode",binding.textView9)

    }
}
