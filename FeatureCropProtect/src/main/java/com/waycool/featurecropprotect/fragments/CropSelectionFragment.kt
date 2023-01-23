package com.waycool.cropprotect.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.data.utils.SpeechToText
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecropprotect.Adapter.CropListAdapter
import com.waycool.featurecropprotect.Adapter.MyCropsAdapter
import com.waycool.featurecropprotect.CropProtectViewModel
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.FragmentCropSelectionBinding
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import kotlinx.coroutines.launch
import java.util.*


class CropSelectionFragment : Fragment() {
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private var selectedCategory: CropCategoryMasterDomain? = null
    private var _binding: FragmentCropSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var myCropAdapter: MyCropsAdapter

    private val viewModel: CropProtectViewModel by lazy {
        ViewModelProvider(requireActivity())[CropProtectViewModel::class.java]
    }
    private val adapter: CropListAdapter by lazy { CropListAdapter() }

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCropSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarTitle.text="Protect your Crop"

        apiErrorHandlingBinding=binding.errorState

        binding.toolbar.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) NavUtils.navigateUpFromSameTask(requireActivity())
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) NavUtils.navigateUpFromSameTask(requireActivity())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        TranslationsManager().loadString("protect_your_crop",binding.toolbarTitle)
        TranslationsManager().loadString("crop_protect_info",binding.cropProtectInfo)
        TranslationsManager().loadString("my_crops",binding.myCropsTitle)

        binding.cropsRv.adapter = adapter
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
            val args = Bundle()
            it?.idd?.let { it1 -> args.putInt("cropid", it1) }
            it?.cropName?.let { it1 -> args.putString("cropname", it1) }
            if(selectedCategory!=null){
            args.putString("selectedCategory",selectedCategory?.categoryName)}
            findNavController().navigate(
                R.id.action_cropSelectionFragment_to_pestDiseaseFragment,
                args
            )
        })
        binding.rvMyCrops.adapter = myCropAdapter
        fabButton()

        myCrops()
        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getSelectedCategoryCrops(
                    searchQuery = searchCharSequence.toString()
                )
            }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                EventClickHandling.calculateClickEvent("Search_crop_protection")
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        adapter.onItemClick = {
            val eventBundle=Bundle()
            eventBundle.putString("cropName",it?.cropName)
            if(selectedCategory!=null){
                eventBundle.putString("selectedCategory",selectedCategory?.categoryName)
            }
            EventItemClickHandling.calculateItemClickEvent("cropprotection_landing",eventBundle)
            val args = Bundle()
            it?.cropId?.let { it1 -> args.putInt("cropid", it1) }
            it?.cropName?.let { it1 -> args.putString("cropname", it1) }
            findNavController().navigate(
                R.id.action_cropSelectionFragment_to_pestDiseaseFragment,
                args
            )
        }
        binding.micBtn.setOnClickListener {
            speechToText()
        }
        setUpCropCategories()

        viewModel.downloadPestAndDiseases()

    }

    private fun setUpCropCategories() {
        binding.clProgressBar.visibility=View.VISIBLE

        viewModel.getCropCategory().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.clProgressBar.visibility=View.GONE

                    binding.cropCategoryChipGroup.removeAllViews()
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
                    ToastStateHandling.toastError(requireContext(), "Error", Toast.LENGTH_SHORT)

                }
                is Resource.Loading -> {
                    ToastStateHandling.toastWarning(requireContext(), "Loading", Toast.LENGTH_SHORT)

                }
            }
        }
    }

    private fun createChip(category: CropCategoryMasterDomain) {
        if(activity!=null){
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
                    categoryId = category.id
                )
            }

            chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
                if (b) {
                    selectedCategory = category
                    getSelectedCategoryCrops(
                        categoryId = category.id
                    )
                }
            }
            binding.cropCategoryChipGroup.addView(chip)
        }



    }

    private fun getSelectedCategoryCrops(categoryId: Int? = null, searchQuery: String? = "") {
        binding.clProgressBar.visibility=View.VISIBLE
        viewModel.getCropMaster(searchQuery).observe(requireActivity()) { res ->
            when (res) {
                is Resource.Success -> {
                    if (categoryId == null) {
                        binding.clProgressBar.visibility=View.GONE
                        adapter.submitList(res.data)
                    } else {
                        binding.clProgressBar.visibility=View.GONE
                        adapter.submitList(res.data?.filter { it.cropCategory_id == categoryId })
                    }
                }
                is Resource.Loading -> {
                    binding.clProgressBar.visibility=View.VISIBLE
                }
                is Resource.Error -> {
                    binding.clProgressBar.visibility=View.GONE
                    ToastStateHandling.toastError(requireContext(), "Error Occurred", Toast.LENGTH_SHORT)
                }
            }
        }
    }


    private fun speechToText() {
        EventClickHandling.calculateClickEvent("STT_crop_protection")

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
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,SpeechToText.getLangCode())
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
                binding.search.setText(searchCharSequence)

            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 1
    }

    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener() {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener() {
            EventClickHandling.calculateClickEvent("call_icon")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener() {
            EventClickHandling.calculateClickEvent("chat_icon")
            FeatureChat.zenDeskInit(requireContext())
        }
    }

    fun myCrops() {
        binding.clProgressBar.visibility=View.VISIBLE
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            myCropAdapter.submitList(it.data)
            if ((it.data?.size!=0)) {
                binding.cvMyCrops.visibility=View.VISIBLE
                binding.tvCount.text = it.data!!.size.toString()
            } else {
                binding.cvMyCrops.visibility=View.GONE
            }
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropSelectionFragment")
    }
}