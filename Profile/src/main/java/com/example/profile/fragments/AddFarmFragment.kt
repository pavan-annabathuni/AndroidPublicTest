package com.example.profile.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.profile.R
import com.example.profile.databinding.FragmentAddFarmBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class AddFarmFragment : Fragment() {
  private lateinit var binding: FragmentAddFarmBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
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
        binding = FragmentAddFarmBinding.inflate(inflater)
        onClick()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        location()
        binding.imgLocation.setOnClickListener(){
            location()
        }
        return binding.root

            }

    private fun onClick() {
        var roleid = 30
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
        binding.farmManger.setOnClickListener(){
            binding.farmManger.background = resources.getDrawable(R.drawable.text_border_gray)
            binding.mandiBench.background = resources.getDrawable(R.drawable.text_border)
            binding.image1.visibility = View.VISIBLE
            binding.image2.visibility = View.GONE
            roleid = 30
        }
        binding.mandiBench.setOnClickListener(){
            binding.mandiBench.background = resources.getDrawable(R.drawable.text_border_gray)
            binding.farmManger.background = resources.getDrawable(R.drawable.text_border)
            binding.image2.visibility = View.VISIBLE
            binding.image1.visibility = View.GONE
            roleid = 31
        }
        binding.submit.setOnClickListener() {

            val name = binding.tvName.text.toString()
            var contact = binding.mobilenoEt.text.toString().toLong()
            val lat2 = binding.tvLat.text
            var long2 = binding.tvLong.text

            if (name.isNullOrEmpty() || lat2.isNullOrEmpty() || long2.isNullOrEmpty()) {
                Toast.makeText(context, "Fill all Fields", Toast.LENGTH_SHORT).show()
            }
            else if(binding.mobilenoEt.text.toString()
                    .isEmpty() || binding.mobilenoEt.text.toString().length != 10){
                binding.mobileNo.error = "Enter Valid Mobile Number"
            }
                else {
                viewModel.updateFarmSupport(
                    name, contact, lat, long, roleid, pinCode,
                    village, address, state, district
                ).observe(viewLifecycleOwner) {
                    if(it.data?.status.toString()!="true") {
                        Toast.makeText(context, "Number already taken", Toast.LENGTH_SHORT).show()
                    }
                    else
                    findNavController().navigateUp()
                }
                // findNavController().navigateUp()
            }
        }
    }
    private fun location(){
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
//                Toast.makeText(context, "${it.longitude} ${it.latitude}", Toast.LENGTH_LONG).show()
                lat = String.format("%.6f",it.latitude).toDouble()
                long = String.format("%.6f",it.longitude).toDouble()
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
//                val bounds = RectangularBounds.newInstance(
//                    LatLng(-33.880490, it.latitude),
//                    LatLng(-33.858754,it.longitude)
//                )


        }

    }
}