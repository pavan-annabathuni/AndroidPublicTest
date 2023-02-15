package com.example.addcrop.ui.premium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentVeriatyCropBinding
import com.example.addcrop.ui.selectcrop.MockList
import com.example.addcrop.ui.selectcrop.VariatyModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager


class VarietyCropFragment : Fragment(), ItemSelectedListener, ItemGraphsClicked {
    private var cropCategoryTagName: String?=null
    private var crop_name: String?=null
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
        TranslationsManager().loadString("select_variety", binding.textSelectType, "Select Variety")
        TranslationsManager().loadString("add_crop", binding.toolbarTitle, "Add Crop")
        TranslationsManager().loadString("next", binding.tvCheckCrop, "Next")

        if (arguments != null) {
            crop_id = arguments?.getInt("cropid")
            crop_name = arguments?.getString("cropname")
            cropCategoryTagName = arguments?.getString("selectedCategory")

            crop_type = arguments?.getInt("soil_type_id")
            if (crop_id == 67) {
                initView()
            } else if (crop_id == 97) {
                initViewGraphs()
            }

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
        val adapter = GraphsAdapter(MockListGraphs.getModel() as ArrayList<GraphsModel>, this)
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
                if (name.isSelected == false) {
                    ToastStateHandling.toastError(
                        requireContext(),
                        "Please Select Soil Type",
                        Toast.LENGTH_SHORT
                    )

                } else {
                    val bundle = Bundle()
                    bundle.putInt("cropid", crop_id_selected!!)
                    bundle.putString("pom", name.name)
                    bundle.putString("cropName",crop_name)
                    bundle.putString("selectedCategory",cropCategoryTagName)
                    findNavController().navigate(
                        R.id.action_veriatyCropFragment_to_addCropFragment, bundle
                    )
                }


            }


        }
    }

    override fun clickGraphs(name: GraphsModel) {
        if (arguments != null) {
            val crop_id_selected = arguments?.getInt("cropid")
            binding.cardCheckHealth.isEnabled = true
            binding.cardCheckHealth.setOnClickListener {
                if (name.isSelected == false) {
                    ToastStateHandling.toastError(
                        requireContext(),
                        "Please Select Soil Type",
                        Toast.LENGTH_SHORT
                    )
                } else {
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
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("VarietyCropFragment")
    }

}