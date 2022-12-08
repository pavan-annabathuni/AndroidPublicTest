package com.waycool.iwap.spotlight

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.addcrop.AddCropActivity
import com.waycool.data.Local.DataStorePref.DataStoreManager
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
                if (position != imageList.size) {
                    binding.next.setOnClickListener() {
                        binding.idViewPager.setCurrentItem(position + 1, true)
                    }}
                    if (position == imageList.size - 1) {
                        binding.next.setOnClickListener() {
                          lifecycleScope.launch(){
                              DataStoreManager.save("FirstTime","true")

                            this@SpotLightFragment.findNavController()
                                .navigate(R.id.action_spotLightFragment_to_homePagesFragment)
                        }}


                    }
                    binding.prev.setOnClickListener() {
                        binding.idViewPager.setCurrentItem(position - 1, true)
                    }
                    binding.skip.setOnClickListener(){

                            lifecycleScope.launch(){
                                DataStoreManager.save("FirstTime","true")

                            this@SpotLightFragment.findNavController()
                                .navigate(R.id.action_spotLightFragment_to_homePagesFragment)
                        }}
                }
        }
        binding.idViewPager.registerOnPageChangeCallback(myPageChangeCallback)

    }
}