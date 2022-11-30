package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.HistoryDetailAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.example.irrigationplanner.databinding.FragmentIrrigationHistoryBinding


class IrrigationHistoryFragment : Fragment() {
    private lateinit var binding:FragmentIrrigationHistoryBinding

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
        binding = FragmentIrrigationHistoryBinding.inflate(inflater)
        binding.recycleViewHis.adapter = HistoryDetailAdapter()
        onClick()
        return binding.root
    }
    private fun onClick(){
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

}