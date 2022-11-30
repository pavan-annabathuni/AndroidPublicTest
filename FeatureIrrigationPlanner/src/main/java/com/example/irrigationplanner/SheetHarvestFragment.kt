package com.example.irrigationplanner

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.irrigationplanner.databinding.FragmentSheetHarvestBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*


class SheetHarvestFragment : BottomSheetDialogFragment() {
   private lateinit var binding:FragmentSheetHarvestBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    var dateCrop: String = ""
    val myCalendar = Calendar.getInstance()
    var dateofBirthFormat = SimpleDateFormat("yyyy-MM-dd")
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
        binding = FragmentSheetHarvestBinding.inflate(inflater)

        binding.close.setOnClickListener(){
            this.dismiss()
        }
        binding.save.setOnClickListener(){
            var yield_tone = binding.editText.text.toString().toInt()
            var date  = binding.editText2.text.toString()

            viewModel.updateHarvest(18,date,yield_tone).observe(viewLifecycleOwner){
                Log.d("Harvest", "onCreateView: ${it.message}")
            }
            Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show()
         this.dismiss()
        }
        binding.editText2.setOnClickListener(){
            showCalender()
        }
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
}