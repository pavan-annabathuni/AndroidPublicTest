package com.waycool.featurecrophealth.ui.details

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.waycool.data.utils.SpeechToText
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropSelectBinding
import com.waycool.featurecropprotect.Adapter.MyCropsAdapter
import kotlinx.coroutines.launch
import java.util.*


class CropSelectFragment : Fragment() {
    private var _binding: FragmentCropSelectBinding? = null
    private val binding get() = _binding!!
    private val adapter: CropListAdapter by lazy { CropListAdapter() }
    private var selectedCategory: CropCategoryMasterDomain? = null

    private var str: String? = null

    private val viewModel by lazy { ViewModelProvider(this)[CropHealthViewModel::class.java] }

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = ""
    private lateinit var myCropAdapter: MyCropsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCropSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerviewDetails.adapter = adapter
        bindObserversCategory()
//        bindObserversDetails()
        initView()
        translationPestAndDisease()

        clickSearch()

        adapter.onItemClick = {
            val eventBundle = Bundle()
            eventBundle.putString("cropName", it?.cropName)
            if (selectedCategory != null) {
                eventBundle.putString("selectedCategory", selectedCategory?.categoryName)
            }
            EventItemClickHandling.calculateItemClickEvent("crophealth_landing", eventBundle)
            val bundle = Bundle()
            it?.cropId?.let { it1 -> bundle.putInt("crop_id", it1) }
            bundle.putString("name", it?.cropName)
            bundle.putString("crop_logo", it?.cropLogo)
            bundle.putString("TagCrop", it?.cropNameTag)
            findNavController().navigate(
                R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
                bundle
            )
        }

        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
            val id = it.cropId
            var id2 = 0
            val args = Bundle()
            it.idd?.let { it1 -> args.putInt("crop_id", it1) }
            it.cropName?.let { it1 -> args.putString("name", it1) }
            it.cropLogo?.let { it2 -> args.putString("crop_logo", it2) }
            viewModel.getCropMaster().observe(viewLifecycleOwner) {
                for (i in 0 until it.data?.size!!) {
                    Log.d("CropId", "onViewCreated: ${id} ${it.data?.get(i)?.cropId}")
                    if (it.data?.get(i)?.cropId == id) {
                        id2 = it.data?.get(i)?.cropId!!
                    }
                }
                if (id == id2) {
                    findNavController().navigate(
                        R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
                        args
                    )
                } else dialog()

            }

        })
        binding.rvMyCrops.adapter = myCropAdapter
        myCrops()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = activity?.let { findNavController().popBackStack() }
//                    if (!isSuccess) activity?.let { NavUtils.navigateUpFromSameTask(it) }
                }
            }
        activity?.let {
            activity?.onBackPressedDispatcher?.addCallback(
                it,
                callback
            )
        }
    }

    private fun myCrops() {

        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            myCropAdapter.submitList(it.data)
            binding.clProgressBar.visibility = View.GONE
            if ((it.data != null)) {
                binding.tvCount.text = it.data!!.size.toString()
            } else {
                binding.tvCount.text = "0"
            }
            // Log.d("MYCROPS", it.data?.get(0)?.cropLogo.toString())

        }
    }

  private  fun translationPestAndDisease() {
        viewModel.viewModelScope.launch {
            binding.searchView.hint = TranslationsManager().getString("search")
        }
        TranslationsManager().loadString("crop_selection", binding.tvToolBar, "Crop Selection")
        TranslationsManager().loadString("", binding.myCropsTitle, "My Crops")
    }


  private fun initView() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

        binding.micBtn.setOnClickListener {
            speechToText()
        }

    }

    private fun bindObserversCategory() {
        binding.clProgressBar.visibility = View.VISIBLE

        viewModel.getCropCategory().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    binding.cropCategoryChipGroup.removeAllViews()
                    binding.clProgressBar.visibility = View.GONE

                    selectedCategory = null
                    val categoryList = it.data
                    val allCategory = CropCategoryMasterDomain(categoryName = "All")
                    createChip(allCategory)
                    if (categoryList != null) {
                        for (category in categoryList) {
                            createChip(category)
                        }
                    }
                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
                is Resource.Loading -> {
                    AppUtils.translatedToastLoading(context)
                }
            }
        }


    }

    private fun createChip(category: CropCategoryMasterDomain) {
        if(activity!=null) {
            val chip = Chip(activity)
            chip.text = category.categoryName
            chip.isCheckable = true
            chip.isClickable = true
            chip.isCheckedIconVisible = false
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
                com.waycool.uicomponents.R.color.bg_chip_text
            )

            if (selectedCategory == null) {
                chip.isChecked = true
                selectedCategory = category
                getSelectedCategoryCrops(
                    categoryId = category.id,
                    // searchQuery = searchCharSequence.toString()
                )
            }


            chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
                if (b) {
                    selectedCategory = category
                    getSelectedCategoryCrops(
                        categoryId = category.id,
                        //   searchQuery = searchCharSequence.toString()
                    )
                }
            }
            binding.cropCategoryChipGroup.addView(chip)
        }
    }

    private fun getSelectedCategoryCrops(categoryId: Int? = null, searchQuery: String? = "") {
        binding.clProgressBar.visibility = View.VISIBLE

        viewModel.getCropMaster(searchQuery).observe(requireActivity()) { res ->
            when (res) {
                is Resource.Success -> {
                    binding.clProgressBar.visibility = View.GONE
                    if (categoryId == null) {
                        adapter.submitList(res.data)
                    } else
                        adapter.submitList(res.data?.filter { it.cropCategory_id == categoryId })
                }
                is Resource.Loading -> {
                    AppUtils.translatedToastLoading(context)

                }
                is Resource.Error -> {

                    AppUtils.translatedToastServerErrorOccurred(context)

                }
            }
        }
    }


    private fun clickSearch() {

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getSelectedCategoryCrops(
                    // categoryId = selectedCategory?.id,
                    searchQuery = searchCharSequence.toString()
                )
            }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isEmpty()) {
                    bindObserversCategory()
                }
                EventClickHandling.calculateClickEvent("Search_crophealth")
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    private fun speechToText() {
        EventClickHandling.calculateClickEvent("STT_crophealth")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        viewModel.viewModelScope.launch {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, SpeechToText.getLangCode())
        }
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast
                .makeText(requireActivity(), " " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                searchCharSequence = result?.get(0).toString()
                binding.searchView.setText(searchCharSequence)

            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 1
    }

    private fun dialog() {

        val dialog = Dialog(requireContext())
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_information)
        // val body = dialog.findViewById(R.id.body) as TextView
        val yesBtn = dialog.findViewById(R.id.ok) as Button
        val tvInformation = dialog.findViewById(R.id.textView14) as TextView
        val tvMessage = dialog.findViewById(R.id.textView15) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            Log.d("Dialog", "dialog: Clicked")
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        TranslationsManager().loadString("str_information", tvInformation, "Information")
        TranslationsManager().loadString(
            "str_crop_health_message",
            tvMessage,
            "Thanks for showing your interest. Currently, we’re working on a pest & disease detection model for this crop. We look forward to serving you shortly."
        )
        viewModel.viewModelScope.launch {
            var ok = TranslationsManager().getString("str_ok")
            if (ok.isNullOrEmpty())
                yesBtn.text = "Ok"
        }
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropSelectFragment")
    }
}