package com.example.addcrop.ui.premium

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddCropPremiumFragment : Fragment() {
    private var cropCategoryTagName: String? = null
    private var cropNameTag: String? = null
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
    var crop_variety:Int?=null
    var crop_type: Int? = null
    val arrayList = ArrayList<String>()
    lateinit var irrigation_selected: String
    lateinit var year_selected: String
    lateinit var areaTypeSelected: String
    var dateOfBirthFormat = SimpleDateFormat("yyyy-MM-dd")
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }

    val cropIrrigationList = arrayOf(
        "Select Irrigation method",
        "Drip Irrigation",
        "Sprinkler Irrigation",
        "Flood Irrigation"
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
        initViewClicks()
        observeSelectCropYear()
        observeSelectCopUnit()
        observeSelectYearBahar()
        translationAddCropPremiumPage()
        year_selected = "0".toInt().toString()

        if (requireActivity().intent.extras != null) {
            val bundle = requireActivity().intent.extras
            selectedFarmId = bundle?.getInt("farmID")
            if (selectedFarmId == null) {
                binding.farmsCl.visibility = View.VISIBLE
                binding.paragraphMedium.visibility = View.VISIBLE
                binding.myfarmsChipGroup.visibility = View.VISIBLE
            } else {
                binding.farmsCl.visibility = View.GONE
                binding.paragraphMedium.visibility = View.GONE
                binding.myfarmsChipGroup.visibility = View.GONE
            }
        } else {
            binding.farmsCl.visibility = View.VISIBLE
            binding.paragraphMedium.visibility = View.VISIBLE
            binding.myfarmsChipGroup.visibility = View.VISIBLE
            viewModel.getUserDetails().observe(viewLifecycleOwner) {
                accountID = it.data?.accountId
                getFarms()

            }
        }

        binding.toolbar.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun observeSelectYearBahar() {
        viewModel.selectNumberOfYearBahar.observe(viewLifecycleOwner, androidx.lifecycle.Observer {cropBaharYear->
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cropBaharYear)
            binding.tvSpinnerYearBahar.adapter = arrayAdapter
            binding.tvSpinnerYearBahar.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { p0?.selectedItem }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        })
    }

    private fun observeSelectCopUnit() {
        viewModel.selectCropUnit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,it )
            binding.Acres.adapter = arrayAdapter
            binding.Acres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val item = p0?.selectedItem
                    areaTypeSelected = item.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        })

    }

    private fun observeSelectCropYear() {
        viewModel.selectCropYear.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
            binding.tvSpinnerYear.adapter = arrayAdapter
            binding.tvSpinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val item = p0?.selectedItem
                    year_selected = item.toString()
                    when (year_selected) {
                        "Select" -> {
                            year_selected = "0"
                        }
                        "0-1" -> {
                            year_selected = "1"
                        }
                        "1-2" -> {
                            year_selected = "2"
                        }
                        "2-3" -> {
                            year_selected = "3"
                        }
                        "3-4" -> {
                            year_selected = "4"
                        }
                        "4-5" -> {
                            year_selected = "5"
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        })

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
                    }
                    is Resource.Error -> {
                        binding.farmsCl.visibility = View.GONE
                    }
                    else -> {

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
           crop_variety=arguments?.getInt("pom")

            crop_type = arguments?.getInt("soil_type_id")
            cropNameTag = arguments?.getString("cropNameTag")
            cropCategoryTagName = arguments?.getString("selectedCategory")

            if (crop_id == 97) {
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
            }

        }

    }

    private fun initViewClicks() {
        binding.clCalender.setOnClickListener {
            showCalender()
        }
    }


    private fun irrigationTypeSpinner(account_id: Int?, crop_id: Int?, soil_type_id: Int?) {
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cropIrrigationList)
        binding.tvSpinner.adapter = arrayAdapter
        binding.tvSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = p0?.selectedItem.toString()
                irrigation_selected = item

                if (cropIrrigationList[0] == irrigation_selected) {
                    binding.btnSaveCropDetails.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            "Please Add Irrigation Type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (cropIrrigationList[1] == (irrigation_selected)) {
//                    binding.tvCheckCrop.setText("Next")
                    TranslationsManager().loadString("next", binding.tvCheckCrop)
                    binding.btnSaveCropDetails.setOnClickListener {
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
                            binding.etNoOfAcre.error = "Enter Number of Plants per Acre"
                            return@setOnClickListener
                        } else if (area.isNotEmpty() && date.isNotEmpty() && numberOfPlanets.isNotEmpty()) {
                            val bundle = Bundle()
                            if (account_id != null) {
                                bundle.putInt("account_id", account_id)
                            }
                            if (crop_id != null) {
                                bundle.putInt("cropid", crop_id)
                            }
                            if (soil_type_id != null) {
                                bundle.putInt("crop_type", soil_type_id)
                            }
                            bundle.putString("area_type", areaTypeSelected)
                            bundle.putString("nick_name", nickName)
                            bundle.putString("area", area)
                            bundle.putInt("pom",crop_variety.toString().toInt())
                            bundle.putString("date", date)
                            bundle.putString("irrigation_selected", irrigation_selected)
                            bundle.putString("numberOfPlanets", numberOfPlanets)

                            if (accountID != null) {
                                bundle.putInt("account_id", accountID!!)
                            }
                            if (crop_id != null) {
                                bundle.putInt("cropid", crop_id)
                            }
                            if (soil_type_id != null) {
                                bundle.putInt("crop_type", soil_type_id)
                            }
                            if (selectedFarmId != null) {
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
                } else if (cropIrrigationList[2] == (item)) {
                    TranslationsManager().loadString("save_crop", binding.tvCheckCrop)
                    binding.clCropNickname.visibility = View.VISIBLE
                    binding.plotNumber.visibility = View.VISIBLE
                    binding.tvCheckCrop.text = "Save Crop"
                    cropFieldVerification(account_id, crop_id, soil_type_id, item,crop_variety)
                } else if (cropIrrigationList[3] == (item)) {
                    TranslationsManager().loadString("save_crop", binding.tvCheckCrop)
                    binding.clCropNickname.visibility = View.VISIBLE
                    binding.plotNumber.visibility = View.VISIBLE
                    binding.tvCheckCrop.text = "Save Crop"
                    cropFieldVerification(account_id, crop_id, soil_type_id, item,crop_variety)
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

   private fun translationAddCropPremiumPage() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("add_crop")
            binding.toolbarTitle.text = title
            val NickNamehint = TranslationsManager().getString("e_g_crop_name")
            binding.etNickName.hint = NickNamehint
            val areaHint = TranslationsManager().getString("e_g_50")
            binding.etAreaNumber.hint = areaHint
            val hitnPlant = TranslationsManager().getString("e_g_50")
            binding.etNoOfAcre.hint = hitnPlant
        }
        TranslationsManager().loadString(
            "add_crop_information",
            binding.plot,
            "Add Crop information"
        )
        TranslationsManager().loadString("crop_nickname", binding.plotNumber, "Crop Nickname")
        TranslationsManager().loadString("crop_area", binding.pincodeNumber, "Crop Area")
        TranslationsManager().loadString("sowing_date", binding.Address, "Sowing Date")
        TranslationsManager().loadString("submit", binding.tvCheckCrop, "Submit")
        TranslationsManager().loadString("mulching", binding.tvShapeInFarmMulching, "Mulching")
        TranslationsManager().loadString("shade_farm", binding.tvShapeInFarm, "Shade in farm")
        TranslationsManager().loadString("tea_coffee", binding.tvOnlyForTea, "(only for tea and coffee)")
        TranslationsManager().loadString("first_irrigation", binding.FirstIrrigationDate, "First Irrigation of cycle start")
        TranslationsManager().loadString("enter_date", binding.EnterDateoffruitPruning, "Enter Date of fruit Pruning")
        TranslationsManager().loadString("bahar", binding.tvBahar, "Bahar")
        TranslationsManager().loadString("crop_year", binding.tvYearShow, "Crop Year")






        TranslationsManager().loadString(
            "select_irrigation",
            binding.tvselectIrrigation,
            "Select Irrigation"
        )
        TranslationsManager().loadString("irrigation_type", binding.City, "Irrigation Type")
        TranslationsManager().loadString(
            "no_of_plants_per_acre",
            binding.State,
            "No of Plants Per Acre"
        )
//        TranslationsManager().loadString("save_crop", binding.tvCheckCrop,"")
        TranslationsManager().loadString(
            "select_farm_to_add",
            binding.paragraphMedium,
            "Select Farm to add this crop"
        )
        TranslationsManager().loadString("bahar", binding.tvBahar, "Bahar")
        TranslationsManager().loadString("crop_year", binding.tvYearShow, "Crop Year")
        TranslationsManager().loadString(
            "first_irrigation",
            binding.FirstIrrigationDate,
            "First Irrigation of cycle start"
        )
        TranslationsManager().loadString("mulching", binding.tvShapeInFarmMulching, "Mulching")
        TranslationsManager().loadString(
            "enter_date",
            binding.EnterDateoffruitPruning,
            "Enter Date of fruit Pruning"
        )

    }


    private fun cropFieldVerification(
        account_id: Int?,
        crop_id: Int?,
        soil_type_id: Int?,
        irrigation_type: Any,
        crop_variety:Int?

    ) {
        binding.btnSaveCropDetails.setOnClickListener { it ->
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
                if (account_id != null) {
                    map["account_no_id"] = account_id
                }
                if (crop_id != null) {
                    map["crop_id"] = crop_id
                }
                if (crop_variety==0 ){

                }else{
                    map["crop_variety_id"]=crop_variety.toString().toInt()
                }

                if (soil_type_id != null) {
                    map["soil_type_id"] = soil_type_id
                }
                if (selectedFarmId != null)
                    map["farm_id"] = selectedFarmId!!
                map["crop_year"] = year_selected
                map["area_type"] = areaTypeSelected.lowercase()
                map["plot_nickname"] = binding.etNickName.text.toString()
                map["area"] = binding.etAreaNumber.text
                map["irrigation_type"] = irrigation_type
                map["sowing_date"] = binding.etCalender.text.toString()
                map["no_of_plants"] = binding.etNoOfAcre.text.toString()
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSaveCropDetails.visibility = View.GONE
                val eventBundle = Bundle()
                eventBundle.putString("crop_name", cropNameTag.toString())
                eventBundle.putString("cropCategoryTagName", cropCategoryTagName.toString())
                eventBundle.putString("soil_type_id", soil_type_id.toString())
                eventBundle.putString("plot_nickname", binding.etNickName.text.toString())
                eventBundle.putString("area", binding.etAreaNumber.text.toString())
                eventBundle.putString("irrigation_type", irrigation_type.toString())
                eventBundle.putString("no_of_plants", binding.etNoOfAcre.text.toString())

                EventItemClickHandling.calculateItemClickEvent("Add_cropPremium", eventBundle)


                viewModel.addCropDataPass(map).observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.btnSaveCropDetails.visibility = View.VISIBLE
                            activity?.finish()
                        }
                        is Resource.Error -> {
                            AppUtils.translatedToastServerErrorOccurred(context)

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
        val date: DatePickerDialog.OnDateSetListener? =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                myCalendar.add(Calendar.YEAR, 0)
                view.minDate = myCalendar.timeInMillis
                updateLabel(myCalendar)
                myCalendar.add(Calendar.YEAR, 0)
                view.maxDate = myCalendar.timeInMillis
            }
        val dialog = DatePickerDialog(
            requireContext(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dateCrop = dateOfBirthFormat.format(myCalendar.time)
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
        binding.etCalender.text = dateFormat.format(myCalendar.time)
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

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AddCropPremiumFragment")
    }
}