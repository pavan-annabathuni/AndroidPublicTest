package com.example.addcrop.ui.selectcrop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentAddCropBinding
import com.example.addcrop.ui.CategoryAdapter
import com.example.addcrop.viewmodel.AddCropViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.SoilTypeDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
        translationForPremiumAddCrop()


    }

    private fun initView() {
        binding.recyclerviewSand.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewSand.adapter = categoryAdapter
        binding.toolbar .setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun bindObserversCategory() {
        viewModel.getAddCropType().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    val response = it.data!!
                    categoryAdapter.setSoilTypeList (response)
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

   private fun translationForPremiumAddCrop() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("add_crop")
            binding.toolbarTitle.text = title
        }
        TranslationsManager().loadString("select_soil_type", binding.textSelectType)
        TranslationsManager().loadString("next", binding.tvCheckCrop)
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
                    val bundle = Bundle()
                    bundle.putInt("soil_type_id", name.id!!)
                    bundle.putInt("cropid", crop_id_selected!!)
                    bundle.putString("pom",pomo)
                    findNavController().navigate(
                        R.id.action_addCropFragment_to_addCropPremiumFragment, bundle)
                }


            }


        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AddCropFragment")
    }
}