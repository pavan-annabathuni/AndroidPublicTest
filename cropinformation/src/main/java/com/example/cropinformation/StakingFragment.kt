package com.example.cropinformation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentCropInfoBinding
import com.example.cropinformation.databinding.FragmentNextCropBinding
import com.example.cropinformation.databinding.FragmentStakingBinding
import com.example.cropinformation.utils.Constants
import com.example.cropinformation.viewModle.TabViewModel


class StakingFragment : Fragment() {
    private lateinit var binding: FragmentStakingBinding
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
        binding = FragmentStakingBinding.inflate(inflater)
        ViewModel.cropAdvisory()
        observer()
        return binding.root

    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) {
            val data = it.data!!
            for (i in 0..data.size-1) {
                if (data[i].label_name == "Staking") {
                    binding.labelName.text = data[i].label_name
                    binding.labelValue.text = data[i].label_value
                }
                if(data[i].label_name=="Distance between stakes"){
                    binding.labelValue4.text = data[i].label_value
                }
            }
        }
    }

}