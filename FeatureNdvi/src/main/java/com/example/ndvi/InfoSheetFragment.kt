package com.example.ndvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ndvi.databinding.FragmentInfoSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.translations.TranslationsManager

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
        translation()
        return  binding.root
    }
    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

    private fun translation() {
        TranslationsManager().loadString("str_satellite",binding.title,"Satellite Imagery Info")
        TranslationsManager().loadString("vegetation_index",binding.tvIndex,"Vegetation Index")
        TranslationsManager().loadString("str_vegitation_info",binding.textView9,getString(R.string.veg_info))
        TranslationsManager().loadString("true_colour",binding.tvTrueColor,"True Colour")
        TranslationsManager().loadString("str_true_info",binding.tvTrueDes)
        TranslationsManager().loadString("str_disclaimer",binding.tvDis,"Disclaimer")
        TranslationsManager().loadString("str_disclaimer_details",binding.tvDisDesc)
    }
}