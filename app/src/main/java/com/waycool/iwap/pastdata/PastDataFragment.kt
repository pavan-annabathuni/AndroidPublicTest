package com.waycool.iwap.pastdata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentPastDataBinding

class PastDataFragment : Fragment() {

    private val binding:FragmentPastDataBinding by lazy { FragmentPastDataBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }
}