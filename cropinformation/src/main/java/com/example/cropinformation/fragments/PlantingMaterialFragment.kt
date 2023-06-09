package com.example.cropinformation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentPlantingMaterialBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager

class PlantingMaterialFragment : Fragment() {

    private lateinit var binding:FragmentPlantingMaterialBinding
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
        binding = FragmentPlantingMaterialBinding.inflate(inflater)
       // ViewModel.cropAdvisory()
        observer()
        translation()
        return binding.root
    }

    private fun observer() {
        var lang:String
        ViewModel.getUserDetails().observe(viewLifecycleOwner){
            lang = it.data?.profile?.langCode.toString()

        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){

            val data = it.data!!
            for(i in 0 until data.size){
                if(data[i].label_name=="Planting Material"||data[i].labelNameTag=="Planting Material") {
                    binding.textView7.text = data[i].label_name
                    var values:String
                    if(lang!="en") {
                         values = data[i].labelValueTag.toString()
                        Log.d("cropInfo", "observer: $values")
                    }else{
                        values = data[i].labelValueTag.toString()
                    }
                    val lstValues: List<String> = values?.split(",")!!.map { it -> it.trim() }
                    lstValues.forEach { itt ->
                        when (itt) {
                            "Seed" -> {
                                binding.imgRight1.visibility = View.VISIBLE
                                binding.ll1.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Tuber" -> {
                                binding.imgRight2.visibility = View.VISIBLE
                                binding.ll2.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Grafting" -> {
                                binding.img3.visibility = View.VISIBLE
                                binding.ll3.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Semi Hardwood" -> {
                                binding.img4.visibility = View.VISIBLE
                                binding.ll4.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Setts" -> {
                                binding.img5.visibility = View.VISIBLE
                                binding.ll5.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Ring Budding" -> {
                                binding.img6.visibility = View.VISIBLE
                                binding.ll6.setBackgroundResource(R.drawable.brownborder)

                            }
                        }
                    }
                }

        }
        }
    }}
    private fun translation(){
        TranslationsManager().loadString("str_seed",binding.tvLabelValue,)
        TranslationsManager().loadString("str_tuber",binding.tvLabelValue2)
        TranslationsManager().loadString("str_grafting",binding.label3)
        TranslationsManager().loadString("str_semi_hardwood",binding.label4)
        TranslationsManager().loadString("str_setts",binding.label5)
        TranslationsManager().loadString("str_ring",binding.label6)
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("PlantingMaterialFragment")
    }
}