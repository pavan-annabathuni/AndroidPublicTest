package com.example.addcrop.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentAddCropBinding
import com.example.addcrop.databinding.FragmentPlantSpacingBinding

import com.example.addcrop.viewmodel.AddViewModel

class PlantSpacingFragment : Fragment() {
    private var _binding: FragmentPlantSpacingBinding? = null
    private val binding get() = _binding!!
    var area: String? = null
    var date: String? = null
    var irrigation_selected: String? = null
    private val viewModel by lazy { ViewModelProvider(this)[AddViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantSpacingBinding.inflate(inflater, container, false)
        val bundle = Bundle()
        if (arguments != null)
            area = arguments?.getString("area")
        date = arguments?.getString("date")
        irrigation_selected = arguments?.getString("irrigation_selected")
        Log.d("TAG", "onCreateViewGetData: $area")
        Log.d("TAG", "onCreateViewGetData: $date")
        Log.d("TAG", "onCreateViewGetData: $irrigation_selected")
//        binding.cardCheckHealth.setOnClickListener {
//            val addCropRequest= AddCropRequest()
//            viewModel.addCropPassData(addCropRequest)
//
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }


    }


}