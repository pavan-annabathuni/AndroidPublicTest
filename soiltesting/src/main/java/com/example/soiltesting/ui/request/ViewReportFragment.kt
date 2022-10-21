package com.example.soiltesting.ui.request

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentSucessFullBinding
import com.example.soiltesting.databinding.FragmentViewReportBinding


class ViewReportFragment : Fragment() {
    private var _binding: FragmentViewReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewReportBinding.inflate(inflater, container, false)
        return binding.root
    }


}