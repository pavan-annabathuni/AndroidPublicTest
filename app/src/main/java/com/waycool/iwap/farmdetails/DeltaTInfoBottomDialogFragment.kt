package com.waycool.iwap.farmdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.translations.TranslationsManager
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentDeltaTInfoBottomDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DeltaTInfoBottomDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentDeltaTInfoBottomDialogBinding.inflate(inflater)
        binding.closeDialog.setOnClickListener(){
            this.dismiss()
        }
        TranslationsManager().loadString("textView267", binding.textView267)
        TranslationsManager().loadString("textView268", binding.textView268)
        TranslationsManager().loadString("textView269", binding.textView269)
        TranslationsManager().loadString("textView270", binding.textView270)
        TranslationsManager().loadString("textView271", binding.textView271)
        TranslationsManager().loadString("declare", binding.declare)
        return binding.root
    }



    override fun getTheme(): Int {
        return com.waycool.uicomponents.R.style.AppBottomSheetDialogTheme
    }

}