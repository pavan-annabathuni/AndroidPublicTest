package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentPlantToPlantBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.waycool.data.translations.TranslationsManager

class PlantToPlantFragment : Fragment() {
    private lateinit var binding: FragmentPlantToPlantBinding
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
        binding = FragmentPlantToPlantBinding.inflate(inflater)
       // ViewModel.cropAdvisory()
        observer()
        TranslationsManager().loadString("str_spacing",binding.labelName)
        return binding.root

    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!
            for (i in 0 until data.size) {
                if (data[i].label_name == "Spacing between Plant to Plant"||data[i].labelNameTag == "Spacing between Plant to Plant") {
                    binding.tvRow.text = data[i].label_name
                    binding.labelValue2.text = data[i].label_value
                    binding.labelValue4.text = data[i].label_value
                    break
                }
        }
    }}
}