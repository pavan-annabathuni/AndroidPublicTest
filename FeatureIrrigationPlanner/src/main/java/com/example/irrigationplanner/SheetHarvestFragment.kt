package com.example.irrigationplanner

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SheetHarvestFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSheetHarvestBinding
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
        binding = FragmentSheetHarvestBinding.inflate(inflater)

        binding.close.setOnClickListener() {
            this.dismiss()
        }
        binding.save.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Harvest_details_save")
            var date = binding.editText2.text.toString()
            if (binding.editText.text.toString() != "" && date != "") {
                var yield_tone = binding.editText.text.toString().toInt()
                plotId?.let { it1 ->
                    viewModel.updateHarvest(it1, accountId!!, cropId!!, date, yield_tone).observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                this.dismiss()
                            }
                            is Resource.Loading -> {}
                            is Resource.Error -> {
                                context?.let { it1 ->
                                    ToastStateHandling.toastError(
                                        it1,
                                        "Error",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                                Log.d("cropInfo", "onCreateView: ${it.message}")
                            }
                        }
                        Log.d("Harvest", "onCreateView: ${it.message}")
                    }
                }
            } else {
                context?.let { it1 ->
                    ToastStateHandling.toastError(
                        it1,
                        "Enter All Fields",
                        Toast.LENGTH_SHORT
                    )
                }
            }
            // Toast.makeText(context,"Enter the data",Toast.LENGTH_SHORT).show()
//                GlobalScope.launch {
//                    LocalSource.deleteAllMyCrops()
//                    MyCropSyncer().invalidateSync()
//
//            }
            //this.dismiss()

        }
//        binding.cal.setOnClickListener() {
//            showCalender()
//        }
        binding.editText2.setOnClickListener(){
            showCalender()
        }
        translation()
        return binding.root
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
                myCalendar.add(Calendar.YEAR, -1);
                view.setMinDate(myCalendar.timeInMillis)
                updateLabel(myCalendar)
                myCalendar.add(Calendar.YEAR, 1)
                view.setMaxDate(myCalendar.timeInMillis)

            }

        val dialog = DatePickerDialog(
            requireContext(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dateCrop = dateofBirthFormat.format(myCalendar.time)
        myCalendar.add(Calendar.YEAR, -1);
        dialog.datePicker.setMinDate(myCalendar.timeInMillis)
        myCalendar.add(Calendar.YEAR, 2); // add 4 years to min date to have 2 years after now
        dialog.datePicker.setMaxDate(myCalendar.getTimeInMillis());
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(
            Color.parseColor("#7946A9")
        );
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(
            Color.parseColor("#7946A9")
        )
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.editText2.setText(dateFormat.format(myCalendar.getTime()))
    }

    private fun translation() {
        TranslationsManager().loadString("str_harvest_details", binding.textView13,"Harvest Details")
        TranslationsManager().loadString("str_actual_yeild", binding.textView14,"Actual Yeild in Tonne")
        TranslationsManager().loadString("str_actual_harvest_date", binding.textView2,"Actual Harvest Date")

        viewModel.viewModelScope.launch {
            val save = TranslationsManager().getString("str_save")
            binding.save.text = save
        }

    }
}