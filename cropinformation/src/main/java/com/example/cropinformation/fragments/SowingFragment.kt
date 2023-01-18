package com.example.cropinformation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentSowingBinding
import com.example.cropinformation.utils.Constants
import com.example.cropinformation.viewModle.TabViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager

class SowingFragment : Fragment() {
       private lateinit var binding:FragmentSowingBinding
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
        binding = FragmentSowingBinding.inflate(inflater)
        ViewModel.cropAdvisory()
        observer()
        translation()
        return binding.root
    }

    private fun observer() {
        var lang = "en"
        ViewModel.getUserDetails().observe(viewLifecycleOwner){
            lang= it.data?.profile?.langCode.toString()
        }
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!
            for(i in 0..data.size-1){
                if(data[i].label_name=="Sowing Season"||data[i].labelNameTag=="Sowing Season") {
                   binding.tvLabelName.text = data[i].label_name
                    val  values = data[i].labelValueTag


            val lstValues: List<String> = values?.split(",")!!.map { it -> it.trim() }
            lstValues.forEach { itt ->
                when (itt) {
                    "Jan" ->binding.jan.setBackgroundResource(R.drawable.brown_circle_border)
                    "Feb" -> binding.tvFeb.setBackgroundResource(R.drawable.brown_circle_border)
                    "Mar" -> binding.Mar.setBackgroundResource(R.drawable.brown_circle_border)
                    "Apr" -> binding.Apr.setBackgroundResource(R.drawable.brown_circle_border)
                    "May" -> binding.may.setBackgroundResource(R.drawable.brown_circle_border)
                    "Jun" -> binding.jun.setBackgroundResource(R.drawable.brown_circle_border)
                    "Jul" -> binding.jul.setBackgroundResource(R.drawable.brown_circle_border)
                    "Aug" -> binding.tvAug.setBackgroundResource(R.drawable.brown_circle_border)
                    "Sep" -> binding.sep.setBackgroundResource(R.drawable.brown_circle_border)
                    "Oct" -> binding.oct.setBackgroundResource(R.drawable.brown_circle_border)
                    "Nov" -> binding.Nov.setBackgroundResource(R.drawable.brown_circle_border)
                    "Dec" -> binding.Dec.setBackgroundResource(R.drawable.brown_circle_border)
                }
            }
                }

                }
            }
    }

    private fun translation(){
        TranslationsManager().loadString("str_jan",binding.jan,"Jan")
        TranslationsManager().loadString("str_feb",binding.tvFeb,"Feb")
        TranslationsManager().loadString("str_mar",binding.Mar,"Mar")
        TranslationsManager().loadString("str_apr",binding.Apr,"Apr")
        TranslationsManager().loadString("str_may",binding.may,"May")
        TranslationsManager().loadString("str_jun",binding.jun,"Jun")
        TranslationsManager().loadString("str_jul",binding.jul,"Jul")
        TranslationsManager().loadString("str_aug",binding.tvAug,"Aug")
        TranslationsManager().loadString("str_sep",binding.sep,"Sep")
        TranslationsManager().loadString("str_oct",binding.oct,"Oct")
        TranslationsManager().loadString("str_nov",binding.Nov,"Nov")
        TranslationsManager().loadString("str_dec",binding.Dec,"Dec")
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SowingFragment")
    }
}