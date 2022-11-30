package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.HistoryDetailAdapter
import com.example.irrigationplanner.adapter.WeeklyAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.example.irrigationplanner.databinding.FragmentIrrigationHistoryBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import kotlinx.coroutines.launch


class IrrigationHistoryFragment : Fragment() {
    private lateinit var binding:FragmentIrrigationHistoryBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private lateinit var mHistoryAdapter: HistoryDetailAdapter
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
        binding = FragmentIrrigationHistoryBinding.inflate(inflater)

        mHistoryAdapter = HistoryDetailAdapter(HistoryDetailAdapter.OnClickListener {

        })
        binding.recycleViewHis.adapter = mHistoryAdapter
        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(1,1).observe(viewLifecycleOwner) {
                mHistoryAdapter.submitList(it.data?.data?.irrigation?.historicData)
            }
        }

        onClick()
        return binding.root
    }
    private fun onClick(){
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

}