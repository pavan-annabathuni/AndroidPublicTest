package com.waycool.featurecropprotect.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.FragmentCropSelectionBinding
import com.waycool.featurecropprotect.databinding.FragmentTempBinding


class TempFragment : Fragment() {
    private var _binding: FragmentTempBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTempBinding.inflate(inflater, container, false)
        return binding.root
    }
}