package com.example.cropinformation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentFertilizersBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class FertilizersFragment : Fragment() {
    private lateinit var binding: FragmentFertilizersBinding
    private val ViewModel: CropInfoViewModel by lazy {
        ViewModelProviders.of(this).get(CropInfoViewModel::class.java)
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
        binding = FragmentFertilizersBinding.inflate(inflater)
        observer()
        return binding.root

    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data
            for(i in 0..data!!.size-1){
                if(data[i].label_name=="Fertilizers"||data[i].labelNameTag=="Fertilizers") {
            binding.labelName.text = data[i].label_name
          val str = data[i].label_value?.replace("<br>", System.getProperty("line.separator"))
            binding.labelValue.text = str
                    break
        }
    }
}}
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("FertilizersFragment")
    }
}