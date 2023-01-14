package com.example.soiltesting.ui.checksoil

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentCheckSoilTestBinding
import com.example.soiltesting.ui.history.HistoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import java.util.*

class CheckSoilTestFragment : Fragment() {
    private var _binding: FragmentCheckSoilTestBinding? = null
    private val binding get() = _binding!!

    //    private var soilTestingLabsAdapter = SoilTestingLabsAdapter()
    private var list: CheckSoilTestData? = null
    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }
    private var latitude: String? = null
    private var longitude: String? = null
    private var accountID: Int? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //    private val navArgs:CheckSoilTestFragmentArgs by navArgs()
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckSoilTestBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (arguments != null) {
            val your_list = arguments?.getParcelableArrayList<CheckSoilTestDomain>("list")
            binding.tvLabTitle.text = your_list?.get(0)?.onpName.toString()
            binding.tvName.text = your_list?.get(0)?.onpAddress.toString()
            binding.tvCheckCrop.text =
                your_list?.get(0)?.onpDistanceKm.toString() + " from your location"
            binding.pinCode.text = your_list?.get(0)?.onpPincode.toString()
            binding.btnSelectCrop.setOnClickListener {
                findNavController().navigate(R.id.action_checkSoilTestFragment_to_selectCropSoilTestFragment)
            }
            binding.editImage.setOnClickListener {
                findNavController().navigate(R.id.action_checkSoilTestFragment_to_selectCropSoilTestFragment)
            }
            binding.selectCropEdit.setOnClickListener {
                findNavController().navigate(R.id.action_checkSoilTestFragment_to_selectCropSoilTestFragment)
            }
            latitude = arguments?.getString("lat")
            longitude = arguments?.getString("lon")
//            binding.cardCheckHealth.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString("onp_id", your_list?.get(0)?.onpName.toString())
//                findNavController().navigate(
//                    R.id.action_checkSoilTestFragment_to_newSoilTestFormFragment,
//                    bundle
//                )
//
//            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewBackClick()
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountID = it.data?.accountId
            Log.d("TAG", "onViewCreatedcjbsdfbwsf $accountID")
            if (accountID != null) {
                Log.d("TAG", "onViewCreatedcjbsdfbwsf $accountID")
                isLocationPermissionGranted(accountID!!)
            }
        }
        translationSoilTesting()

    }
    fun translationSoilTesting() {
        TranslationsManager().loadString("next", binding.cardCheckHealth)
        TranslationsManager().loadString("add", binding.addText,"Add")
        TranslationsManager().loadString("str_edit", binding.selectCropEdit,"Edit")
        TranslationsManager().loadString("soil_sample_will_be_collected_by_the_selected_lab", binding.collectedLab,"Soil sample will be collected by the selected Lab")
        TranslationsManager().loadString("select_crop", binding.selectCrop,"Select Crop")
        TranslationsManager().loadString("select_testing_lab", binding.toolBarTittle,"Select Testing Lab")


//        TranslationsManager().loadString("crop_nickname", binding.plotNumber)
//        TranslationsManager().loadString("crop_area", binding.pincodeNumber)
//        TranslationsManager().loadString("sowing_date", binding.Address)
//        TranslationsManager().loadString("submit", binding.tvCheckCrop)
    }



    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
//            soilTestingLabsAdapter.upDateList()
        }
    }
    private fun isLocationPermissionGranted(account_id: Int): Boolean {
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
            false
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && account_id != null) {

                        val latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)


                        viewModel.getCheckSoilTestLab(
                            account_id,
                            latitude,
                            longitutde
                        ).observe(requireActivity()) {
                            when (it) {
                                is Resource.Success -> {
                                    var your_list = it.data
                                    binding.tvLabTitle.text = your_list?.get(0)?.onpName.toString()
                                    binding.tvName.text = your_list?.get(0)?.onpAddress.toString()
                                    binding.tvCheckCrop.text =
                                        your_list?.get(0)?.onpDistanceKm.toString() + " from your location"
                                    binding.pinCode.text = your_list?.get(0)?.onpPincode.toString()
                                    val latitude =
                                        String.format(Locale.ENGLISH, "%.2f", location.latitude)
                                    val longitutde =
                                        String.format(Locale.ENGLISH, "%.2f", location.longitude)
                                    val bundle = Bundle()
                                    bundle.putString("lat", latitude)
                                    bundle.putString("lon", longitutde)
                                    bundle.putString(
                                        "onp_id",
                                        your_list?.get(0)?.onpName.toString()
                                    )
                                    val idOne = arguments?.getInt("crop_id")
                                    val idTwo = arguments?.getString("name")
                                    val idImage = arguments?.getString("crop_logo")

                                    if (idTwo.isNullOrEmpty() || idOne ==null) {
                                        binding.btnSelectCrop.visibility = View.VISIBLE
                                    } else {
                                        binding.imageView.visibility = View.VISIBLE
//                                        binding.editImage.visibility = View.VISIBLE
                                        binding.tvCrops.visibility=View.VISIBLE
                                        binding.cardCheckHealth.isEnabled = true
                                        binding.editImage.visibility = View.VISIBLE
                                        binding.btnSelectCrop.visibility = View.GONE
//                                        binding.selectCrop.text = "Selected Crop"
                                        TranslationsManager().loadString("txt_select_crop", binding.selectCrop,"Selected Crop")
                                        binding.selectCropEdit.setTextColor(Color.parseColor("#146133"))
                                        Glide.with(requireContext()).load(idImage)
                                            .into(binding.imageView)
                                        Log.d("TAG", "isLocationPermissionGrantedvjsdfnv:$idTwo")
                                        binding.tvCrops.text = idTwo.toString()
                                        binding.tvCrops.setTextColor(Color.parseColor("#5D6571"))

//                                        binding.tvCrops.setTextColor(Color.parseColor("#5D6571"))
                                        binding.cardCheckHealth.setOnClickListener {
                                            val bundle = Bundle()
                                            bundle.putInt(
                                                "soil_test_number",
                                                your_list?.get(0)?.onpId.toString().toInt()
                                            )
                                            bundle.putString("lat", latitude)
                                            bundle.putString("long", longitutde)
                                            bundle.putInt("plot_id", idOne.toString().toInt())
                                            findNavController().navigate(
                                                R.id.action_checkSoilTestFragment_to_newSoilTestFormFragment,
                                                bundle
                                            )

                                        }

                                    }
                                }
                                is Resource.Error -> {


                                }
                                is Resource.Loading -> {


                                }
                            }

                        }


                    }
                }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}