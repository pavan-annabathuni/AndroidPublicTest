package com.example.addcrop.ui.editcrop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.addcrop.databinding.FragmentEditCropBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.launch

class EditCropFragment : Fragment() {
    private lateinit var binding: FragmentEditCropBinding
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }
    private lateinit var myCropAdapter: EditMyCropsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditCropBinding.inflate(inflater)
        //set translations
        translation()
        //click of item
        myCropAdapter = EditMyCropsAdapter(EditMyCropsAdapter.DiffCallback.OnClickListener{
            //event listener when a crop is deleted
            val eventBundle=Bundle()
            eventBundle.putString("",it.cropName)
            //translation of crop deleted
            viewModel.viewModelScope.launch {
                val toast = TranslationsManager().getString("crop_deleted")
             viewModel.getEditMyCrop(it.id!!).observe(viewLifecycleOwner) {
                 if(!toast.isNullOrEmpty())
                     context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toast, LENGTH_SHORT)
                     }
                 else context?.let { it1 ->ToastStateHandling.toastSuccess(it1,"Crop deleted",
                     LENGTH_SHORT
                 )}
             }
             }

        })
        //set adapter
        binding.rvMyCrops.adapter = myCropAdapter
        //navigation back
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        //get list of crops
        myCrops()
        //callback for back press
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    this@EditCropFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        return binding.root
    }
    private fun myCrops() {
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            myCropAdapter.submitList(it.data)
            if ((it.data != null)) {
                binding.tvCount.text = it.data!!.size.toString()
                if(!it.data!![0].cropNameTag.isNullOrEmpty()){
                EventClickHandling.calculateClickEvent("Edit_crop_${it.data!![0].cropNameTag}")
                }
            }
                if(it.data.isNullOrEmpty()){
                    this@EditCropFragment.findNavController().navigateUp()
                }
        }
    }

    override fun onPause() {
        super.onPause()
        myCrops()
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("EditCropFragment")
    }

    private fun translation(){
        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("edit_crop")
            if(title.isNullOrEmpty()){
                binding.toolbar.title = "Edit Crop"
            }else binding.toolbar.title  = title

            TranslationsManager().loadString("my_crops",binding.title3SemiBold,"My Crops")

        }
    }
}