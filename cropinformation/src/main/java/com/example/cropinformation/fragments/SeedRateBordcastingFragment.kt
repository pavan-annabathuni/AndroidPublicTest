package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentSeedRateBordcastingBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class SeedRateBordcastingFragment : Fragment() {

    private lateinit var binding: FragmentSeedRateBordcastingBinding
    private val viewModel: CropInfoViewModel by lazy {
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
        binding = FragmentSeedRateBordcastingBinding.inflate(inflater)
        viewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) {
            val data = it.data!!
            for (i in data.indices) {
                if (data[i].label_name == "Seed Rate (Broadcasting)"||data[i].labelNameTag == "Seed Rate (Broadcasting)") {
                    binding.tvSeedRate.text = data[i].label_value
                    binding.textView10.text = data[i].label_name

                }}}
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SeedFragment")
    }
    }
