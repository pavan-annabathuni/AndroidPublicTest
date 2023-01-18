package com.example.soiltesting.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentAllHistoryBinding
import com.example.soiltesting.databinding.FragmentAllVideoBinding
import com.waycool.data.eventscreentime.EventScreenTimeHandling


class AllVideoFragment : Fragment() {
    private var _binding: FragmentAllVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AllVideoFragment")
    }

}