package com.example.cropinformation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentRatooningBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling


class RatooningFragment : Fragment() {
    private lateinit var binding:FragmentRatooningBinding
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
        binding = FragmentRatooningBinding.inflate(inflater)
      //  ViewModel.cropAdvisory()
        observer()
        return binding.root

    }
        private fun observer() {
            ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
                val data = it.data!!
                for (i in 0 until data.size) {
                    if (data[i].label_name == "Ratooning"||data[i].labelNameTag == "Ratooning") {
                        binding.labelName.text = data[i].label_name
                        binding.labelValue.text = data[i].label_value
                        break
                    }
                }
            }}
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("RatooningFragment")
    }
}