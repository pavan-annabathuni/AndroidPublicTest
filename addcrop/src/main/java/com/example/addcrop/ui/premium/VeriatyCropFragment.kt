package com.example.addcrop.ui.premium

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentPlantSpacingBinding
import com.example.addcrop.databinding.FragmentVeriatyCropBinding
import com.example.addcrop.ui.CategoryAdapter


class VeriatyCropFragment : Fragment(),ItemSelectedListener,ItemGraphsClicked {
    private var _binding: FragmentVeriatyCropBinding? = null
    private val binding get() = _binding!!
    var crop_id: Int? = null
    var crop_type: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVeriatyCropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            crop_id = arguments?.getInt("cropid")
            crop_type = arguments?.getInt("soil_type_id")
            if (crop_id == 2) {
                initView()
            } else if (crop_id == 67) {
                initViewGraphs()
            }
            Log.d(ContentValues.TAG, "onCreateViewONPIDPrinteddvsvff: $crop_id")
            Log.d(ContentValues.TAG, "onCreateViewONPIDPrinteddvsvff: $crop_type")
        }
    }

    private fun initView() {
        binding.recyclerviewSand.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val adapter = VarietyAdapter(MockList.getModel() as ArrayList<VariatyModel>, this)
        binding.recyclerviewSand.adapter = adapter
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }
        private fun initViewGraphs() {
        binding.recyclerviewSand.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val adapter = GraphsAdapter(MockListGraphs.getModel() as ArrayList<GraphsModel>,this)
        binding.recyclerviewSand.adapter = adapter

        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    override fun clickOnCategory(name: VariatyModel) {
        if (arguments != null) {
            var crop_id_selected = arguments?.getInt("cropid")
            binding.cardCheckHealth.isEnabled = true
            binding.cardCheckHealth.setOnClickListener {
//                categoryAdapter.upDateList()
                if (name.isSelected == false) {
                    Toast.makeText(requireContext(), "Please Select Soil Type", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d("TAG", "clickOnCategorySelected: $crop_id_selected ")
                    val bundle = Bundle()
//                    bundle.putInt("soil_type_id", name.id!!)
                    bundle.putInt("cropid", crop_id_selected!!)
                    bundle.putString("pom",name.name)
                    findNavController().navigate(
                        R.id.action_veriatyCropFragment_to_addCropFragment, bundle
                    )
                }


            }


        }
    }

    override fun clickGraphs(name: GraphsModel) {
        if (arguments != null) {
            var crop_id_selected = arguments?.getInt("cropid")
            binding.cardCheckHealth.isEnabled = true
            binding.cardCheckHealth.setOnClickListener {
//                categoryAdapter.upDateList()
                if (name.isSelected == false) {
                    Toast.makeText(requireContext(), "Please Select Soil Type", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d("TAG", "clickOnCategorySelected: $crop_id_selected ")
                    val bundle = Bundle()
                    bundle.putInt("soil_type_id", name.id!!)
                    bundle.putInt("cropid", crop_id_selected!!)
                    findNavController().navigate(
                        R.id.action_veriatyCropFragment_to_addCropFragment,
                        bundle
                    )
                }


            }


        }

    }

}