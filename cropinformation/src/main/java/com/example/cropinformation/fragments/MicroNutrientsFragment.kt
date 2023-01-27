package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentMicronutrientsBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class MicroNutrientsFragment : Fragment() {
    private lateinit var binding: FragmentMicronutrientsBinding
    private val ViewModel: TabViewModel by lazy {
        ViewModelProviders.of(this).get(TabViewModel::class.java)
    }
    private var cropId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cropId = it.getInt("CropId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMicronutrientsBinding.inflate(inflater)
        observer()
        return binding.root

    }
    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!
            for (i in 0..data.size-1) {
                if (data[i].label_name == "Micronutrients"||data[i].labelNameTag == "Micronutrients") {
                    binding.labelName.text = data[i].label_name
                    val str = data[i].label_value?.replace("<br>", System.getProperty("line.separator"))
                    binding.labelValue.text = str
                    break
                }
            }
        }}
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("MicroNutrientsFragment")
    }
}