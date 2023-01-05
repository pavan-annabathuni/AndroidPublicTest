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
import com.waycool.data.translations.TranslationsManager
import org.joda.time.DateTime
import org.joda.time.Weeks
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.temporal.TemporalQueries.zone
import java.util.*


class CropOverviewFragment : BottomSheetDialogFragment() {
    private val viewModel by lazy { ViewModelProvider(this)[IrrigationViewModel::class.java] }
    private lateinit var binding: FragmentCropOverviewBinding
     var accountId:Int = 0
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
        binding.imgClose.setOnClickListener() {
            this.dismiss()
        }
        viewModel.getUserDetails().observe(viewLifecycleOwner){
            accountId = it.data?.accountId!!
        }
        information()
        translation()
        return binding.root
    }

    private fun information() {
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            val data = it.data?.filter {
                it.id==plotId
            }
            Log.d("cropInfo", "information: $data ")
            //Toast.makeText(context, "${data?.get(0)?.actualHarvestDate}", Toast.LENGTH_SHORT).show()
            binding.apply {

                if (data?.get(0)?.area != null)
                    tvAce.text = data?.get(0)?.area
                else tvAce.text = "NA"

                if (data?.get(0)?.sowingDate != null)
                    tvDate.text = data!![0].sowingDate.toString()
                else tvDate.text = "NA"

                if(it.data?.get(0)?.soilType!=null)
                    tvSoil.text = it.data?.get(0)?.soilType
                else
                tvSoil.text = "NA"

                if (data?.get(0)?.irrigationType != null)
                    tvDrip.text = data?.get(0)?.irrigationType
                else tvDrip.text = "NA"

                if (data?.get(0)?.actualHarvestDate != null)
                    tvHarvest.text = data.get(0)?.actualHarvestDate

                else tvHarvest.text = "NA"

                if (data?.get(0)?.cropYear != null)
                    tvWeek.text = data?.get(0)?.cropYear.toString()
                else tvWeek.text = "NA"

                if (data?.get(0)?.expectedHarvestDate != null)
                    try {
                     val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val sowindate: Date =
                            dateFormat.parse(data?.get(0)?.expectedHarvestDate)
                        val currentdate = Date()
                        val dateTime1 = DateTime(sowindate)
                        val dateTime2 = DateTime(currentdate)
                        val weeksOld = Weeks.weeksBetween(dateTime1, dateTime2).getWeeks()
                        tvYield.setText(weeksOld.toString() + " Weeks Old")
                    } catch (e: ParseException) {
                        tvYield.setText("")
                    }
                else tvYield.text = "NA"
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }
    private fun translation(){
        TranslationsManager().loadString("str_crop_overview",binding.tv1)
        TranslationsManager().loadString("str_area",binding.tvArea)
        TranslationsManager().loadString("str_sowing_date",binding.tvSowingDate)
        TranslationsManager().loadString("str_soil_type",binding.tvSoilType)
        TranslationsManager().loadString("str_irrigation_type",binding.tvIrrigation)
        TranslationsManager().loadString("str_harvest_date",binding.tvHarvestDate)
        TranslationsManager().loadString("str_crop_age",binding.textView12)
        TranslationsManager().loadString("str_expected_avg",binding.tvExpectedDate)



    }

}