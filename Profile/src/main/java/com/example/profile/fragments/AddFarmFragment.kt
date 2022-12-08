package com.example.profile.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
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
        return binding.root

            }

    private fun onClick() {
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
        binding.farmManger.setOnClickListener(){
            binding.farmManger.background = resources.getDrawable(R.drawable.text_border)
            binding.mandiBench.setBackgroundColor(resources.getColor(R.color.white))
            binding.image1.visibility = View.VISIBLE
            binding.image2.visibility = View.GONE
        }
        binding.mandiBench.setOnClickListener(){
            binding.mandiBench.background = resources.getDrawable(R.drawable.text_border)
            binding.farmManger.setBackgroundColor(resources.getColor(R.color.white))
            binding.image2.visibility = View.VISIBLE
            binding.image1.visibility = View.GONE
        }
        binding.submit.setOnClickListener(){
            location()
            findNavController().navigateUp()
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
                Toast.makeText(context, "${it.longitude} ${it.latitude}", Toast.LENGTH_LONG).show()

                viewModel.getReverseGeocode("${it.latitude},${it.longitude}")
                    .observe(viewLifecycleOwner) {
                        if (it.results.isNotEmpty()) {
                            val result = it.results[0]
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