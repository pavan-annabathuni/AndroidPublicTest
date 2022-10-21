package com.example.addcrop.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentSelectAddCropBinding
import com.example.addcrop.viewmodel.SelectAddCropViewModel
import com.google.android.material.chip.Chip
import com.waycool.data.Repository.DomainModels.CropCategoryMasterDomain
import com.waycool.data.utils.Resource
import java.util.*


class SelectAddCropFragment : Fragment() {

    private var selectedCategory: CropCategoryMasterDomain? = null
    private lateinit var binding: FragmentSelectAddCropBinding
    private val viewModel: SelectAddCropViewModel by lazy {
        ViewModelProvider(requireActivity())[SelectAddCropViewModel::class.java]

    }
    private val adapter: SelectCropAdapter by lazy { SelectCropAdapter() }

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectAddCropBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }
        binding.toolbarTitle.text = "Protect Your Crop"

        binding.cropsRv.adapter = adapter

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getSelectedCategoryCrops(
                    categoryId = selectedCategory?.id,
                    searchQuery = searchCharSequence.toString()
                )
            }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        adapter.onItemClick = {
            val args = Bundle()
            it?.cropId?.let { it1 -> args.putInt("cropid", it1) }
            it?.cropName?.let { it1 -> args.putString("cropname", it1)
                Log.d("TAG", "onViewCreatedbdjvb: ${it.cropId}")
            }
            findNavController().navigate(
                R.id.action_selectAddCropFragment_to_addCropDetailsFragment2,
                args
            )
        }

        binding.micBtn.setOnClickListener {
            speechToText()
        }
        setUpCropCategories()
    }

    private fun setUpCropCategories() {
        viewModel.getCropCategory().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()

                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun createChip(category: CropCategoryMasterDomain) {
        val chip = Chip(requireContext())
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
                searchQuery = searchCharSequence.toString()
            )
        }


        chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                selectedCategory = category
                getSelectedCategoryCrops(
                    categoryId = category.id,
                    searchQuery = searchCharSequence.toString()
                )
            }
        }
        binding.cropCategoryChipGroup.addView(chip)
    }

    private fun getSelectedCategoryCrops(categoryId: Int? = null, searchQuery: String? = "") {
        viewModel.getCropMaster(searchQuery).observe(requireActivity()) { res ->
            when (res) {
                is Resource.Success -> {
                    if (categoryId == null) {
                        adapter.submitList(res.data)
                    } else
                        adapter.submitList(res.data?.filter { it.cropCategory_id == categoryId })
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun speechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
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


}