package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentIrrigationTypeBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager

class IrrigationTypeFragment : Fragment() {
      private lateinit var binding: FragmentIrrigationTypeBinding
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
        binding = FragmentIrrigationTypeBinding.inflate(inflater)
        //ViewModel.cropAdvisory()
        observer()
        translation()
        return binding.root

    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!
            for(i in 0..data.size-1){
                if(data[i].label_name=="Irrigation Type"||data[i].labelNameTag=="Irrigation Type") {
                    binding.tvLabelName.text = data[i].label_name
                    val values = data[i].labelValueTag
                    val lstValues: List<String> = values?.split(",")!!.map { it -> it.trim() }
                    lstValues.forEach { itt ->
                        when (itt) {
                            "Flooding" -> {
                                binding.img1.visibility = View.VISIBLE
                                binding.ll1.setBackgroundResource(R.drawable.brownborder)
                                binding.tvLabelName

                            }
                            "Drip" -> {
                                binding.img2.visibility = View.VISIBLE
                                binding.ll2.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Sprinkler" -> {
                                binding.img3.visibility = View.VISIBLE
                                binding.ll3.setBackgroundResource(R.drawable.brownborder)

                            }
                            "Rain Gun" -> {
                                binding.img4.visibility = View.VISIBLE
                                binding.ll4.setBackgroundResource(R.drawable.brownborder)

                            }
                           }
                    }
                }}

        }
    }
    private fun translation(){
        TranslationsManager().loadString("str_floading",binding.floading)
        TranslationsManager().loadString("str_drip",binding.drip)
        TranslationsManager().loadString("str_sprinker",binding.sprinker)
        TranslationsManager().loadString("str_rain_gun",binding.rainGun)
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("IrrigationTypeFragment")
    }
}