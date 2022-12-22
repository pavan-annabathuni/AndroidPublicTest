package com.example.ndvi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ndvi.databinding.FragmentInfoSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding:FragmentInfoSheetBinding
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
       binding = FragmentInfoSheetBinding.inflate(inflater)
        binding.imgClose.setOnClickListener(){
            this.dismiss()
        }
        return  binding.root
    }
    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }
}