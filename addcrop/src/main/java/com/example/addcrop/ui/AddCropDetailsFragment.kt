package com.example.addcrop.ui

import android.R
import android.app.DatePickerDialog
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
import com.example.addcrop.databinding.FragmentAddCropDetailsBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.google.android.material.chip.Chip
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddCropDetailsFragment : Fragment() {
    private var selectedFarmId: Int? = null
    private var cropIdSelected: Int? = null
    private var accountID: Int? = null
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private var _binding: FragmentAddCropDetailsBinding? = null
    private val binding get() = _binding!!
    private val myCalendar = Calendar.getInstance()
    private var dateCrop: String = ""
    private var nickName: String = ""
    var area: String = ""
    var date: String = ""
    val arrayList = ArrayList<String>()
    lateinit var areaTypeSelected: String

    private var dateOfBirthFormat = SimpleDateFormat("yyyy-MM-dd")
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }


    val colors = arrayOf(
        "Select Irrigation method",
        "Drip Irrigation",
        "Sprinkler Irrigation",
        "Flood Irrigation"
    )
    private val years = arrayOf(
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
        apiErrorHandlingBinding = binding.errorState
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        if (arguments != null) {
            cropIdSelected = arguments?.getInt("cropid")
        }

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountID = it.data?.accountId
            getFarms()

        }

        binding.cardCheckHealth.setOnClickListener {
            postAddCrop()
        }


//        spinner()
        spinnerYear()
        translations()
        binding.clCalender.setOnClickListener {
            showCalender()
        }
        binding.toolbar.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }


//        binding.cardCheckHealth.setOnClickListener {
//            viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                accountID = it.data?.accountId
//                postAddCrop(cropIdSelected!!, accountID!!)
//            }
//        }

    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.cardCheckHealth.visibility = View.GONE
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check internet connectivity",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.cardCheckHealth.visibility = View.VISIBLE
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
    private fun postAddCrop() {
        binding.progressBar.visibility = View.VISIBLE
        binding.cardCheckHealth.visibility = View.INVISIBLE
        val map = mutableMapOf<String, Any>()
        if (accountID != null)
            map["account_no_id"] = accountID!!
        if (cropIdSelected != null)
            map["crop_id"] = cropIdSelected!!
        map["plot_nickname"] = binding.etNickName.text.toString()
        map["sowing_date"] = binding.etCalender.text.toString()
        map["area_type"] = areaTypeSelected.lowercase()
        map["area"] = binding.etAreaNumber.text
        if (selectedFarmId != null)
            map["farm_id"] = selectedFarmId!!

        viewModel.addCropDataPass(
            map
        ).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cardCheckHealth.visibility = View.VISIBLE
                    activity?.finish()
                    viewModel.getMyCrop2().observe(viewLifecycleOwner) {}

                }
                is Resource.Error -> {
                    ToastStateHandling.toastError(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                }
                is Resource.Loading -> {
                    ToastStateHandling.toastWarning(requireContext(), "Loading", Toast.LENGTH_SHORT)

                }
            }
        }


    }


    private fun itemClicked(rop_id: Int) {
        binding.cardCheckHealth.setOnClickListener {
            it.hideSoftInput()
            nickName = binding.etNickName.text.toString().trim()
            area = binding.etAreaNumber.text.toString().trim()
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
            } else if (accountID == null) {
                ToastStateHandling.toastError(requireContext(), "Incorrect Id", Toast.LENGTH_SHORT)
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
    fun translations() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("add_crop")
            binding.toolbarTitle.text = title
            val nickNameHint = TranslationsManager().getString("e_g_crop_name")
            binding.etNickName.hint =nickNameHint
            val areaHint = TranslationsManager().getString("device_hint")
            binding.etAreaNumber.hint =areaHint
        }
        TranslationsManager().loadString("add_crop_information", binding.plot,"Add Crop information")
        TranslationsManager().loadString("crop_nickname", binding.plotNumber,"Crop Nickname")
        TranslationsManager().loadString("crop_area", binding.pincodeNumber,"Crop Area")
        TranslationsManager().loadString("sowing_date", binding.Address,"Sowing Date")
        TranslationsManager().loadString("submit", binding.tvCheckCrop,"Submit")
        TranslationsManager().loadString("select_farm_to_add", binding.paragraphMedium,"Select Farm to add this crop")
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.etCalender.text = dateFormat.format(myCalendar.time)
    }

    private fun View.hideSoftInput() {
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
        EventScreenTimeHandling.calculateScreenTime("AddCropDetailsFragment")
    }
}