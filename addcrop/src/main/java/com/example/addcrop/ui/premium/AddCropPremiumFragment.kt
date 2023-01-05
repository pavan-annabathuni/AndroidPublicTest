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
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentAddCropPremiumBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.google.android.material.chip.Chip
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddCropPremiumFragment : Fragment() {
    private var accountID: Int? = null
    private var _binding: FragmentAddCropPremiumBinding? = null
    private val binding get() = _binding!!
    val myCalendar = Calendar.getInstance()
    private var selectedFarmId: Int? = null
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
    val noOFYearsBahar = arrayOf(
        "0-1",
        "1-2",
        "2-3",
        "3-4"
    )
    val noOFYear = arrayOf(
        "Select",
        "0-1",
        "1-2",
        "2-3",
        "3-4",
        "4-5"
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
        noOFYear()
        noOFYearBahar()
        translationSoilTesting()
//        getFarms()
        year_selected= "0".toString().toInt().toString()
//        binding.cardCheckHealth.setOnClickListener {
//            postDataAddCrop()
//        }
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountID = it.data?.accountId
            getFarms()

        }
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


    fun initView() {
        if (arguments != null) {
            crop_id = arguments?.getInt("cropid")
            crop_type = arguments?.getInt("soil_type_id")

            if (crop_id == 97) {
//                binding.clSwitch.visibility = View.VISIBLE
                binding.EnterDateoffruitPruning.visibility = View.VISIBLE
                binding.Address.visibility = View.INVISIBLE
                binding.tvYearShow.visibility = View.VISIBLE
                binding.clSpinnerYear.visibility = View.VISIBLE

            } else if (crop_id == 67) {

                binding.FirstIrrigationDate.visibility = View.VISIBLE
                binding.Address.visibility = View.INVISIBLE
                binding.tvBahar.visibility = View.VISIBLE
                binding.clSpinnerYearBahar.visibility = View.VISIBLE
                binding.tvYearShow.visibility = View.VISIBLE
                binding.clSpinnerYear.visibility = View.VISIBLE
                binding.clSwitchMulching.visibility = View.VISIBLE

            }

            viewModel.getUserDetails().observe(viewLifecycleOwner) {
                accountID = it.data?.accountId
                irrigationTypeSpinner(accountID, crop_id, crop_type)
                Log.d(ContentValues.TAG, "onCreateViewONPIDPrinteddvsv: $crop_id")
                Log.d(ContentValues.TAG, "onCreateViewONPIDPrinteddvsv: $crop_type")
            }

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
        binding.clCalender.setOnClickListener {
            showCalender()
        }
//        binding.cardCheckHealth.setOnClickListener {
//
//        }
    }

    private fun spinnerYear() {
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        binding.Acres.adapter = arrayAdapter
        binding.Acres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = p0?.selectedItem
                areaTypeSelected = item.toString()
//                year_selected = item.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun noOFYearBahar() {
        val arrayAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                noOFYearsBahar
            )
        binding.tvSpinnerYearBahar.adapter = arrayAdapter
        binding.tvSpinnerYearBahar.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val item = p0?.selectedItem
//                    year_selected = item.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun noOFYear() {
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, noOFYear)
        binding.tvSpinnerYear.adapter = arrayAdapter
        binding.tvSpinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = p0?.selectedItem
                year_selected = item.toString()
                if (year_selected == "Select") {
                    year_selected = "0"
                } else if (year_selected == "0-1") {
                    year_selected = "1"
                } else if (year_selected == "1-2") {
                    year_selected = "2"
                } else if (year_selected == "2-3") {
                    year_selected = "3"
                }else if (year_selected == "3-4") {
                    year_selected = "4"
                }else if (year_selected == "4-5") {
                    year_selected = "5"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun irrigationTypeSpinner(account_id: Int?, crop_id: Int?, soil_type_id: Int?) {
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
                        Toast.makeText(
                            requireContext(),
                            "Please Irrigation Type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (colors[1] == (irrigation_selected)) {
                    binding.tvCheckCrop.setText("Next")
                    binding.cardCheckHealth.setOnClickListener {
                        it.hideSoftInput()
                        nickName = binding.etNickName.text.toString().trim()
                        area = binding.etAreaNumber.text.toString().trim()
                        numberOfPlanets = binding.etNoOfAcre.text.toString().trim()
                        date = binding.etCalender.text.toString().trim()
                        if (nickName.isEmpty()) {
                            binding.etNickName.error = "Nick name should not be empty"
                            return@setOnClickListener
                        } else if (area.isEmpty()) {
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

                            if (account_id != null) {
                                bundle.putInt("account_id", account_id)
                            }
                            if (crop_id != null) {
                                bundle.putInt("cropid", crop_id)
                            }
                            if (soil_type_id != null) {
                                bundle.putInt("crop_type", soil_type_id)
                            }
                            bundle.putString("area_type",areaTypeSelected)
                            bundle.putString("nick_name", nickName)
                            bundle.putString("area", area)
                            bundle.putString("date", date)
                            bundle.putString("irrigation_selected", irrigation_selected)
                            bundle.putString("numberOfPlanets", numberOfPlanets)
//                                Toast.makeText(requireContext(), "${account_id} Abd ${accountID}",Toast.LENGTH_SHORT).show()

                            if (accountID != null) {
                                bundle.putInt("account_id", accountID!!)
                            }
                            if (crop_id != null) {
                                bundle.putInt("cropid", crop_id)
                            }
                            if (soil_type_id != null) {
                                bundle.putInt("crop_type", soil_type_id)
                            }
                            if (selectedFarmId!=null){
                                bundle.putInt("farm_id", selectedFarmId!!)
                            }


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
                    itemClicked(account_id, crop_id, soil_type_id, item)
//                    else if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
//                        Toast.makeText(requireContext(), "Api Call Success 2", Toast.LENGTH_SHORT).show()
//                    }


                } else if (colors[3] == (item)) {
                    binding.clPlotNumber.visibility = View.VISIBLE
                    binding.plotNumber.visibility = View.VISIBLE
                    binding.tvCheckCrop.setText("Save Crop")
                    itemClicked(account_id, crop_id, soil_type_id, item)
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
    fun translationSoilTesting() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("add_crop")
            binding.toolbarTitle.text = title
            var NickNamehint = TranslationsManager().getString("e_g_crop_name")
            binding.etNickName.hint =NickNamehint
            var areaHint = TranslationsManager().getString("e_g_50")
            binding.etAreaNumber.hint =areaHint
            var hitnPlant = TranslationsManager().getString("e_g_50")
            binding.etNoOfAcre.hint =hitnPlant
        }
        TranslationsManager().loadString("add_crop_information", binding.plot)
        TranslationsManager().loadString("crop_nickname", binding.plotNumber)
        TranslationsManager().loadString("crop_area", binding.pincodeNumber)
        TranslationsManager().loadString("sowing_date", binding.Address)
        TranslationsManager().loadString("submit", binding.tvCheckCrop)
        TranslationsManager().loadString("select_irrigation", binding.tvselectIrrigation)
        TranslationsManager().loadString("irrigation_type", binding.City)
        TranslationsManager().loadString("no_of_plants_per_acre", binding.State)
        TranslationsManager().loadString("save_crop", binding.tvCheckCrop)
        TranslationsManager().loadString("select_farm_to_add", binding.paragraphMedium)
        TranslationsManager().loadString("bahar", binding.tvBahar)
        TranslationsManager().loadString("crop_year", binding.tvYearShow)
        TranslationsManager().loadString("first_irrigation", binding.FirstIrrigationDate)
        TranslationsManager().loadString("mulching", binding.tvShapeInFarmMulching)
        TranslationsManager().loadString("enter_date", binding.EnterDateoffruitPruning)

    }


    private fun itemClicked(
        account_id: Int?,
        crop_id: Int?,
        soil_type_id: Int?,
        irrigation_type: String
    ) {
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
                ToastStateHandling.toastError(requireContext(), "Incorrect Id", Toast.LENGTH_SHORT)
            } else if (numberOfPlanets.isEmpty()) {
                binding.etNoOfAcre.error = "Enter Number of Planets"
                return@setOnClickListener
            } else if (nickName.isNotEmpty() && area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
                val map = mutableMapOf<String, Any>()
                Log.d("TAG", "itemClickedjnvjndkfnvk:$account_id ")
                if (account_id != null) {
                    map["account_no_id"] = account_id
                }
                if (crop_id != null) {
                    map["crop_id"] = crop_id
                }
                if (soil_type_id != null) {
                    map["soil_type_id"] = soil_type_id
                }
                if (selectedFarmId != null)
                    map["farm_id"] = selectedFarmId!!
                map["crop_year"] = year_selected
                map["area_type"] = areaTypeSelected.lowercase()
                Log.d("TAG", "itemClickedBHSCbjzdnjvnbtn: $year_selected")
                map["plot_nickname"] = binding.etNickName.text.toString()
                map["area"] = binding.etAreaNumber.text
                map["irrigation_type"] = irrigation_type
                map["sowing_date"] = binding.etCalender.text.toString()
                map["no_of_plants"] = binding.etNoOfAcre.text.toString()
                Log.d("TAG", "itemClickedBHSCbjzdnjvn: $map")
                viewModel.addCropDataPass(map).observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            activity?.finish()
                        }
                        is Resource.Error -> {
                            ToastStateHandling.toastError(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                            Log.d(
                                ContentValues.TAG,
                                "postAddCropExption: ${it.message.toString()}"
                            )
                        }
                        is Resource.Loading -> {
                            ToastStateHandling.toastWarning(
                                requireContext(),
                                "Loading",
                                Toast.LENGTH_SHORT
                            )


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