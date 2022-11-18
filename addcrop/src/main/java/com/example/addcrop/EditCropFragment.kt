package com.example.addcrop

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.addcrop.databinding.FragmentEditCropBinding
import com.example.addcrop.viewmodel.AddViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCropFragment : Fragment() {
    private lateinit var binding: FragmentEditCropBinding
    private val viewModel by lazy { ViewModelProvider(this)[AddViewModel::class.java] }
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
                 Log.d("Plots", "onCreateView: $it")
                 Toast.makeText(context,"Crop Deleted",Toast.LENGTH_SHORT).show()
                 myCrops()
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
    fun myCrops() {

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            var accountId = it.data?.account!![0].id!!

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
}