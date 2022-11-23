package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.ForecastAdapter
import com.example.irrigationplanner.databinding.FragmentForecastBinding
import com.google.android.material.tabs.TabLayout


class ForecastFragment : Fragment() {
    private lateinit var binding: FragmentForecastBinding
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
        binding = FragmentForecastBinding.inflate(inflater)

        binding.recycleViewHis.adapter = ForecastAdapter()
        tabs()
        onclick()
        return binding.root
    }
    private fun tabs() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Mon").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tue").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Wed").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Thr").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Fri").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Sat").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Sun").setCustomView(R.layout.item_tab))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(binding.tabLayout.selectedTabPosition){
                    0-> {
                         Toast.makeText(context, "WORKED", Toast.LENGTH_SHORT).show()

                    }
                    1-> {
                         Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun onclick(){
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

}