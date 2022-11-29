package com.waycool.featurecrophealth.ui.details

import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.utils.Resource
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropSelectBinding
import com.waycool.featurecropprotect.Adapter.MyCropsAdapter
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

        clickSearch()

        adapter.onItemClick = {
            val bundle = Bundle()
            it?.cropId?.let { it1 -> bundle.putInt("crop_id", it1) }
            bundle.putString("name", it?.cropName)
            bundle.putString("crop_logo", it?.cropLogo)

            findNavController().navigate(
                R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
                bundle
            )
        }

        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
            val args = Bundle()
            it?.idd?.let { it1 -> args.putInt("crop_id", it1) }
            it?.cropName?.let { it1 -> args.putString("name", it1) }
            it?.cropLogo?.let { it2 -> args.putString("crop_logo", it2) }
            findNavController().navigate(
                R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
                args
            )
        })
        binding.rvMyCrops.adapter = myCropAdapter
        myCrops()
    }

    fun myCrops() {

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            var accountId = it.data?.accountId

            if (accountId != null)
                viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
                    myCropAdapter.submitList(it.data)
                    if ((it.data != null)) {
                        binding.tvCount.text = it.data!!.size.toString()
                    } else {
                        binding.tvCount.text = "0"
                    }
                    // Log.d("MYCROPS", it.data?.get(0)?.cropLogo.toString())

                }
        }
    }


    fun initView() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

        binding.micBtn.setOnClickListener {
            speechToText()
        }

    }

    private fun bindObserversCategory() {
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


//    private fun bindObserversDetails() {
//        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer {
//
//            when (it) {
//                is NetworkResult.Success -> {
//                    Log.d(Constant.TAG, "bindObserversDataDetails:" + it.data?.data.toString())
//                    val response =
//                        it.data?.data as List<com.waycool.featurecrophealth.model.cropdetails.Data>
////                    detailsAdapter.setMovieList(response)
//                    responseDataList = response as MutableList<Data>
//
//                    cropDetailsList = CropDetails(responseDataList, "", true)
//
//                    binding.apply {
//                        detailsAdapter = DetailsAdapter(this@CropSelectFragment)
//                        recyclerviewDetails.adapter = detailsAdapter
//                        detailsAdapter.setMovieList(responseDataList)
//                        categoryAdapter.notifyDataSetChanged()
//                    }
//
//                }
//                is NetworkResult.Error -> {
//                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                        .show()
//                }
//                is NetworkResult.Loading -> {
//
//
//                }
//            }
//        })
//
//
//    }

//    override fun clickOnDetails(data: Data) {
//        Log.wtf("get_arg:", "Item is clicked" + data.crop_category_id)
//        val bundle = Bundle()
//        bundle.putInt("crop_id", data.crop_id)
//        bundle.putString("name", data.crop_name)
//        bundle.putString("crop_logo", data.crop_logo)
//
//
//        Log.wtf("get_arg:", "SendingData" + data.crop_name.toString())
//        findNavController().navigate(
//            R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
//            bundle
//        )
//    }
//
//    override fun clickOnCategory(categoryDate: com.waycool.featurecrophealth.model.cropcate.Data) {
//        var detailsListArea = ArrayList<Data>()
//        responseDataList.forEach {
//            if (it.crop_category_id.equals(categoryDate.id)) {
//                detailsListArea.add(it)
//            }
//        }
//        binding.all.setTextColor(Color.parseColor("#111827"))
//        binding.all.setBackgroundResource(R.drawable.bd_flex)
//        detailsAdapter.setMovieList(detailsListArea)
//        detailsAdapter.notifyDataSetChanged()
////        binding.skillName.setBackgroundResource(R.drawable.bg_details)
//
//        Log.d(TAG, "clickOnCategory: " + categoryDate.category_name)
//        Log.d(TAG, "clickOnCategoryName: $str")
//    }

    private fun clickSearch() {

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getSelectedCategoryCrops(
                    categoryId = selectedCategory?.id,
                    searchQuery = searchCharSequence.toString()
                )
            }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

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
                binding.searchView.setText(searchCharSequence)

            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 1
    }


}