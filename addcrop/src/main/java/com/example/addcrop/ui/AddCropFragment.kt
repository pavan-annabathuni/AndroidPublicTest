package com.example.addcrop.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentAddCropBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.SoilTypeDomain
import com.waycool.data.utils.Resource


class AddCropFragment : Fragment(), AddCropItemClick {
    private var _binding: FragmentAddCropBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }
    private var categoryAdapter = CategoryAdapter(this)
    private var crop_id_selected:Int?=null
    private var pomo:String?=""
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
        viewModel.getAddCropType().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    Log.d("TAG", "bindObserversCategoryData: ${it.data}")
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    val response = it.data!!
                    Log.d("TAG", "bindObserversCategory:$response ")
                    categoryAdapter.setMovieList(response)

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
    @SuppressLint("NotifyDataSetChanged")
    override fun clickOnCategory(name: SoilTypeDomain) {
        if (arguments != null) {
            crop_id_selected = arguments?.getInt("cropid")
            pomo= arguments?.getString("pom")
            binding.cardCheckHealth.isEnabled = true
            binding.cardCheckHealth.setOnClickListener {
                categoryAdapter.upDateList()
                if (name.isSelected == false) {
                    ToastStateHandling.toastError(requireContext(), "Please Select Soil Type", Toast.LENGTH_SHORT)

                } else {
                    Log.d("TAG", "clickOnCategorySelected: $crop_id_selected ")
                    val bundle = Bundle()
                    bundle.putInt("soil_type_id", name.id!!)
                    bundle.putInt("cropid", crop_id_selected!!)
                    bundle.putString("pom",pomo)
                    Log.d("TAG", "SoilTypeID: ${name.id} ")
                    findNavController().navigate(
                        R.id.action_addCropFragment_to_addCropPremiumFragment, bundle)
                }


            }


        }
    }
}