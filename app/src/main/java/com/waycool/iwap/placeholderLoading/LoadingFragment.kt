package com.waycool.iwap.placeholderLoading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.iwap.R

class LoadingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(com.waycool.uicomponents.R.raw.loading)
            .into(view.findViewById(R.id.loading_gif))
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("LoadingFragment")
    }

}