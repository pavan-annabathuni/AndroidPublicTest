package com.example.addcrop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.databinding.FragmentEditCropBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling

class EditCropFragment : Fragment() {
    private lateinit var binding: FragmentEditCropBinding
    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }
    private lateinit var myCropAdapter:EditMyCropsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditCropBinding.inflate(inflater)
        myCropAdapter = EditMyCropsAdapter(EditMyCropsAdapter.DiffCallback.OnClickListener{
             viewModel.getEditMyCrop(it.id!!).observe(viewLifecycleOwner) {
                 context?.let { it1 -> ToastStateHandling.toastSuccess(it1,"Crop Deleted",Toast.LENGTH_SHORT) }
                 //myCrops()
             }

        })
        binding.rvMyCrops.adapter = myCropAdapter
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        myCrops()
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
}