package com.example.irrigationplanner

import android.os.Bundle
import android.util.Log
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
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Network.NetworkModels.Irrigation
import com.waycool.data.translations.TranslationsManager
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

           // irrigation = it.getParcelable("IrrigationHis")!!
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
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
        mHistoryAdapter = DiseaseHistoryAdapter()
        binding.recycleViewHis.adapter = mHistoryAdapter
        viewModel.viewModelScope.launch {
            viewModel.getDisease(accountId!!,plotId).observe(viewLifecycleOwner) {
//                        val i = it.data?.data?.disease?.size?.minus(1)
//                        while (i!=0) {
                val data = it.data?.data?.historicData?.filter { itt ->
                    itt.disease?.diseaseType == "Disease"
                }
                mHistoryAdapter.submitList(data)
                Log.d("hostry", "setAdapter: ${it.message}")

                val data2 = it.data?.data?.historicData?.filter { itt ->
                    itt.disease?.diseaseType == "Deficiency"
                }
                if(data2!=null) dificiency = "dif"
                else dificiency = "noData"
//                        }
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

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Disease").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Pest").setCustomView(R.layout.item_tab)
        )
        if(dificiency == "diff"){
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Deficiency").setCustomView(R.layout.item_tab)
        )}
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(binding.tabLayout.selectedTabPosition) {
                    0->viewModel.viewModelScope.launch {
                        viewModel.getDisease(accountId!!, plotId).observe(viewLifecycleOwner) {
//                        val i = it.data?.data?.disease?.size?.minus(1)
//                        while (i!=0) {
                            val data = it.data?.data?.historicData?.filter { itt ->
                                itt.disease?.diseaseType == "Disease"
                            }
                            mHistoryAdapter.submitList(data)
                            Log.d("hostry", "setAdapter: ${it.message}")
//                        }
                        }
                    }
                    1->{viewModel.viewModelScope.launch {
                        viewModel.getDisease(accountId!!,plotId).observe(viewLifecycleOwner) {
//                        val i = it.data?.data?.disease?.size?.minus(1)
//                        while (i!=0) {
                            val data = it.data?.data?.historicData?.filter { itt ->
                                itt.disease?.diseaseType == "Pest"
                            }
                            mHistoryAdapter.submitList(data)
                            Log.d("hostry", "setAdapter: ${it.message}")
//                        }
                        }
                    }}
                    2->{viewModel.viewModelScope.launch {
                        accountId?.let {
                            viewModel.getDisease(accountId!!,plotId).observe(viewLifecycleOwner) {
                    //                        val i = it.data?.data?.disease?.size?.minus(1)
                    //                        while (i!=0) {
                                val data = it.data?.data?.historicData?.filter { itt ->
                                    itt.disease?.diseaseType == "Deficiency"
                                }
                                mHistoryAdapter.submitList(data)
                                Log.d("hostry", "setAdapter: ${it.message}")
                    //                        }
                            }
                        }
                    }}

                }}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

}