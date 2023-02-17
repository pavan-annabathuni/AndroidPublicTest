package com.example.irrigationplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.HistoryDetailAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationHistoryBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.waycool.data.Network.NetworkModels.Irrigation
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.launch


class IrrigationHistoryFragment : Fragment() {
    private lateinit var binding:FragmentIrrigationHistoryBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private var historyDetails:Irrigation? = null
    private lateinit var mHistoryAdapter: HistoryDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //TODO
            historyDetails = it.getParcelable("IrrigationHis")
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
        mHistoryAdapter.submitList(historyDetails?.historicData)


        onClick()
        //translation
        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("str_irrigation_history")
            binding.topAppBar.title = title
        }
        return binding.root
    }
    private fun onClick(){
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("IrrigationHistoryFragment")
    }

}