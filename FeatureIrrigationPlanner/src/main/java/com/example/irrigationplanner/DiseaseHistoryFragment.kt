package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.DiseaseHistoryAdapter
import com.example.irrigationplanner.databinding.FragmentDisaseHistoryBinding

class DiseaseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentDisaseHistoryBinding
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
        binding = FragmentDisaseHistoryBinding.inflate(inflater)
        binding.recycleViewHis.adapter = DiseaseHistoryAdapter()
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
        return binding.root
    }

}