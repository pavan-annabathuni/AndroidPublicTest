package com.example.irrigationplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.CropStageAdapter
import com.example.irrigationplanner.databinding.FragmentCropStageBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CropStageFragment : Fragment() {
    private lateinit var binding: FragmentCropStageBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private lateinit var mCropStageAdapter: CropStageAdapter
    private lateinit var date: String
    private var cropStageId:Int? = null
    private var plotId:Int = 0
    private var accountId:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plotId = it.getInt("plotId")
            accountId = it.getInt("accountId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCropStageBinding.inflate(inflater)
        onClick()
        cropStageStartDate()

        mCropStageAdapter = CropStageAdapter(CropStageAdapter.OnClickListener {
             cropStageId = it.id
            Log.d("Date", "getCropStage: $cropStageId")

        })

        getCropStage()
        return binding.root
    }


    fun onClick() {
        binding.topAppBar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.viewModelScope.launch {
            binding.topAppBar.title = TranslationsManager().getString("str_crop_stage")
            binding.saveCropStage.text = TranslationsManager().getString("str_update")
        }
    }

    private fun getCropStage() {
        /** Get date value form adapter and formatting here */
        mCropStageAdapter.onDateSelected={
            val inputDateFormatter: SimpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date2: Date = inputDateFormatter.parse(it)
            date = outputDateFormatter.format(date2)
            Log.d("Date", "getCropStage: $date")

        }
        /** Calling get crop stage api to show the list*/
//            viewModel.getCropStage(accountId, plotId).observe(viewLifecycleOwner) {
//                Log.d("Date", "getCropStage: ${it.data?.data?.get(0)?.id}")
//                binding.recycleViewHis.adapter = mCropStageAdapter
//                mCropStageAdapter.submitList(it.data?.data)
//            }
        /** saving the date for crop stage */
        binding.saveCropStage.setOnClickListener {
            if(cropStageId!=null) {
                viewModel.updateCropStage(accountId, cropStageId!!, plotId, date)
                    .observe(viewLifecycleOwner) {
                        Log.d("Date", "getCropStage: $date")
                        viewModel.getCropStage(accountId, plotId).observe(viewLifecycleOwner) {
                            binding.recycleViewHis.adapter = mCropStageAdapter
                            mCropStageAdapter.submitList(it.data?.data)
                        }
                    }
            }else{
                context?.let { it1 -> ToastStateHandling.toastError(it1,"Please Select The Date",Toast.LENGTH_SHORT) }
            }
        }
        }
    private fun cropStageStartDate(){
        viewModel.getMyCrop2().observe(viewLifecycleOwner){
            val data = it.data?.first {
                it.id==plotId
            }

            data?.sowingDate?.let { it1 ->
                viewModel.updateCropStage(accountId, 1, plotId, it1).observe(viewLifecycleOwner) {
                        when(it){
                            is Resource.Success->{
                                viewModel.getCropStage(accountId, plotId).observe(viewLifecycleOwner) {
                                    binding.recycleViewHis.adapter = mCropStageAdapter
                                    mCropStageAdapter.submitList(it.data?.data)
                                }
                            }
                            is Resource.Loading->{

                            }
                            is Resource.Error->{

                            }
                        }


                    }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropStageFragment")
    }
}

