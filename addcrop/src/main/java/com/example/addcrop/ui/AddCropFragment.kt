package com.example.addcrop.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentAddCropBinding
import com.example.addcrop.model.Data
import com.example.addcrop.utils.NetworkResult
import com.example.addcrop.viewmodel.AddViewModel
import com.waycool.data.Repository.DomainModels.AddCropTypeDomain
import com.waycool.data.Repository.DomainModels.CropCategoryMasterDomain
import com.waycool.data.utils.Resource


class AddCropFragment : Fragment(), AddCropItemClick {
    private var _binding: FragmentAddCropBinding? = null
    private val binding get() = _binding!!

    //    private var categoryAdapter = CategoryAdapter(this)
    private var responseDataList = ArrayList<Data>()
//    private val viewModel: CropProtectViewModel by lazy {
//        ViewModelProvider(requireActivity())[CropProtectViewModel::class.java]
//    }
    private val viewModel by lazy { ViewModelProvider(this)[AddViewModel::class.java] }
    private var categoryAdapter = CategoryAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCropBinding.inflate(inflater, container, false)
        viewModel.getAddCropType()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindObserversCategory()
        binding.toolbar .setOnClickListener {
            findNavController().navigate(R.id.action_addCropFragment_to_addCropDetailsFragment)
        }
//        categoryAdapter.upDateList(responseDataList)

    }

    private fun initView() {
        binding.recyclerviewSand.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewSand.adapter = categoryAdapter
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun bindObserversCategory() {
        viewModel.getAddCropType().observe(requireActivity()){
            when (it) {
                is Resource.Success -> {
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    val response = it.data as java.util.ArrayList<AddCropTypeDomain>
                    Log.d("TAG", "bindObserversCategory:$response ")
                    categoryAdapter.setMovieList(response)

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()

                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }

        }



//        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer {
//            when (it) {
//                is NetworkResult.Success -> {
//                    Log.d("TAG", "bindObserversDataCategory:" + it.data?.data.toString())
//                    val response = it.data?.data as java.util.ArrayList<Data>
//                    categoryAdapter.setMovieList(response)
//                }
//                is NetworkResult.Error -> {
//                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                        .show()
//                }
//                is NetworkResult.Loading -> {
//
//                }
//            }
//        })


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clickOnCategory(name: AddCropTypeDomain) {
        binding.cardCheckHealth.isEnabled = true
        binding.cardCheckHealth.setOnClickListener {
            categoryAdapter.upDateList()
            if (!name.isSelected!!) {
                Toast.makeText(requireContext(), "PleaseSelect Soil Type", Toast.LENGTH_SHORT)
                    .show()
            } else {
                findNavController().navigate(R.id.action_addCropFragment_to_addCropDetailsFragment)

//            }
            }

//        Toast.makeText(requireContext(), "item is clicked", Toast.LENGTH_SHORT).show()

        }


    }

//    override fun clickOnCategory(name: AddCropTypeDomain) {
//        TODO("Not yet implemented")
//    }
}