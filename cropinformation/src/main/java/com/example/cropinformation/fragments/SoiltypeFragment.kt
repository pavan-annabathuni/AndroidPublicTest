package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentSoiltypeBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager


class SoiltypeFragment : Fragment() {
    private lateinit var binding: FragmentSoiltypeBinding
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
        // Inflate the layout for this fragme
        binding = FragmentSoiltypeBinding.inflate(inflater)
        //ViewModel.cropAdvisory()
        observer()
        translation()
        return binding.root
    }

    private fun observer() {
        var lang = "en"
        ViewModel.getUserDetails().observe(viewLifecycleOwner){
            lang = it.data?.profile?.langCode.toString()

        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) {
           // val data = it.filter { it -> it.crop_id== Constants.CROP_ID }
            val data = it.data
            for(i in 0 until data!!.size){

                if(data[i].label_name=="Soil Type"||data[i].labelNameTag=="Soil Type") {

                      val  values = data[i].labelValueTag

                    binding.textView5.text = data[i].label_name
                    //val values = data[i].labelValueTag
                    val lstValues: List<String> = values!!.split(",").map { it -> it.trim() }
                    lstValues.forEach { itt ->
            when (itt) {
                "Sandy loam" -> {
                    binding.imgRight1.visibility = View.VISIBLE
                    binding.ll1.setBackgroundResource(R.drawable.brownborder)

                }
                "Loam" -> {
                    binding.imgRight2.visibility = View.VISIBLE
                    binding.ll2.setBackgroundResource(R.drawable.brownborder)

                }
                "Stil loam" -> {
                    binding.img3.visibility = View.VISIBLE
                    binding.ll3.setBackgroundResource(R.drawable.brownborder)

                }
                "Clay loam" -> {
                    binding.img4.visibility = View.VISIBLE
                    binding.ll4.setBackgroundResource(R.drawable.brownborder)

                }
                "Alluvial" -> {
                    binding.img5.visibility = View.VISIBLE
                    binding.ll5.setBackgroundResource(R.drawable.brownborder)

                }}
            } }
           }

        }
    }}
    private fun translation(){
        TranslationsManager().loadString("str_sandy_loam",binding.tvLabelValue)
        TranslationsManager().loadString("str_loam",binding.tvLabelValue2)
        TranslationsManager().loadString("str_still_loam",binding.tvStill)
        TranslationsManager().loadString("clay_loam",binding.tvClay)
        TranslationsManager().loadString("str_alluvial",binding.tvAllu)
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SoilTypeFragment")
    }
}
