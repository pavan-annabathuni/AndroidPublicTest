package com.example.addcrop.ui.free

import android.R
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.databinding.FragmentAddCropDetailsBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.google.android.material.chip.Chip
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.data.utils.AppUtils.translatedToastCheckInternet
import com.waycool.data.utils.AppUtils.translatedToastLoading
import com.waycool.data.utils.AppUtils.translatedToastServerErrorOccurred
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddCropDetailsFragment : Fragment() {
    private var cropCategoryTagName: String? = null
    private var cropNameTag: String? = null
    private var selectedFarmId: Int? = null
    private var cropIdSelected: Int? = null
    private var accountID: Int? = null
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    lateinit var binding:FragmentAddCropDetailsBinding
    private val myCalendar = Calendar.getInstance()
    private var dateCrop: String = ""
    var area: String = ""
    var date: String = ""
    lateinit var areaTypeSelected: String

    private var cropSelectedDate = SimpleDateFormat("yyyy-MM-dd")
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }
    private val areaOfUnitList = arrayOf(
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
        binding = FragmentAddCropDetailsBinding.inflate(layoutInflater,container,false)
        binding.viewModel=viewModel
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiErrorHandlingBinding = binding.errorState
        networkErrorStateTranslations(apiErrorHandlingBinding)
        if (arguments != null) {
            cropIdSelected = arguments?.getInt("cropid")
            cropNameTag = arguments?.getString("cropNameTag")
            cropCategoryTagName = arguments?.getString("selectedCategory")
        }
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountID = it.data?.accountId
        }
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
            initObserveFarmDetails()
        }
        checkInternet()
        initViewClick()
        selectAreaUnit()
        translationAddCropForFreeUser()
        mvvmInit()
//        binding.viewModel=viewModel
    }

    //    private fun mvvmInit() {
//        viewModel.saveButtonPassData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            postCropDetails()
//        })
//        viewModel.calenderShow .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//         selectCropDateFromCalender()
//        })
//        viewModel.navigateBack.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            val isSuccess = findNavController().navigateUp()
//            if (!isSuccess) requireActivity().onBackPressed()
//        })
//
//
//    }
    private fun mvvmInit() {
        viewModel.navigation.observe(viewLifecycleOwner, androidx.lifecycle.Observer { action ->
                when (action) {
                    "SUBMIT_BUTTON" -> postCropDetails()
                    "CALENDER_SHOW" -> showCalendar()
                    "BACK_BUTTON" -> navigateBack()
                    else -> Log.w("mvvmInit", "Unexpected value: $action")
                }
            })
    }
    private fun navigateBack() {
        val isSuccess = findNavController().navigateUp()
        if (!isSuccess) requireActivity().onBackPressed()
    }


    private fun initViewClick() {
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            checkInternet()
        }

    }


    private fun checkInternet() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.cardSaveDetailsCrop.visibility = View.GONE
            translatedToastCheckInternet(context)

        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.cardSaveDetailsCrop.visibility = View.VISIBLE
        }
    }


    private fun initObserveFarmDetails() {
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
        if(activity!=null) {
            val chip = Chip(activity)
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
    }

    //select area unit
    private fun selectAreaUnit() {
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, areaOfUnitList)
        binding.selectAreaUnit.adapter = arrayAdapter
        binding.selectAreaUnit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val item = p0?.selectedItem
                    areaTypeSelected = item.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    //post crop details using retrofit
    private fun postCropDetails() {
        binding.progressBar.visibility = View.VISIBLE
        binding.cardSaveDetailsCrop.visibility = View.INVISIBLE
        val map = mutableMapOf<String, Any>()
        if (accountID != null)
            map["account_no_id"] = accountID!!
        if (cropIdSelected != null)
            map["crop_id"] = cropIdSelected!!
        map["plot_nickname"] = binding.etNicknameCrop.text.toString()
        map["sowing_date"] = binding.tvDateSelected.text.toString()
        map["area_type"] = areaTypeSelected.lowercase()
        map["area"] = binding.etCropArea.text
        if (selectedFarmId != null)
            map["farm_id"] = selectedFarmId!!

        val eventBundle = Bundle()
        eventBundle.putString("cropCategoryTagName", "Crop_category_${cropCategoryTagName}")
        eventBundle.putString("cropTagName", cropNameTag)
        eventBundle.putString("sowingDate", binding.tvDateSelected.text.toString())
        EventItemClickHandling.calculateItemClickEvent("Add_crop", eventBundle)
        viewModel.addCropDataPass(
            map
        ).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cardSaveDetailsCrop.visibility = View.VISIBLE
                    activity?.finish()
                    viewModel.getMyCrop2().observe(viewLifecycleOwner) {}

                }
                is Resource.Error -> {
//                    ToastStateHandling.toastError(requireContext(),it.message.toString(), Toast.LENGTH_LONG)
            translatedToastServerErrorOccurred(context)
                }
                is Resource.Loading -> {
                  translatedToastLoading(context)
                }
            }
        }


    }

    private fun showCalendar() {
        val date = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
        }

        val dialog = DatePickerDialog(
            requireContext(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum and maximum dates
        val minDate = Calendar.getInstance()
        minDate.set(Calendar.YEAR, minDate.get(Calendar.YEAR) - 1) // set the minimum year to the previous year
        dialog.datePicker.minDate = minDate.timeInMillis

        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.YEAR, maxDate.get(Calendar.YEAR) + 1) // set the maximum year to the next year
        dialog.datePicker.maxDate = maxDate.timeInMillis

        // Preview current year and previous and next years
//        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
//        val prevYear = currentYear - 1
//        val nextYear = currentYear + 1
//        val previewText = "Preview: $prevYear, $currentYear, $nextYear"
//        dialog.setTitle(previewText)

        dialog.show()

        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#7946A9"))
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#7946A9"))
    }

    //translation for this fragment
    private fun translationAddCropForFreeUser() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("add_crop")
            binding.toolbarTitle.text = title
            val nickNameHint = TranslationsManager().getString("e_g_crop_name")
            binding.etNicknameCrop.hint = nickNameHint
            val areaHint = TranslationsManager().getString("device_hint")
            binding.etCropArea.hint = areaHint
        }
        TranslationsManager().loadString(
            "add_crop_information",
            binding.tvAddCropInformation,
            "Add Crop information"
        )
        TranslationsManager().loadString("crop_nickname", binding.tvCropNickname, "Crop Nickname")
        TranslationsManager().loadString("crop_area", binding.tvCropArea, "Crop Area")
        TranslationsManager().loadString("sowing_date", binding.tvShowCalender, "Sowing Date")
        TranslationsManager().loadString("submit", binding.tvCheckCrop, "Submit")
        TranslationsManager().loadString(
            "select_farm_to_add",
            binding.paragraphMedium,
            "Select Farm to add this crop"
        )
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.tvDateSelected.text = dateFormat.format(myCalendar.time)
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AddCropDetailsFragment")
    }
}