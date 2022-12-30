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

    val binding:FragmentDeltaTInfoBottomDialogBinding by lazy { FragmentDeltaTInfoBottomDialogBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delta_t_info_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeDialog.setOnClickListener { findNavController().navigateUp() }
    }

    override fun getTheme(): Int {
        return com.waycool.uicomponents.R.style.AppBottomSheetDialogTheme
    }

}