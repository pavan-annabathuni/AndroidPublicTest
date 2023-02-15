package com.waycool.iwap.spotlight

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentSpotLightBinding
import kotlinx.coroutines.launch


class SpotLightFragment : Fragment() {
    private lateinit var binding: FragmentSpotLightBinding
    lateinit var viewPagerAdapter: SpotLightAdapter
    lateinit var imageList: List<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpotLightBinding.inflate(inflater)
        imageList = ArrayList<Int>()
        imageList = imageList + R.drawable.image1
        imageList = imageList + R.drawable.image3
        imageList = imageList + R.drawable.image4
        imageList = imageList + R.drawable.image5
        imageList = imageList + R.drawable.image6
        imageList = imageList + R.drawable.image2

        viewPagerAdapter = SpotLightAdapter(requireContext(), imageList)
        viewPagerAdapter
        viewPager()
        return binding.root

    }

    fun viewPager() {
        val viewpager = binding.idViewPager
        viewpager.adapter = viewPagerAdapter
        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != imageList.size-1) {
                    binding.next.setOnClickListener {
                        binding.idViewPager.setCurrentItem(position + 1, true)
                    }
                    binding.next2.setOnClickListener {
                        binding.idViewPager.setCurrentItem(position + 1, true)
                    }
                }
                if(position == imageList.size-1){
                    binding.next.setOnClickListener {
                    lifecycleScope.launch {
                        DataStoreManager.save("FirstTime", "true")
                        this@SpotLightFragment.findNavController().popBackStack()
//                            .navigate(R.id.action_spotLightFragment_to_homePagesFragment)
                    }}
                        binding.next2.setOnClickListener {
                            lifecycleScope.launch {
                                DataStoreManager.save("FirstTime", "true")
                                this@SpotLightFragment.findNavController().popBackStack()
//                            .navigate(R.id.action_spotLightFragment_to_homePagesFragment)
                            }
                    Log.d("sportLight", "onPageSelected: ${imageList.size},$position")
                }}
                if (position == imageList.size) {
                    binding.next.setOnClickListener {
                        when(position){
                            0-> EventClickHandling.calculateClickEvent("Next(1/5)")
                            1-> EventClickHandling.calculateClickEvent("Next(2/5)")
                            2-> EventClickHandling.calculateClickEvent("Next(3/5)")
                            3-> EventClickHandling.calculateClickEvent("Next(4/5)")
                            4-> EventClickHandling.calculateClickEvent("Next(5/5)")
                            5-> EventClickHandling.calculateClickEvent("Done(6/6)")
                        }}
//                             .navigate(R.id.action_spotLightFragment_to_homePagesFragment)


                }
                binding.prev.setOnClickListener {
                    binding.idViewPager.setCurrentItem(position - 1, true)
                }
                binding.skip.setOnClickListener {
                    when(position){
                        0-> EventClickHandling.calculateClickEvent("Skip(1/5))")
                        1-> EventClickHandling.calculateClickEvent("Skip(2/5)")
                        2-> EventClickHandling.calculateClickEvent("Skip(3/5)")
                        3-> EventClickHandling.calculateClickEvent("Skip(4/5)")
                        4-> EventClickHandling.calculateClickEvent("Skip(5/5))")
                        5-> EventClickHandling.calculateClickEvent("Skip(6/5)")
                    }
                    lifecycleScope.launch {
                        DataStoreManager.save("FirstTime", "true")
                        this@SpotLightFragment.findNavController().popBackStack()
//                            .navigate(R.id.action_spotLightFragment_to_homePagesFragment)
                    }
                }
            }
        }
        binding.idViewPager.registerOnPageChangeCallback(myPageChangeCallback)

    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SpotLightFragment")
    }
}