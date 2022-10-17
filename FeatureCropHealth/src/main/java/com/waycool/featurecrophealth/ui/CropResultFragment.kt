package com.waycool.featurecrophealth.ui

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waycool.featurecrophealth.databinding.FragmentCropResultBinding


class CropResultFragment : Fragment() {

    private var _binding: FragmentCropResultBinding? = null
    private val binding get() = _binding!!
//    var tabLayout: TabLayout? = null
//    var viewPager: ViewPager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCropResultBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.tabsMain.addTab(binding.tabsMain.newTab().setText("Chemical"))
//        binding.tabsMain.addTab(binding.tabsMain.newTab().setText("Biological"))
//        binding.tabsMain.addTab(binding.tabsMain.newTab().setText("Movie"))
////
////        val adapter = MyAdapter(requireContext(),, tabLayout!!.tabCount)
////        viewPager!!.adapter = adapter
//        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
//
//        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                viewPager!!.currentItem = tab.position
//            }
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//
//            }
//            override fun onTabReselected(tab: TabLayout.Tab) {
//
//            }
//        })
//    }

}