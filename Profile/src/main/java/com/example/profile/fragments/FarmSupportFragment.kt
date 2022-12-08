package com.example.profile.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.profile.R
import com.example.profile.adapter.AddUseAdapter
import com.example.profile.databinding.FragmentFarmSupportBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.math.log


class FarmSupportFragment : Fragment() {
 private lateinit var binding: FragmentFarmSupportBinding
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
        binding = FragmentFarmSupportBinding.inflate(inflater)
        binding.recycleView.adapter = AddUseAdapter(AddUseAdapter.OnClickListener {
                   val dialog = Dialog(requireContext())

       dialog.setCancelable(false)
       dialog.setContentView(R.layout.dailog_delete)
      // val body = dialog.findViewById(R.id.body) as TextView
       val yesBtn = dialog.findViewById(R.id.cancel) as Button
       val noBtn = dialog.findViewById(R.id.delete) as Button
       yesBtn.setOnClickListener {
           dialog.dismiss()
       }
       noBtn.setOnClickListener { dialog.dismiss() }
       dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       dialog.show()
        })
        Log.d("Clicked", "onCreateView: ciclked")
        onClick()
        return binding.root
    }

    private fun onClick() {
        binding.addUser.setOnClickListener() {
            this.findNavController().navigate(FarmSupportFragmentDirections.actionFarmSupportFragmentToAddFarmFragment())
        }
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }
    }