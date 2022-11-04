package com.example.cropinformation.fragments

import android.os.Bundle
import android.text.TextUtils.split
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentSoilPhBinding
import com.example.cropinformation.utils.Constants
import com.example.cropinformation.viewModle.TabViewModel

class SoilPhFragment : Fragment() {

     private lateinit var binding:FragmentSoilPhBinding
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
        binding = FragmentSoilPhBinding.inflate(inflater)
        ViewModel.cropAdvisory()

        binding.slider.setCustomThumbDrawable(R.drawable.ic_indicator)


        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!

                for(i in 0..data.size-1){
                if(data[i].label_name=="Soil pH") {
                    binding.tvLabelValue.text = data[i].label_value
                    binding.tvLabelName.text = data[i].label_name
                    val values = data[i].label_value
                    val lstValues:List<String> = values?.split("-")!!.map { it -> it.trim() }
                    binding.slider.value = lstValues[0].toFloat()
                    if(lstValues[0]< 6.toString() &&lstValues[1]> 7.toString()){
                    }
                    Log.d("slider", "onCreateView: $lstValues")


        }}
        }
        return binding.root


    }

}