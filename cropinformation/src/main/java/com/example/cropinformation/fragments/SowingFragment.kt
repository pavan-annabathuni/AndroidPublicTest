package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentSowingBinding
import com.example.cropinformation.utils.Constants
import com.example.cropinformation.viewModle.TabViewModel

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
        return binding.root
    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!
            for(i in 0..data.size-1){
                if(data[i].label_name=="Sowing Season") {
            val values = data[i].label_value
            val lstValues: List<String> = values?.split(",")!!.map { it -> it.trim() }
            lstValues.forEach { itt ->
                when (itt) {
                    "Jan" -> binding.jan.setBackgroundResource(R.drawable.brown_circle_border)
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

}