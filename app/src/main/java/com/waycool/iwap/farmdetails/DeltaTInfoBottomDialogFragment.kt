package com.waycool.iwap.farmdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentDeltaTInfoBottomDialogBinding


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
        return binding.root
    }


    override fun getTheme(): Int {
        return com.waycool.uicomponents.R.style.AppBottomSheetDialogTheme
    }

}