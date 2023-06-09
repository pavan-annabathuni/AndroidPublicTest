package com.example.cropinformation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentSeedBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling


class SeedFragment : Fragment() {
    private lateinit var binding: FragmentSeedBinding
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
        binding = FragmentSeedBinding.inflate(inflater)
        binding.lifecycleOwner = this
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) {
            val data = it.data!!
            for (i in 0..data.size-1) {
                if (data[i].label_name == "Seed Rate (Line Sowing)"||data[i].labelNameTag == "Seed Rate (Line Sowing)") {
                    binding.tvSeedRate.text = data[i].label_value
                    binding.tvSeedRate2.text = data[i].label_value
                    binding.textView10.text = data[i].label_name

                }}}
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SeedFragment")
    }

}