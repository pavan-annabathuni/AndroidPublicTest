package com.example.irrigationplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.DiseaseHistoryAdapter
import com.example.irrigationplanner.databinding.FragmentDisaseHistoryBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.tabs.TabLayout
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch

class DiseaseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentDisaseHistoryBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private var plotId:Int = 0
    var accountId:Int?= null
    private lateinit var mHistoryAdapter: DiseaseHistoryAdapter
    var dificiency:String = "noData"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountId = it.getInt("accountId")
            plotId = it.getInt("plotId")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDisaseHistoryBinding.inflate(inflater)
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
        mHistoryAdapter = DiseaseHistoryAdapter()
        binding.recycleViewHis.adapter = mHistoryAdapter
        viewModel.viewModelScope.launch {
            viewModel.getDisease(accountId!!,plotId).observe(viewLifecycleOwner) {
                when(it){
                    is Resource.Success->{
                        val data = it.data?.data?.historicData?.filter { itt ->
                            itt.disease?.diseaseType == "Disease"
                        }
                        if (data.isNullOrEmpty()) {
                            binding.noPest.visibility = View.VISIBLE
                        } else {
                            binding.noPest.visibility = View.GONE
                        }
                        mHistoryAdapter.submitList(data)
                    }
                    is Resource.Loading->{
                        binding.noPest.visibility = View.GONE
                    }
                    is Resource.Error->{}
                }
                val data = it.data?.data?.historicData?.filter { itt ->
                    itt.disease?.diseaseType == "Disease"
                }
                mHistoryAdapter.submitList(data)
                /** checking that in api Deficiency is there or not */
                val data2 = it.data?.data?.historicData?.filter { itt ->
                    itt.disease?.diseaseType == "Deficiency"
                }
                if(data2!=null) dificiency = "dif"
                else dificiency = "noData"
            }
        }
        //translation
        viewModel.viewModelScope.launch{
            val title = TranslationsManager().getString("str_disease_his")
            binding.topAppBar.title = title
        }
        tabs()
        return binding.root
    }

    private fun tabs() {
        viewModel.viewModelScope.launch {
            val disease: String = TranslationsManager().getString("str_disease")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(disease).setCustomView(R.layout.item_tab)
            )
            var pest: String = TranslationsManager().getString("str_pest")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(pest).setCustomView(R.layout.item_tab)
            )
            /** if Deficiency is there we showing Deficiency tab*/
            if (dificiency == "diff") {
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(dificiency)
                        .setCustomView(R.layout.item_tab)
                )
            }
            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (binding.tabLayout.selectedTabPosition) {
                        0 -> viewModel.viewModelScope.launch {
                            viewModel.getDisease(accountId!!, plotId).observe(viewLifecycleOwner) {
                                when(it){
                                    is Resource.Success->{
                                        val data = it.data?.data?.historicData?.filter { itt ->
                                            itt.disease?.diseaseType == "Disease"
                                        }
                                        if (data.isNullOrEmpty()) {
                                            binding.noPest.visibility = View.VISIBLE
                                        } else {
                                            binding.noPest.visibility = View.GONE
                                        }
                                        mHistoryAdapter.submitList(data)
                                    }
                                    is Resource.Loading->{
                                        binding.noPest.visibility = View.GONE
                                    }
                                    is Resource.Error->{}
                                }

                            }
                        }
                        1 -> {
                            viewModel.viewModelScope.launch {
                                viewModel.getDisease(accountId!!, plotId)
                                    .observe(viewLifecycleOwner) {
                                        when(it){
                                            is Resource.Success->{
                                                val data = it.data?.data?.historicData?.filter { itt ->
                                                    itt.disease?.diseaseType == "Pest"
                                                }

                                                if (data.isNullOrEmpty()) {
                                                    binding.noPest.visibility = View.VISIBLE
                                                } else {
                                                    binding.noPest.visibility = View.GONE
                                                }
                                                mHistoryAdapter.submitList(data)
                                            }
                                            is Resource.Loading->{
                                                binding.noPest.visibility = View.GONE
                                            }
                                            is Resource.Error->{}
                                        }

                                        Log.d("hostry", "setAdapter: ${it.message}")
                                    }
                            }
                        }
                        2 -> {
                            viewModel.viewModelScope.launch {
                                accountId?.let {
                                    viewModel.getDisease(accountId!!, plotId)
                                        .observe(viewLifecycleOwner) {
                                            val data = it.data?.data?.historicData?.filter { itt ->
                                                itt.disease?.diseaseType == "Deficiency"
                                            }
                                            mHistoryAdapter.submitList(data)
                                        }
                                }
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("DiseaseHistoryFragment")
    }
}