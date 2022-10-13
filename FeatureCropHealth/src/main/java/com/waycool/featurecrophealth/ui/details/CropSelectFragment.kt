package com.waycool.featurecrophealth.ui.details

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.waycool.featurecrophealth.DetailsCLickLIstener
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropSelectBinding
import com.waycool.featurecrophealth.model.cropdetails.CropDetails
import com.waycool.featurecrophealth.model.cropdetails.Data
import com.waycool.featurecrophealth.utils.Constant
import com.waycool.featurecrophealth.utils.Constant.TAG
import com.waycool.featurecrophealth.utils.NetworkResult
import com.waycool.featurecrophealth.viewmodel.CropCategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class CropSelectFragment : Fragment(), DetailsCLickLIstener, CategoryListener {
    private var _binding: FragmentCropSelectBinding? = null
    private val binding get() = _binding!!
    private var detailsAdapter = DetailsAdapter(this)
    private var categoryAdapter = CategoryAdapter(this)
    private var responseDataList = mutableListOf<Data>()
    private var str: String? = null
    lateinit var cropDetailsList: CropDetails
    val filteredList = ArrayList<Data>()

    private val viewModel by lazy { ViewModelProvider(this)[CropDetailsViewModel::class.java] }
    private val viewModelCategory by lazy { ViewModelProvider(this)[CropCategoryViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCropSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCropDetails(requireContext())
        CoroutineScope(Dispatchers.IO).launch { // coroutine on Main
            val cropList = viewModel.getLocalCropList()
            Log.d("TAG", "onViewCreatedDataList: $cropList")
        }
        viewModelCategory.getAllCategory(requireContext())
        binding.recyclerviewDetails.adapter = detailsAdapter
        binding.recyclerviewCategory.adapter = categoryAdapter
        bindObserversCategory()
        bindObserversDetails()
        initView()

        clickSearch()
    }


    fun initView() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

    }

    private fun bindObserversCategory() {
        viewModelCategory.categoryLiveData.observe(viewLifecycleOwner, Observer {

            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constant.TAG, "bindObserversDataCategory:" + it.data?.data.toString())
                    val response =
                        it.data?.data as ArrayList<com.waycool.featurecrophealth.model.cropcate.Data>
                    categoryAdapter.setMovieList(response)


                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {

                }
            }
        })


    }

    private fun bindObserversDetails() {
        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer {

            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constant.TAG, "bindObserversDataDetails:" + it.data?.data.toString())
                    val response =
                        it.data?.data as List<com.waycool.featurecrophealth.model.cropdetails.Data>
//                    detailsAdapter.setMovieList(response)
                    responseDataList = response as MutableList<Data>

                    cropDetailsList = CropDetails(responseDataList, "", true)

                    binding.apply {
                        detailsAdapter = DetailsAdapter(this@CropSelectFragment)
                        recyclerviewDetails.adapter = detailsAdapter
                        detailsAdapter.setMovieList(responseDataList)
                        categoryAdapter.notifyDataSetChanged()
                    }

                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {


                }
            }
        })


    }

    override fun clickOnDetails(data: Data) {
        Log.wtf("get_arg:", "Item is clicked" + data.crop_category_id)
        val bundle = Bundle()
        bundle.putInt("crop_id", data.crop_id)
        bundle.putString("name", data.crop_name)
        bundle.putString("crop_logo", data.crop_logo)


        Log.wtf("get_arg:", "SendingData" + data.crop_name.toString())
        findNavController().navigate(
            R.id.action_cropSelectFragment_to_cropDetailsCaptureFragment,
            bundle
        )
    }

    override fun clickOnCategory(categoryDate: com.waycool.featurecrophealth.model.cropcate.Data) {
        var detailsListArea = ArrayList<Data>()
        responseDataList.forEach {
            if (it.crop_category_id.equals(categoryDate.id)) {
                detailsListArea.add(it)
            }
        }
        binding.all.setTextColor(Color.parseColor("#111827"))
        binding.all.setBackgroundResource(R.drawable.bd_flex)
        detailsAdapter.setMovieList(detailsListArea)
        detailsAdapter.notifyDataSetChanged()
//        binding.skillName.setBackgroundResource(R.drawable.bg_details)

        Log.d(TAG, "clickOnCategory: " + categoryDate.category_name)
        Log.d(TAG, "clickOnCategoryName: $str")
    }

    private fun clickSearch() {

        binding.searchView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int

            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                filteredList.clear()
//                Log.d("TAG", "::::str $charSequence")
                for (item in cropDetailsList.data.indices) {
                    Log.d("TAG", "::::stderr $charSequence")
                    if (cropDetailsList.data[item].crop_name.lowercase().startsWith(charSequence.toString().lowercase())) {
                        filteredList.add(cropDetailsList.data[item])
                        Log.d(TAG, "onTextChangedList:$filteredList")
                        Log.d("TAG", "::::::::stderr $charSequence")
                    }

                }
                detailsAdapter.upDateList(filteredList)
//                binding.etSearchItem.getText().clear();
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


}