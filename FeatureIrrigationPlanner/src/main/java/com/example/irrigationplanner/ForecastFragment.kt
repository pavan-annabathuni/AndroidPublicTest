package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.ForecastAdapter
import com.example.irrigationplanner.databinding.FragmentForecastBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.util.*


class ForecastFragment : Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private lateinit var mForecastAdapter: ForecastAdapter
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
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

        viewModelData(0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mForecastAdapter = ForecastAdapter()
        binding.recycleViewHis.adapter = mForecastAdapter
        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(477,1).observe(viewLifecycleOwner){
                it.data?.data?.irrigation?.irrigationForecast?.let { it1 -> mForecastAdapter.setList(it1) }
            }}

        tabs()
        onclick()
    }
    private fun tabs() {
  viewModel.viewModelScope.launch{
      viewModel.getIrrigationHis(477,1).observe(viewLifecycleOwner){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(0)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(1)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(2)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(3)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(4)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(5)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.data?.data?.irrigation!!.irrigationForecast?.days?.get(6)).setCustomView(R.layout.item_tab_irrigation))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(binding.tabLayout.selectedTabPosition){
                    0-> viewModelData(0)
                    1-> viewModelData(1)
                    2-> viewModelData(2)
                    3-> viewModelData(3)
                    4-> viewModelData(4)
                    5-> viewModelData(5)
                    6-> viewModelData(6)
                    7-> viewModelData(7)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }}}

    private fun onclick(){
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

    private fun viewModelData(i:Int){
        var area:Int =0
        var areaPerPlant:Float = 0.00f
        viewModel.getMyCrop2().observe(viewLifecycleOwner){
            area = it.data?.get(0)?.area?.toInt() ?: 0
            val lenght = it.data?.get(0)?.lenDrip?.toFloat()
            val width = it.data?.get(0)?.widthDrip?.toFloat()?:0f
            if (lenght != null) {
                areaPerPlant = lenght * width
            }
        }
        viewModel.viewModelScope.launch(){
            viewModel.getIrrigationHis(477,1).observe(viewLifecycleOwner){
                val dep = it.data?.data?.irrigation?.irrigationForecast?.depletion?.get(i).toString().toFloat()
                binding.tvEtc.text = "${it.data?.data?.irrigation?.irrigationForecast?.etc?.get(i)} mm" ?: ""
                binding.tvEto.text = "${(it.data?.data?.irrigation?.irrigationForecast?.eto?.get(i))} mm"
                binding.tvMm.text = "${it.data?.data?.irrigation?.irrigationForecast?.depletion?.get(i)} mm"?:""
                binding.tvAcres.setText(String.format(Locale.ENGLISH, "%.0f", dep * 4046.86 * area / 0.9) + " L")
                if(areaPerPlant<=0){
                binding.tvPerPlant.visibility = View.INVISIBLE
                    binding.textView25.visibility = View.INVISIBLE
                }
                else {
                    binding.tvPerPlant.text = areaPerPlant.toString()
                    binding.tvPerPlant.visibility = View.VISIBLE
                    binding.textView25.visibility = View.INVISIBLE
                }

               val  properties = it.data?.data?.irrigation?.irrigationForecast
                if (properties!!.mad[i] == 0) {
                    val value = 30 - properties.depletion[i].toFloat()
                    if (value <= 0) {
                        binding.irrigationReq.text = "Irrigation Required"
                    } else {
                        val value = 30 - properties.depletion[i].toFloat()
                        val percentage = (value / 30) * 100
                        binding.irrigationReq.text = "Irrigation Not Required"
                    }
                } else {
                    val value = properties.mad[i] - properties.depletion[i].toFloat()
                    if (value <= 0) {
                        binding.irrigationReq.text = "Irrigation Required"
                    } else {
                        val value = properties.mad[i] - properties.depletion[i].toFloat()
                        val percentage = (value / properties.mad[i]) * 100
                        binding.irrigationReq.text = "Irrigation Not Required"
                    }
//        if(level<=0)
//        holder.waterLevel.progress = level
                }
            }
        }
    }


}