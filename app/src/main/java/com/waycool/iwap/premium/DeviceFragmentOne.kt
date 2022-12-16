package com.waycool.iwap.premium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentDeviceOneBinding
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding


class DeviceFragmentOne : Fragment() {
    private var _binding: FragmentDeviceOneBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceOneBinding.inflate(inflater, container, false)
        return binding.root
    }

}