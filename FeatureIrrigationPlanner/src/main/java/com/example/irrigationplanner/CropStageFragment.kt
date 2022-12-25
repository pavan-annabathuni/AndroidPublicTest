package com.example.irrigationplanner

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.CropStageAdapter
import com.example.irrigationplanner.databinding.FragmentCropStageBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.waycool.data.Network.NetworkModels.UpdateCropStage
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


        mCropStageAdapter = CropStageAdapter(CropStageAdapter.OnClickListener {
             cropStageId = it.id
            Log.d("Date", "getCropStage: $cropStageId")

        })
        getCropStage()
        return binding.root
    }


    fun onClick() {
        binding.topAppBar.setOnClickListener(){
            findNavController().navigateUp()
        }


    }

    fun getCropStage() {

        mCropStageAdapter.onDateSelected={
            val inputDateFormatter: SimpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date2: Date = inputDateFormatter.parse(it)
            date = outputDateFormatter.format(date2)

            Log.d("Date", "getCropStage: $date")

        }
            viewModel.getCropStage(accountId, plotId).observe(viewLifecycleOwner) {

                Log.d("Date", "getCropStage: ${it.data?.data?.get(0)?.id}")
                binding.recycleViewHis.adapter = mCropStageAdapter
                mCropStageAdapter.submitList(it.data?.data)
            }
        binding.saveCropStage.setOnClickListener(){
            viewModel.updateCropStage(accountId, cropStageId!!,plotId,date).observe(viewLifecycleOwner){
                Log.d("Date", "getCropStage: $date")
            }
            viewModel.getCropStage(accountId  , plotId).observe(viewLifecycleOwner) {
                binding.recycleViewHis.adapter = mCropStageAdapter
                mCropStageAdapter.submitList(it.data?.data)
            }

        }
        }
}

