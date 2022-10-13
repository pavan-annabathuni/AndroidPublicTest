package com.waycool.squarecamera

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.squarecamera.databinding.FragmentDialogBinding


class DialogFragment : BottomSheetDialogFragment()  {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = com.waycool.uicomponents.R.style.AppBottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding. close.setOnClickListener {
//            dismiss()
//        }
//    }


}