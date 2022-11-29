package com.example.addcrop.ui.premium

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentAddCropPremiumBinding
import com.example.addcrop.viewmodel.AddViewModel
import com.waycool.data.utils.Resource
import java.text.SimpleDateFormat
import java.util.*


class AddCropPremiumFragment : Fragment() {
    private var accountID: Int? = null
    private var _binding: FragmentAddCropPremiumBinding? = null
    private val binding get() = _binding!!
    val myCalendar = Calendar.getInstance()
    var dateCrop: String = ""
    var nickName: String = ""
    var area: String = ""
    var numberOfPlanets: String = ""
    var date: String = ""
    var crop_id: Int? = null
    var crop_type: Int? = null
    val arrayList = ArrayList<String>()
    lateinit var irrigation_selected: String
    lateinit var year_selected: String

    var dateofBirthFormat = SimpleDateFormat("yyyy-MM-dd")
    private val viewModel by lazy { ViewModelProvider(this)[AddViewModel::class.java] }

    val colors = arrayOf(
        "Select Irrigation method",
        "Drip Irrigation",
        "Sprinkler Irrigation",
        "Flood Irrigation"
    )
    val years = arrayOf(
        "Acres",
        "Gunta",
        "Cent",
        "Hectare",
        "Bigha"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCropPremiumBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        spinnerYear()
        initViewClicks()
//        binding.cardCheckHealth.setOnClickListener {
//            postDataAddCrop()
//        }
    }


    fun initView() {
        if (arguments != null) {
            crop_id = arguments?.getInt("cropid")
            crop_type = arguments?.getInt("soil_type_id")

            if (crop_id == 2) {
//                binding.clSwitch.visibility = View.VISIBLE
                binding.FirstIrrigationDate.visibility = View.VISIBLE
                binding.Address.visibility = View.INVISIBLE
                binding.tvBahar.visibility = View.VISIBLE
                binding.clSpinnerYearBahar.visibility = View.VISIBLE
                binding.tvYearShow.visibility = View.VISIBLE
                binding.clSpinnerYear.visibility = View.VISIBLE
                binding.clSwitchMulching.visibility = View.VISIBLE
            } else if (crop_id == 67) {
                binding.EnterDateoffruitPruning.visibility = View.VISIBLE
                binding.Address.visibility = View.INVISIBLE
                binding.tvYearShow.visibility = View.VISIBLE
                binding.clSpinnerYear.visibility = View.VISIBLE

            }

            viewModel.getUserDetails().observe(viewLifecycleOwner) {
                for (i in it.data!!.account) {
                    if (i.accountType == "outgrow") {
                        Log.d(ContentValues.TAG, "onCreateViewAccountID:${i.id}")
                        accountID = i.id
                        irrigationTypeSpinner(accountID!!, crop_id!!, crop_type!!)

                    }
                }
            }
            Log.d(ContentValues.TAG, "onCreateViewONPIDPrinteddvsv: $crop_id")
            Log.d(ContentValues.TAG, "onCreateViewONPIDPrinteddvsv: $crop_type")
        }

    }

    fun postDataAddCrop(): Map<String, Any>? {
        val map = mutableMapOf<String, Any>()
//        map.put("account_id", account_id)
//        map.put("cropid", crop_id)
        map.put("nickname", binding.etNickName.text.toString())
        map.put("area", binding.etAreaNumber.text)
        map.put("sowingdate", binding.etCalender.text.toString())
        map.put("noOfAcre", binding.etNoOfAcre.text.toString())
        Log.d("TAG", "itemClickedBHSCbjzdnjvn: $map")
//        if (arguments != null) {
//            viewModel.addCropDataPass(map)
//        }
        return map

    }

    private fun initViewClicks() {
        binding.tvCalender.setOnClickListener {
            showCalender()
        }
        binding.cardCheckHealth.setOnClickListener {

        }
    }

    private fun spinnerYear() {
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        binding.Acres.adapter = arrayAdapter
        binding.Acres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = p0?.selectedItem
                year_selected = item.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }


    private fun irrigationTypeSpinner(account_id: Int, crop_id: Int, soil_type_id: Int) {
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, colors)
        binding.tvSpinner.adapter = arrayAdapter
        binding.tvSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                itemClicked()
                    val item = p0?.selectedItem
                    irrigation_selected = item.toString()
//                arrayList.add(irrigation)
                    if (colors[0] == irrigation_selected) {
                        binding.cardCheckHealth.setOnClickListener {
                            Toast.makeText(requireContext(), "Please Irrigation Type", Toast.LENGTH_SHORT).show()
                        }
                    } else if (colors[1] == (irrigation_selected)) {
                        binding.clPlotNumber.visibility = View.GONE
                        binding.plotNumber.visibility = View.GONE
                        binding.tvCheckCrop.setText("Next")
                        binding.cardCheckHealth.setOnClickListener {
                            it.hideSoftInput()
                            nickName = binding.etNickName.text.toString().trim()
                            area = binding.etAreaNumber.text.toString().trim()
                            numberOfPlanets = binding.etNoOfAcre.text.toString().trim()
                            date = binding.etCalender.text.toString().trim()
                            if (area.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "please Enter Area",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.etAreaNumber.error = "Enter Area"
                                return@setOnClickListener
                            } else if (date.isEmpty()) {
                                binding.etCalender.error = "Pick up the Date"
                                return@setOnClickListener
                            } else if (numberOfPlanets.isEmpty()) {
                                binding.etNoOfAcre.error = "Enter Number of Planets"
                                return@setOnClickListener
                            } else if (area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
                                Toast.makeText(requireContext(), "Next Page", Toast.LENGTH_SHORT)
                                    .show()
                                val bundle = Bundle()
//                                val map = mutableMapOf<String, Any>()
//                                Log.d("TAG", "itemClickedjnvjndkfnvk:$account_id ")
//                                map.put("account_no_id", account_id)
//                                map.put("crop_id", crop_id)
//                                map.put("soil_type_id",soil_type_id)
//                                map.put("plot_nickname", binding.etNickName.text.toString())
//                                map.put("area", binding.etAreaNumber.text)
//                                map.put("irrigation_type",item.toString())
//                                map.put("sowing_date", binding.etCalender.text.toString())
//                                map.put("no_of_plants", binding.etNoOfAcre.text.toString())
//                                Log.d("TAG", "itemClickedBHSCbjzdnjvn: $map")
//                                bundle.putString("map",map.toString())

                                bundle.putInt("account_id", account_id)
                                bundle.putInt("cropid", crop_id)
                                bundle.putInt("crop_type", soil_type_id)
                                bundle.putString("area", area)
                                bundle.putString("date", date)
                                bundle.putString("irrigation_selected", irrigation_selected)
                                bundle.putString("numberOfPlanets", numberOfPlanets)

                                findNavController().navigate(
                                    R.id.action_addCropPremiumFragment_to_plantSpacingFragment,
                                    bundle
                                )
                            }
                        }
                    } else if (colors[2] == (item)) {
                        Log.d("TAG", "onItemSelectedIrrigationType:$colors[2]")
                        Log.d("TAG", "onItemSelectedIrrigationType:$colors")
                        Log.d("TAG", "onItemSelectedIrrigationType:$item")
                        binding.clPlotNumber.visibility = View.VISIBLE
                        binding.plotNumber.visibility = View.VISIBLE
                        binding.tvCheckCrop.setText("Save Crop")
                        itemClicked(account_id, crop_id,soil_type_id,item)
//                    else if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
//                        Toast.makeText(requireContext(), "Api Call Success 2", Toast.LENGTH_SHORT).show()
//                    }


                    } else if (colors[3] == (item)) {
                        binding.clPlotNumber.visibility = View.VISIBLE
                        binding.plotNumber.visibility = View.VISIBLE
                        binding.tvCheckCrop.setText("Save Crop")
                        itemClicked(account_id, crop_id,soil_type_id,item)
//                    if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
//                        Toast.makeText(requireContext(), "Api Call Success 3", Toast.LENGTH_SHORT).show()
//                    }

                    }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }


    private fun itemClicked(account_id: Int, crop_id: Int,soil_type_id:Int,irrigation_type:String) {
        binding.cardCheckHealth.setOnClickListener {
            it.hideSoftInput()
            nickName = binding.etNickName.text.toString().trim()
            area = binding.etAreaNumber.text.toString().trim()
            date = binding.etCalender.text.toString().trim()
            numberOfPlanets = binding.etNoOfAcre.text.toString().trim()

            if (nickName.isEmpty()) {
                binding.etNickName.error = "Nick name should not be empty"
                return@setOnClickListener
            } else if (area.isEmpty()) {
                binding.etAreaNumber.error = "Enter Area"
                return@setOnClickListener
            } else if (date.isEmpty()) {
                binding.etCalender.error = "Pick up the Date"
                return@setOnClickListener
            } else if (accountID == null) {
                Toast.makeText(requireContext(), "Incorrect Id", Toast.LENGTH_SHORT).show()
            } else if (numberOfPlanets.isEmpty()) {
                binding.etNoOfAcre.error = "Enter Number of Planets"
                return@setOnClickListener
            } else if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
                val map = mutableMapOf<String, Any>()
                Log.d("TAG", "itemClickedjnvjndkfnvk:$account_id ")
                map.put("account_no_id", account_id)
                map.put("crop_id", crop_id)
                map.put("soil_type_id",soil_type_id)
                map.put("plot_nickname", binding.etNickName.text.toString())
                map.put("area", binding.etAreaNumber.text)
                map.put("irrigation_type",irrigation_type)
                map.put("sowing_date", binding.etCalender.text.toString())
                map.put("no_of_plants", binding.etNoOfAcre.text.toString())
                Log.d("TAG", "itemClickedBHSCbjzdnjvn: $map")
                viewModel.addCropDataPass(map).observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            activity?.finish()
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(
                                ContentValues.TAG,
                                "postAddCropExption: ${it.message.toString()}"
                            )
                        }
                        is Resource.Loading -> {
                            Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
                }


            }
        }

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
        binding.etCalender.text = dateFormat.format(myCalendar.getTime())
    }

    fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}