package com.example.irrigationplanner

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.irrigationplanner.databinding.FragmentSheetHarvestBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SheetHarvestFragment : BottomSheetDialogFragment() {
    private lateinit var harvestBinding: FragmentSheetHarvestBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private var plotId: Int? = null
    private var accountId: Int? = null
    private var cropId: Int? = null
    var dateCrop: String = ""
    val myCalendar = Calendar.getInstance()
    var dateofBirthFormat = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plotId = it.getInt("plotId")
            accountId = it.getInt("accountId")
            cropId = it.getInt("cropId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        harvestBinding = FragmentSheetHarvestBinding.inflate(inflater)
        harvestBinding.close.setOnClickListener {
            this.dismiss()
        }
        harvestBinding.save.setOnClickListener {
            EventClickHandling.calculateClickEvent("Harvest_details_save")
            var date = harvestBinding.editText2.text.toString()
            if (harvestBinding.editText.text.toString() != "" && date != "") {
                var yield_tone = harvestBinding.editText.text.toString().toInt()
                    viewModel.updateHarvest(plotId!!, accountId!!, cropId!!, date, yield_tone)
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                is Resource.Success -> {
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        MyCropSyncer().invalidateSync()
//                                        LocalSource.deleteAllMyCrops()
//                                        MyCropSyncer().getMyCrop()
//                                    }
//                                    viewModel.setCropHarvested()
                                    dismiss()
                                }
                                is Resource.Loading -> {}
                                is Resource.Error -> {
                                AppUtils.translatedToastServerErrorOccurred(context)
                                }
                        }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastEnterFields = TranslationsManager().getString("toast_enter_all_field")
                    if(!toastEnterFields.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastEnterFields,
                            Toast.LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please enter all the mandatory fields",
                        Toast.LENGTH_SHORT
                    ) }}}

            }
        }
        harvestBinding.editText2.setOnClickListener {
            showCalender()
        }
        translation()
        return harvestBinding.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

    private fun showCalender() {
        var date: DatePickerDialog.OnDateSetListener? =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                myCalendar.add(Calendar.YEAR, -1)
                view.minDate = myCalendar.timeInMillis
                updateLabel(myCalendar)
                myCalendar.add(Calendar.YEAR, 1)
                view.maxDate = myCalendar.timeInMillis
            }
        val dialog = DatePickerDialog(
            requireContext(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dateCrop = dateofBirthFormat.format(myCalendar.time)
        myCalendar.add(Calendar.YEAR, -1)
        dialog.datePicker.minDate = myCalendar.timeInMillis
        myCalendar.add(Calendar.YEAR, 2) // add 4 years to min date to have 2 years after now
        dialog.datePicker.maxDate = myCalendar.timeInMillis
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(
            Color.parseColor("#7946A9")
        )
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(
            Color.parseColor("#7946A9")
        )
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        harvestBinding.editText2.text = dateFormat.format(myCalendar.time)
    }

    private fun translation() {
//        TranslationsManager().loadString(
//            "str_harvest_details",
//            harvestBinding.textView13,
//            "Harvest Details"
//        )
        TranslationsManager().loadString(
            "str_actual_yeild",
            harvestBinding.textView14,
            "Actual Yeild in Tonne"
        )
        TranslationsManager().loadString(
            "str_actual_harvest_date",
            harvestBinding.textView2,
            "Actual Harvest Date"
        )

        viewModel.viewModelScope.launch {
            val save = TranslationsManager().getString("str_save")
            harvestBinding.save.text = save
        }

    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SheetHarvestFragment")
    }

}