package com.example.irrigationplanner

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.databinding.FragmentCropStageBinding
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CropStageFragment : Fragment() {
    private lateinit var binding: FragmentCropStageBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    var date1: String? = ""
    var date2: String? = ""
    var date3: String? = ""
    var date4: String? = ""
    var date5: String? = ""
    var date6: String? = ""
    var date7: String? = ""
    var date8: String? = ""
    var date9: String? = ""
    var date10: String? = ""
    var date11: String? = ""
    var date12: String? = ""
    var date13: String? = ""
    var date14: String? = ""
    var date15: String? = ""


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
        binding = FragmentCropStageBinding.inflate(inflater)
        onClick()
        getCropStage()


        return binding.root
    }


    fun onClick() {
        binding.topAppBar.setOnClickListener(){
            findNavController().navigateUp()
        }
        date1 = binding.cal1.text.toString()
        date2 = binding.cal2.text.toString()
        date3 = binding.cal3.text.toString()
        date4 = binding.cal4.text.toString()
        date5 = binding.cal5.text.toString()

        binding.cal1.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    // Display Selected date in textbox
                    binding.cal1.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date1 = binding.cal1.text.toString()
                },
                year,
                month,
                day
            )
            Log.d("CropStage", "getCropStage: ${date1}")

            dpd.show()
        }
        binding.cal2.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal2.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date2 = binding.cal2.text.toString()
                    Log.d("CropStage", "getCropStage: ${date2}")
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal3.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal3.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date3 = binding.cal3.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal4.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal4.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date4 = binding.cal4.text.toString()
                    Log.d("CropStage", "getCropStage: ${date4}")
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal5.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal5.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date5 = binding.cal5.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal6.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal6.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date6 = binding.cal6.text.toString()
                    Log.d("date", "onClick: $date6")
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal7.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal7.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date7 = binding.cal7.text.toString()
                    Log.d("date", "onClick: $date7")
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal8.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal8.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date8 = binding.cal8.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal9.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal9.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date9 = binding.cal9.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal10.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal10.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date10 = binding.cal10.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal11.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal11.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date11 = binding.cal11.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal12.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal12.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date12 = binding.cal12.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal13.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal13.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date13 = binding.cal13.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal14.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal14.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date14 = binding.cal14.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.cal15.setOnClickListener() {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding.cal15.text = ("" + dayOfMonth + "-" + monthOfYear + "-" + year)
                    date15 = binding.cal15.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }


        binding.saveCropStage.setOnClickListener() {
            viewModel.updateCropStage(
                1, 1, 20,date1,date2,date3,date4,date5,date6,date7,
                date8, date9,date10,date11,date12,date13,
                date14,date15
            ).observe(viewLifecycleOwner) {
                Log.d("CropStage", "onClick: ${it.message}")
            }
            getCropStage()
        }
    }

    fun getCropStage() {
        viewModel.getCropStage().observe(viewLifecycleOwner) {
            val size: Int? = it.data?.data?.size?.minus(1)
            binding.cal1.text =
                size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningFruitPruning }
                    ?: "Select Date"
            date1 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningFruitPruning }
                    ?: ""
            if (binding.cal1.text == "Select Date") {
                binding.view9.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo1.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view9.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo1.setImageResource(R.drawable.ic_holo_darkgreen)
            }
            Log.d("size", "getCropStage: ${it.data?.data?.get(size!!)?.id}")

            binding.cal2.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningBudBreak }
                ?: "Select Date"
            date2 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningBudBreak }
                ?: ""
            if (binding.cal2.text == "Select Date") {
                binding.view2.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo2.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view2.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo2.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal3.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningRemovalOfExcessive }
                ?: "Select Date"
            date3 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningRemovalOfExcessive }
                ?: ""
            if (binding.cal3.text == "Select Date") {
                binding.view3.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo3.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view3.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo3.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal4.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningShootDevelopment }
                ?: "Select Date"
            date4 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningShootDevelopment }
                ?: ""
            if (binding.cal4.text == "Select Date") {
                binding.view4.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo4.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view4.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo4.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal5.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningFlowering }
                ?: "Select Date"
            date5 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningFlowering }
                ?: ""
            if (binding.cal5.text == "Select Date") {
                binding.view5.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo5.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view5.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo5.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal6.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningFruitSet }
                ?: "Select Date"
            date6 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningFruitSet }
                ?: ""
            if (binding.cal6.text == "Select Date") {
                binding.view6.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo6.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view6.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo6.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal7.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningBerryDevelopment }
                ?: "Select Date"
            date7 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningBerryDevelopment }
                ?: ""
            if (binding.cal7.text == "Select Date") {
                binding.view7.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo7.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view7.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo7.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal8.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningBeginningOfVeraison }
                ?: "Select Date"
            date8 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningBeginningOfVeraison }
                ?: ""
            if (binding.cal8.text == "Select Date") {
                binding.view8.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo8.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view8.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo8.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal9.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningHarvest }
                ?: "Select Date"
            date9 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningHarvest }
                ?: ""
            if (binding.cal9.text == "Select Date") {
                binding.view10.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo9.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view10.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo9.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal10.text = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningRestPeriod }
                ?: "Select Date"
            date10 = size?.let { it1 -> it.data?.data?.get(it1)?.fruitPruningRestPeriod }
                ?: ""
            if (binding.cal10.text == "Select Date") {
                binding.view11.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo10.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view11.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo10.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal11.text = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningFoundationPruning }
                ?: "Select Date"
            date11 = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningFoundationPruning }
                ?: ""
            if (binding.cal11.text == "Select Date") {
                binding.view12.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo11.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view12.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo11.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal12.text = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningBudBreak }
                ?: "Select Date"
            date12 = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningBudBreak }
                ?: ""
            if (binding.cal12.text == "Select Date") {
                binding.view13.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo12.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view13.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo12.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal13.text = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningCaneThinning }
                ?: "Select Date"
            date13 = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningCaneThinning }
                ?: ""
            if (binding.cal13.text == "Select Date") {
                binding.view14.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo13.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view14.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo13.setImageResource(R.drawable.ic_holo_darkgreen)
            }

            binding.cal14.text = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningSubCane }
                ?: "Select Date"
            date14 = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningSubCane }
                ?: ""
            if (binding.cal14.text == "Select Date") {
                binding.view15.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo15.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view15.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo15.setImageResource(R.drawable.ic_holo_darkgreen)
            }
            binding.cal15.text = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningTopping }
                ?: "Select Date"
            date15 = size?.let { it1 -> it.data?.data?.get(it1)?.foundationPruningTopping }
                ?: ""
            if (binding.cal15.text == "Select Date") {
                binding.view16.background = resources.getDrawable(R.color.LightGray)
                binding.imgHolo16.setImageResource(R.drawable.ic_holo_gray)
            } else {
                binding.view16.background = resources.getDrawable(R.color.DarkGreen)
                binding.imgHolo16.setImageResource(R.drawable.ic_holo_darkgreen)
            }

        }

        }
        }
