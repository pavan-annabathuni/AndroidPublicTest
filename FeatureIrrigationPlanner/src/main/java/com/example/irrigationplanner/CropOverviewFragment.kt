package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.irrigationplanner.databinding.FragmentCropOverviewBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CropOverviewFragment : BottomSheetDialogFragment() {
    private val viewModel by lazy { ViewModelProvider(this)[IrrigationViewModel::class.java] }
    private lateinit var binding: FragmentCropOverviewBinding
     var accountId:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCropOverviewBinding.inflate(inflater)
        binding.imgClose.setOnClickListener() {
            this.dismiss()
        }
        viewModel.getUserDetails().observe(viewLifecycleOwner){
            accountId = it.data?.accountId!!
        }
        information()
        return binding.root
    }

    fun information() {
        viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
            binding.apply {
                if (it.data?.get(0)?.area != null)
                    tvAce.text = it.data?.get(0)?.area
                else tvAce.text = "NA"

                if (it.data?.get(0)?.sowingDate != null)
                    tvDate.text = it.data!![0].sowingDate.toString()
                else tvDate.text = "NA"

                // if(it.data?.get(0)?.expectedHarvestDate!=null)
                tvSoil.text = "NA"

                if (it.data?.get(0)?.irrigationType != null)
                    tvDrip.text = it.data?.get(0)?.irrigationType
                else tvDrip.text = "NA"

                if (it.data?.get(0)?.actualHarvestDate != null)
                    tvHarvest.text = it.data?.get(0)?.actualHarvestDate
                else tvHarvest.text = "NA"

                if (it.data?.get(0)?.expectedHarvestDate != null)
                    tvWeek.text = it.data?.get(0)?.cropYear.toString()
                else tvWeek.text = "NA"

                if (it.data?.get(0)?.expectedHarvestDate != null)
                    tvYield.text = it.data!![0].expectedHarvestDate
                else tvYield.text = "NA"
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

}