package com.example.soiltesting.ui.request

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentSucessFullBinding
import com.example.soiltesting.utils.Constant.TAG


class SucessFullFragment : Fragment() {
    private var _binding: FragmentSucessFullBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSucessFullBinding.inflate(inflater, container, false)
//        if (arguments!=null) {
            val soilTestNumber = arguments?.getString("soil_test_number")
            Log.d(TAG, "onCreateViewSoilTestNumber: $soilTestNumber ")
            binding.tvRequestIDText.text = "Your soil test request for $soilTestNumber"

            binding.ivClose.setOnClickListener {
               findNavController().navigateUp()
//                findNavController().navigate(R.id.action_sucessFullFragment_to_soilTestingHomeFragment)
//                val isSuccess = findNavController().navigateUp()
//                if (!isSuccess) requireActivity().finish()
                findNavController().navigateUp()
//            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}