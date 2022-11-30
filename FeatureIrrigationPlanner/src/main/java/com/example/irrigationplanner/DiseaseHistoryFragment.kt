package com.example.irrigationplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.DiseaseHistoryAdapter
import com.example.irrigationplanner.databinding.FragmentDisaseHistoryBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import kotlinx.coroutines.launch

class DiseaseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentDisaseHistoryBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private lateinit var mHistoryAdapter: DiseaseHistoryAdapter

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
        binding = FragmentDisaseHistoryBinding.inflate(inflater)
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
        mHistoryAdapter = DiseaseHistoryAdapter()
        binding.recycleViewHis.adapter = mHistoryAdapter
        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(1,1).observe(viewLifecycleOwner) {
                mHistoryAdapter.submitList(it.data?.data?.disease)
            }
        }

        return binding.root
    }

}