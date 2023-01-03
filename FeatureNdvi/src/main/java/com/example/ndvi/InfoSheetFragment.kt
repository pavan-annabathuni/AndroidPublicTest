package com.example.ndvi

import android.os.Bundle
import androidx.fragment.app.Fragment
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
        TranslationsManager().loadString("str_satellite",binding.title)
        TranslationsManager().loadString("vegetation_index",binding.tvIndex)
        TranslationsManager().loadString("str_vegitation_info",binding.textView9)
        TranslationsManager().loadString("true_colour",binding.tvTrueColor)
        TranslationsManager().loadString("str_true_info",binding.tvTrueDes)
        TranslationsManager().loadString("str_disclaimer",binding.tvDis)
        TranslationsManager().loadString("str_disclaimer_details",binding.tvDisDesc)
    }
}