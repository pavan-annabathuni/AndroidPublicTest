package com.example.cropinformation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentSoilPhBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager

class SoilPhFragment : Fragment() {

     private lateinit var binding:FragmentSoilPhBinding
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
        binding = FragmentSoilPhBinding.inflate(inflater)

        binding.slider.setCustomThumbDrawable(R.drawable.ic_indicator)


        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!

                for(i in 0..data.size-1){
                if(data[i].label_name=="Soil pH"||data[i].labelNameTag=="Soil pH") {
                    binding.tvLabelValue.text = data[i].label_value
                    binding.tvLabelName.text = data[i].label_name
                    val values = data[i].labelValueTag
                    val lstValues:List<String> = values?.split("-")!!.map { it -> it.trim() }
                    binding.slider.value = lstValues[0].toFloat()
                    if(lstValues[0]< 6.toString() &&lstValues[1]> 7.toString()){
                    }
                    Log.d("slider", "onCreateView: $lstValues")


        }}
        }
        TranslationsManager().loadString("str_plant_nutrient",binding.textView3)
        TranslationsManager().loadString("str_acidic",binding.tvAcidic)
        TranslationsManager().loadString("str_neutral",binding.tvNeutral)
        TranslationsManager().loadString("str_alkaline",binding.tvAlkaline)
        return binding.root


    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SoilPhFragment")
    }

}