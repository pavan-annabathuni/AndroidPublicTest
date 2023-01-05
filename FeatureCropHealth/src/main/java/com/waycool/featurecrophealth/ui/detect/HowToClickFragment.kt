package com.waycool.featurecrophealth.ui.detect

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentHowToClickBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.translations.TranslationsManager


open class HowToClickFragment() :  BottomSheetDialogFragment() {

    private var _binding: FragmentHowToClickBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHowToClickBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvclose.setOnClickListener {
            dismiss()
        }
        translationSoilTesting()
    }
    fun translationSoilTesting() {
        TranslationsManager().loadString("how_to_capture", binding.textView285)
        TranslationsManager().loadString("capture_info_1", binding.imageOne)
        TranslationsManager().loadString("capture_info_2", binding.imageTwo)
        TranslationsManager().loadString("capture_info_3", binding.imageThree)
        TranslationsManager().loadString("capture_info_4", binding.imageFour)

    }

}