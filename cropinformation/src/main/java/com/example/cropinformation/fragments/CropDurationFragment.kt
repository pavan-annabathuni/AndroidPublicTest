package com.example.cropinformation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentCropDurationBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class CropDurationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding:FragmentCropDurationBinding
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
        binding = FragmentCropDurationBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModel
        //ViewModel.cropAdvisory()
//        ViewModel.response3.observe(viewLifecycleOwner){
//            val data = it.filter { it -> it.crop_id==CROP_ID}
//            for(i in 0..data.size){
//                if(data[i].label_name=="Crop Duration") {
//                    binding.tvDays.text = data[i].label_value
//                    binding.tvDays2.text = data[i].label_value
//                }
//        }}
        ViewModel.getCropInformationDetails(cropId!!).observe(requireActivity()){
           // val data = it.filter { it -> it.crop_id==CROP_ID}
            val data = it.data
            for(i in 0 until data!!.size){
                if(data[i].label_name=="Crop Duration"||data[i].labelNameTag=="Crop Duration") {
                    binding.tvDays.text = data[i].label_value
                    binding.tvDays2.text = data[i].label_value
                    binding.textView8.text = data[i].label_name
                    break
                }
            }
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropDurationFragment")
    }
}