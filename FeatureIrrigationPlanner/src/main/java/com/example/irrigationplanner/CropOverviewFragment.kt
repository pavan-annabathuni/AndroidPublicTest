package com.example.irrigationplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.irrigationplanner.databinding.FragmentCropOverviewBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import org.joda.time.DateTime
import org.joda.time.Weeks
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CropOverviewFragment : BottomSheetDialogFragment() {
    private val viewModel by lazy { ViewModelProvider(this)[IrrigationViewModel::class.java] }
    private lateinit var binding: FragmentCropOverviewBinding
    var plotId:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plotId = it.getInt("plotId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCropOverviewBinding.inflate(inflater)
        binding.imgClose.setOnClickListener {
            this.dismiss()
        }
        information()
        translation()
        binding.tvDrip.isSelected = true
        return binding.root
    }

    private fun information() {
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            /** Filtering my crop api with plot id*/
            val data = it.data?.first {
                it.id==plotId
            }
            binding.apply {
                /**setting data from api if data is null then we are showing null text*/
                if (data?.area != null)
                    tvAce.text = data.area
                else tvAce.text = "NA"

                if (data?.sowingDate != null)
                    tvDate.text = data.sowingDate.toString()
                else tvDate.text = "NA"

                if(it.data?.get(0)?.soilType!=null)
                    tvSoil.text = it.data?.get(0)?.soilType
                else
                tvSoil.text = "NA"

                if (data?.irrigationType != null)
                    tvDrip.text = data.irrigationType
                else tvDrip.text = "NA"

                if (data?.actualHarvestDate != null)
                    tvHarvest.text = data.actualHarvestDate

                else tvHarvest.text = "NA"

                if (data?.cropYear != null)
                    tvWeek.text = data.cropYear.toString()
                else tvWeek.text = "NA"

                /** setting crop age value */
                if (data?.sowingDate != null)
                    try {
                     val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val sowindate: Date =
                            dateFormat.parse(data.sowingDate)
                        val currentdate = Date()
                        val dateTime1 = DateTime(sowindate)
                        val dateTime2 = DateTime(currentdate)
                        val weeksOld = Weeks.weeksBetween(dateTime1, dateTime2).weeks
                        tvYield.text = weeksOld.toString() + " Weeks Old"
                    } catch (e: ParseException) {
                        tvYield.text = ""
                    }
                else tvYield.text = "NA"
            }
        }
    }

    /** Style for bottom sheet dialog */
    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }
    private fun translation(){
        TranslationsManager().loadString("str_crop_overview",binding.tv1,"Crop Overview")
        TranslationsManager().loadString("str_area",binding.tvArea,"Area")
        TranslationsManager().loadString("str_sowing_date",binding.tvSowingDate,"Sowing date")
        TranslationsManager().loadString("str_soil_type",binding.tvSoilType,"Soil Type")
        TranslationsManager().loadString("str_irrigation_type",binding.tvIrrigation,"Irrigation Type")
        TranslationsManager().loadString("str_harvest_date",binding.tvHarvestDate,"Harvest Date")
        TranslationsManager().loadString("str_crop_age",binding.textView12,"Crop Age")
        TranslationsManager().loadString("str_expected_avg",binding.tvExpectedDate,"Expected Avg Yeild")
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropOverviewFragment")
    }

}