package com.waycool.iwap.premium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentDeviceOneBinding
import com.waycool.iwap.databinding.FragmentDeviceTwoBinding
import com.waycool.iwap.databinding.FragmentMyFarmBinding


class MyFarmFragment : Fragment() {
    private var _binding: FragmentMyFarmBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyFarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}