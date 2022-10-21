package com.example.soiltesting.ui.request

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.soiltesting.databinding.FragmentSucessFullBinding
import com.example.soiltesting.utils.Constant.TAG


class SucessFullFragment : Fragment() {
    private var _binding: FragmentSucessFullBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSucessFullBinding.inflate(inflater, container, false)
        if (arguments!=null) {
            var soil_test_number = arguments?.getString("soil_test_number")
            Log.d(TAG, "onCreateViewSoilTestNumber: $soil_test_number ")

            binding.tvRequestIDText.text =
                "Your soil test request for " + soil_test_number.toString()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}