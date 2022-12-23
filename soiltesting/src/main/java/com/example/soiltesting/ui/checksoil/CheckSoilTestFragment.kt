package com.example.soiltesting.ui.checksoil

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentCheckSoilTestBinding
import com.example.soiltesting.ui.history.HistoryViewModel
import com.example.soiltesting.utils.Constant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import com.waycool.data.utils.Resource
import java.util.*
import kotlin.collections.ArrayList

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
            var your_list = arguments?.getParcelableArrayList<CheckSoilTestDomain>("list")
            Log.d("TAG", "onCreateViewGettingList: ${your_list.toString()}")
            binding.tvLabTitle.text = your_list?.get(0)?.onpName.toString()
            binding.tvName.text = your_list?.get(0)?.onpAddress.toString()
            binding.tvCheckCrop.text =
                your_list?.get(0)?.onpDistanceKm.toString() + " from your location"
            binding.pinCode.text = your_list?.get(0)?.onpPincode.toString()
            Log.d(Constant.TAG, "jdbfvdsghugifjdha$your_list?.get(0)?.onpName .toString()")
            binding.btnSelectCrop.setOnClickListener {
                findNavController().navigate(R.id.action_checkSoilTestFragment_to_selectCropSoilTestFragment)
            }
            binding.editImage.setOnClickListener {
                findNavController().navigate(R.id.action_checkSoilTestFragment_to_selectCropSoilTestFragment)
            }
            latitude = arguments?.getString("lat")
            longitude = arguments?.getString("lon")
            binding.cardCheckHealth.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("onp_id", your_list?.get(0)?.onpName.toString())
//            Log.d(Constant.TAG, "initViewsendingId: "+your_list[0]..toString())
                findNavController().navigate(
                    R.id.action_checkSoilTestFragment_to_newSoilTestFormFragment,
                    bundle
                )

            }
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

    }


    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
//            soilTestingLabsAdapter.upDateList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            // use your location object
            Log.d("checkLocation", "isLocationPermissionGranted:1 ")
            false
        } else {
            Log.d("checkLocation", "isLocationPermissionGranted:2 ")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && account_id != null) {

                        val latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)

                        Log.d("TAG", "isLocationPermissionGrantedhdcbhjbdj:$latitude ")
                        Log.d("TAG", "isLocationPermissionGrantedhdcbhjbdj:$longitutde ")

                        viewModel.getCheckSoilTestLab(
                            account_id,
                            latitude,
                            longitutde
                        ).observe(requireActivity()) {
                            when (it) {
                                is Resource.Success -> {
                                    var your_list = it.data
//                                    Log.d("TAG", "onCreateViewGettingList: ${your_list.toString()}")
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

                                    if (idTwo.isNullOrEmpty()

                                    ) {
                                        binding.btnSelectCrop.visibility = View.VISIBLE
//                                        binding.selectCropEdit.visibility=View.GONE
//                                        binding.imageView.visibility=View.GONE
//                                        binding.tvCrops.visibility=View.GONE
                                    } else {
                                        binding.imageView.visibility = View.VISIBLE
//                                        binding.tvCrops.visibility = View.VISIBLE
                                        binding.editImage.visibility = View.VISIBLE
                                        binding.cardCheckHealth.isEnabled = true
                                        binding.editImage.visibility = View.VISIBLE
                                        binding.btnSelectCrop.visibility = View.GONE
//                                        binding.selectCropEdit.visibility=View.VISIBLE
                                        binding.selectCrop.text = "Selected Crop"
                                        binding.selectCropEdit.setTextColor(Color.parseColor("#146133"))
                                        Glide.with(requireContext()).load(idImage)
                                            .into(binding.imageView)
                                        binding.tvCrops.text = idTwo
                                        binding.tvCrops.setTextColor(Color.parseColor("#5D6571"))
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

}