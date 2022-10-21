package com.example.profile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.profile.adapter.AddUseAdapter
import com.example.profile.databinding.FragmentFarmSupportBinding


class FarmSupportFragment : Fragment() {
 private lateinit var binding: FragmentFarmSupportBinding

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
        binding.recycleView.adapter = AddUseAdapter(requireContext())
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