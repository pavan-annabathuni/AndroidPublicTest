package com.example.cropinformation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentSeedBinding
import com.example.cropinformation.databinding.FragmentSeedRateBinding
import com.example.cropinformation.viewModle.TabViewModel


class SeedFragment : Fragment() {
    private lateinit var binding: FragmentSeedBinding
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
        binding = FragmentSeedBinding.inflate(inflater)
        binding.lifecycleOwner = this
        ViewModel.cropAdvisory()
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

}