package com.example.soiltesting.ui.request

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentSelectCropSoilTestBinding
import com.google.android.material.chip.Chip
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.utils.Resource
import java.util.*


class SelectCropSoilTestFragment : Fragment() {
    private var _binding: FragmentSelectCropSoilTestBinding? = null
    private val binding get() = _binding!!
    private val adapter: SelectCropSoilAdapter by lazy { SelectCropSoilAdapter() }
    private var selectedCategory: CropCategoryMasterDomain? = null

    private var str: String? = null

    private val viewModel by lazy { ViewModelProvider(this)[SoilTestRequestViewModel::class.java] }

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = ""
    private lateinit var myCropAdapter: MyCropSoilTest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectCropSoilTestBinding.inflate(inflater, container, false)
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
            val args = Bundle()
            it?.cropId?.let { it1 -> args.putInt("crop_id", it1) }
            args.putString("name", it?.cropName)
            args.putString("crop_logo", it?.cropLogo)

            findNavController().navigate(
                R.id.action_selectCropSoilTestFragment_to_checkSoilTestFragment,
                args
            )
        }

        myCropAdapter = MyCropSoilTest(MyCropSoilTest.DiffCallback.OnClickListener {
            val id = it.cropId
            var id2 = 0
            val args = Bundle()
            it.idd?.let { it1 -> args.putInt("crop_id", it1) }
            it.cropName?.let { it1 -> args.putString("name", it1) }
            it.cropLogo?.let { it2 -> args.putString("crop_logo", it2) }
//            findNavController().navigateUp()
            findNavController().navigate(
                R.id.action_selectCropSoilTestFragment_to_checkSoilTestFragment,
                args
            )
//           val action=SelectCropSoilTestFragmentDirections.actionSelectCropSoilTestFragmentToCheckSoilTestFragment(it.idd.toString().toInt(),it.cropName.toString(),it.cropLogo.toString())
//            findNavController().navigate(action)
//            findNavController().navigate(
//                        R.id.action_selectCropSoilTestFragment_to_checkSoilTestFragment,
//                        args)
            viewModel.getCropMaster().observe(viewLifecycleOwner) {
                for (i in 0 until it.data?.size!!) {
                    Log.d("CropId", "onViewCreated: ${id} ${it.data?.get(i)?.cropId}")
                    if (it.data?.get(i)?.cropId == id) {
                        id2 = it.data?.get(i)?.cropId!!
                    }
                }
//                if(id==id2){
//                    findNavController().navigate(
//                        R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
//                        args)
//                }else  dialog()

            }

        })
        binding.rvMyCrops.adapter = myCropAdapter
        myCrops()

    }

    fun myCrops() {


        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            myCropAdapter.submitList(it.data)
            if ((it.data != null)) {
                binding.tvCount.text = it.data!!.size.toString()
            } else {
                binding.tvCount.text = "0"
            }
            // Log.d("MYCROPS", it.data?.get(0)?.cropLogo.toString())

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
                    ToastStateHandling.toastError(requireContext(), "Error", Toast.LENGTH_SHORT)

                }
                is Resource.Loading -> {
                    ToastStateHandling.toastWarning(requireContext(), "Loading", Toast.LENGTH_SHORT)

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
                    ToastStateHandling.toastWarning(requireContext(), "Error Occurred", Toast.LENGTH_SHORT)
                }
            }
        }
    }


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

//    private fun dialog(){
//
//        val dialog = Dialog(requireContext())
//        //dialog.setCancelable(false)
//        dialog.setContentView(com.waycool.featurecrophealth.R.layout.dailog_information)
//        // val body = dialog.findViewById(R.id.body) as TextView
//        val yesBtn = dialog.findViewById(com.waycool.featurecrophealth.R.id.ok) as Button
//        yesBtn.setOnClickListener {
//            dialog.dismiss()
//            Log.d("Dialog", "dialog: Clicked")
//        }
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.show()
//    }

}