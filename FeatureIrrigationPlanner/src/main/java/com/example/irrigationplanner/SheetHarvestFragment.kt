package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.irrigationplanner.databinding.FragmentSheetHarvestBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SheetHarvestFragment : BottomSheetDialogFragment() {
   private lateinit var binding:FragmentSheetHarvestBinding
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
        binding = FragmentSheetHarvestBinding.inflate(inflater)
        binding.close.setOnClickListener(){
            this.dismiss()
        }
        return binding.root
    }
    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }
}