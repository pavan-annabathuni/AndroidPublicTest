package com.example.cropinformation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentNextCropBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class NextCropFragment : Fragment() {
     private lateinit var binding:FragmentNextCropBinding
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
        binding = FragmentNextCropBinding.inflate(inflater)
        //ViewModel.cropAdvisory()
        observer()
        return binding.root

    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) {
            val data = it.data!!
            for (i in 0 until data.size) {
                if (data[i].label_name == "Proposed Next Crops"||data[i].labelNameTag == "Proposed Next Crops") {
                    binding.labelName.text = data[i].label_name
                    binding.labelValue.text = data[i].label_value
                    break
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NextCropFragment")
    }
}