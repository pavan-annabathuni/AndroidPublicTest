package com.example.addcrop.ui

import android.R
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
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
import com.example.addcrop.databinding.FragmentAddCropDetailsBinding
import com.example.addcrop.viewmodel.AddViewModel
import com.waycool.data.utils.Resource
import java.text.SimpleDateFormat
import java.util.*


class AddCropDetailsFragment : Fragment() {
    private var accountID: Int? = null
    private var _binding: FragmentAddCropDetailsBinding? = null
    private val binding get() = _binding!!
    val myCalendar = Calendar.getInstance()
    var dateCrop: String = ""
    var nickName: String = ""
    var area: String = ""
    var numberOfPlanets: String = ""
    var date: String = ""
    val arrayList = ArrayList<String>()
    lateinit var irrigation_selected: String
    lateinit var areaTypeSelected: String


    var dateofBirthFormat = SimpleDateFormat("yyyy-MM-dd")
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }


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
        // Inflate the layout for this fragment
        _binding = FragmentAddCropDetailsBinding.inflate(inflater, container, false)

//        viewModel.getUserData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val cropIdSelected = arguments?.getInt("cropid")
        }

            viewModel.getUserDetails().observe(viewLifecycleOwner) {
                accountID = it.data?.accountId
                getFarms()
            }

            binding.cardCheckHealth.setOnClickListener {
                postAddCrop(cropIdSelected!!, accountID!!)
            }


//        spinner()
            spinnerYear()
            binding.clCalender.setOnClickListener {
                showCalender()
//            showDateDailog()
            }
            binding.backBtn.setOnClickListener {
                val isSuccess = findNavController().navigateUp()
                if (!isSuccess) requireActivity().onBackPressed()
            }
//        if (binding.etNickName.text.toString().isEmpty() || binding.etAreaNumber.text.toString().isEmpty() || binding.etCalender.text.toString().isEmpty()){
//
//        }


//        }

//    private fun spinner() {
//        val arrayAdapter =
//            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, colors)
//        binding.tvSpinner.adapter = arrayAdapter
//        binding.tvSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
////                itemClicked()
//                val item = p0?.selectedItem
//                irrigation_selected = item.toString()
////                arrayList.add(irrigation)
//                if (colors[1] == (irrigation_selected)) {
//
//                    binding.clPlotNumber.visibility = View.GONE
//                    binding.plotNumber.visibility = View.GONE
//                    binding.tvCheckCrop.setText("Next")
//                    binding.cardCheckHealth.setOnClickListener {
//                        it.hideSoftInput()
////                        nickName = binding.etPlotNumber.text.toString().trim()
//                        area = binding.etPincodeNumber.text.toString().trim()
//                        numberOfPlanets = binding.etState.text.toString().trim()
//                        date = binding.etCalender.text.toString().trim()
//                         if (area.isEmpty()) {
//                            binding.etPincodeNumber.error = "Enter Area"
//                            return@setOnClickListener
//                        } else if (date.isEmpty()) {
//                            binding.etCalender.error = "Pick up the Date"
//                            return@setOnClickListener
//
//                        } else if (numberOfPlanets.isEmpty()) {
//                            binding.etState.error = "Enter Number of Planets"
//                            return@setOnClickListener
//                        } else if (area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
//                                Toast.makeText(requireContext(), "Success API Call", Toast.LENGTH_SHORT)
//                                    .show()
//                                val bundle = Bundle()
//                                bundle.putString("area", area)
//                                bundle.putString("date", date)
//                                bundle.putString("irrigation_selected",irrigation_selected)
//                                bundle.putString("numberOfPlanets",numberOfPlanets)
//                                findNavController().navigate(R.id.action_addCropDetailsFragment_to_plantSpacingFragment,bundle)
//
//
//                        }
//                    }
//                } else if (colors[2] == (item)) {
////                    itemClicked()
//                    binding.clPlotNumber.visibility = View.VISIBLE
//                    binding.plotNumber.visibility = View.VISIBLE
//                    binding.tvCheckCrop.setText("Save Crop")
//
//                } else if (colors[3] == (item)) {
////                    binding.etPincodeNumber.notify().
//                    binding.clPlotNumber.visibility = View.VISIBLE
//                    binding.plotNumber.visibility = View.VISIBLE
//                    binding.tvCheckCrop.setText("Save Crop")
//                    itemClicked()
//
//
//                }
//
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//        }
//
//    }
    }

    private fun getFarms() {
        if (accountID != null)
            viewModel.getMyFarms().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        if (!it.data.isNullOrEmpty()) {
                            binding.farmsCl.visibility = View.VISIBLE
                            binding.myfarmsChipGroup.removeAllViews()
                            selectedFarmId = null
                            val farmsList = it.data
                            if (farmsList != null) {
                                for (farm in farmsList) {
                                    createChip(farm)
                                }
                            }
                        } else {
                            binding.farmsCl.visibility = View.GONE
                        }
                    }
                    is Resource.Loading -> {
                        Log.d("farm", "step5")
                    }
                    is Resource.Error -> {
                        binding.farmsCl.visibility = View.GONE
                        Log.d("farm", "step6 " + it.message)
                    }
                    else -> {
                        Log.d("farm", "step7")

                    }
                }
            }
    }

    private fun createChip(farm: MyFarmsDomain) {
        val chip = Chip(requireContext())
        chip.text = farm.farmName
        chip.isCheckable = true
        chip.isClickable = true
        chip.isCheckedIconVisible = true
        chip.setTextColor(
            AppCompatResources.getColorStateList(
                requireContext(),
                com.waycool.uicomponents.R.color.bg_chip_text
            )
        )
        chip.setChipBackgroundColorResource(com.waycool.uicomponents.R.color.chip_bg_selector)
        chip.chipStrokeWidth = 1f
        chip.chipStrokeColor = AppCompatResources.getColorStateList(
            requireContext(),
            com.waycool.uicomponents.R.color.strokegrey
        )

        chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                selectedFarmId = farm.id
            }
        }
        binding.myfarmsChipGroup.addView(chip)
    }


    private fun spinnerYear() {
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, years)
        binding.Acres.adapter = arrayAdapter
        binding.Acres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = p0?.selectedItem
                areaTypeSelected = item.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    //format(binding.etAreaNumber.text.toString()).toDouble()
    private fun postAddCrop(crop_id: Int, account_id: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.cardCheckHealth.visibility = View.INVISIBLE
//    if (binding.etNickName.text.isEmpty() ||format(binding.etAreaNumber.text.toString()).toDouble() ==null){
        val map = mutableMapOf<String, Any>()
        map["account_no_id"] = account_id
        map["crop_id"] = crop_id
        map["plot_nickname"] = binding.etNickName.text.toString()
        map["sowing_date"] = binding.etCalender.text.toString()
        map["area_type"] = areaTypeSelected.lowercase()
        map["area"] = binding.etAreaNumber.text
        if (selectedFarmId != null)
            map["farm_id"] = selectedFarmId!!

        viewModel.addCropDataPass(
            map
//            crop_id, account_id, binding.etNickName.text.toString(), 1,
//            binding.etCalender.text.toString(), binding.etAreaNumber.text
        ).observe(requireActivity()) {
//            Log.d(TAG, "itemClickedData: $myCalendar")
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cardCheckHealth.visibility = View.VISIBLE
                    activity?.finish()
                    accountID?.let { it1 ->
                        viewModel.getMyCrop2(it1).observe(viewLifecycleOwner) {}
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "postAddCropExption: ${it.message.toString()}")
                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        }


    }
//    else {
//        viewModel.addCropPassData(
//            crop_id,account_id, binding.etNickName.text.toString(), 1,
//             binding.etCalender.text.toString(),format(binding.etAreaNumber.text.toString()).toDouble()).observe(requireActivity()) {
////            Log.d(TAG, "itemClickedData: $myCalendar")
//            when (it) {
//                is Resource.Success -> {
//                    activity?.finish()
//                }
//                is Resource.Error -> {
//                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "postAddCropExption: ${it.message.toString()}")
//                }
//                is Resource.Loading -> {
//                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//        }
//    }
//    }


    private fun itemClicked(rop_id: Int) {
        binding.cardCheckHealth.setOnClickListener {
            it.hideSoftInput()
            nickName = binding.etNickName.text.toString().trim()
            area = binding.etAreaNumber.text.toString().trim()
            date = binding.etCalender.text.toString().trim()

//            numberOfPlanets = binding.etState.text.toString().trim()

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
            }
//            else if (numberOfPlanets.isEmpty()) {
//                binding.etState.error = "Enter Number of Planets"
//                return@setOnClickListener
//            }
//            else if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && accountID != null) {
//                viewModel.addCropPassData(rop_id, accountID!!, binding.etNickName.text.toString(), 1,
//                    sowing_date = dateCrop,
//                    area = area.toDouble()).observe(requireActivity()) {
//                    Log.d(TAG, "itemClickedData: $myCalendar")
//                    if (it is Resource.Success) {
//                        activity?.finish()
//                    } else {
//                        activity?.finish()
//                        Toast.makeText(requireContext(), "Error API Call", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                }
//
//            }

        }
    }

//    private fun addCropPostBody() {
//        nickName = binding.etPlotNumber.text.toString().trim()
//        area = binding.etPincodeNumber.text.toString().trim()
//        date = binding.etCalender.text.toString().trim()
//        numberOfPlanets = binding.etState.text.toString().trim()
//        if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()  ) {
//            Toast.makeText(requireContext(), "Success API Call", Toast.LENGTH_SHORT).show()
////            val requestBody=AddCropRequest(19,area,1,"","","")
////            viewModel.addCropPassData(requestBody)
//        }
//        else{
//            Toast.makeText(requireContext(), "Please Fill The Details", Toast.LENGTH_SHORT).show()
//        }
//
//            arrayList.add(nickName)
//            arrayList.add(area)
//            arrayList.add(date)
//            arrayList.add(numberOfPlanets)
//
//
//            Log.d(TAG, "bindHandlersArea: $nickName")
//            Log.d(TAG, "bindHandlersArea: $area")
//            Log.d(TAG, "bindHandlersArea: $date")
//            Log.d(TAG, "bindHandlersArea: $numberOfPlanets")
//            Log.d(TAG, "bindHandlersAreaList: $arrayList")
//            Log.d(TAG, "bindHandlersSelectedItem: $irrigation_selected")
//            Log.d(TAG, "bindHandlersYear: $year_selected")
//    }

    private fun showCalender() {


        var date: DatePickerDialog.OnDateSetListener? =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                myCalendar.add(Calendar.YEAR, 0);
                view.setMinDate(myCalendar.timeInMillis)
                updateLabel(myCalendar)
                myCalendar.add(Calendar.YEAR, 0)
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