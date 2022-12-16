package com.example.irrigationplanner

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.DiseaseAdapter
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.example.irrigationplanner.adapter.WeeklyAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class IrrigationFragment : Fragment() {
     private lateinit var binding: FragmentIrrigationBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private lateinit var mHistoryAdapter: HistoryAdapter
    private lateinit var mDiseaseAdapter: DiseaseAdapter
    private lateinit var mWeeklyAdapter: WeeklyAdapter
    private var accountId:Int = 0
     var dificiency:String = "noData"
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
        binding = FragmentIrrigationBinding.inflate(inflater)
        viewModel.getUserDetails().observe(viewLifecycleOwner){
            accountId = it.data?.accountId!!
            setAdapter()
        }
        binding.tvCropInfo.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.actionIrrigationFragmentToCropOverviewFragment())
        }
       // binding.recycleViewHis.adapter = mHistoryAdapter


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(accountId,1).observe(viewLifecycleOwner){
                if(it.data?.data?.irrigation?.currentData?.irrigation !=0)
                binding.irrigationReq.text = "Irrigation Not Required"
                else binding.irrigationReq.text = "Irrigation Required"
            }
        }
        tabs()
        exitDialog()
        binding.irrigationYes.setOnClickListener(){
            dialog()
        }
        binding.tvEdit.setOnClickListener(){
            binding.perDay.visibility = View.VISIBLE
            dialog()
        }

        onClick()

    }

    private fun onClick() {
        binding.btHarvest.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.
            actionIrrigationFragmentToSheetHarvestFragment())
        }

        binding.irrigationNo.setOnClickListener(){
            binding.dailyIrrigation.visibility = View.GONE
            binding.perDay.visibility = View.VISIBLE
        }
//        binding.tvEdit.setOnClickListener(){
//            binding.perDay.visibility = View.VISIBLE
//            val dialog = BottomSheetDialog(this.requireContext(),R.style.BottomSheetDialog)
//            dialog.setContentView(R.layout.irrigation_pre_day)
//            val close = dialog.findViewById<ImageView>(R.id.close)
//            close!!.setOnClickListener(){
//                dialog.dismiss()
//            }
//            dialog.show()
//        }

        binding.btHistory.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.
            actionIrrigationFragmentToIrrigationHistoryFragment())
        }
        binding.btDisease.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.actionIrrigationFragmentToDiseaseHistoryFragment())
        }

        binding.btForecast.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.
            actionIrrigationFragmentToForecastFragment())
        }
        binding.cropStage.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.actionIrrigationFragmentToCropStageFragment())
        }
        binding.topAppBar.setOnClickListener(){
            activity?.finish()
        }
    }

    private fun setAdapter() {
        mWeeklyAdapter = WeeklyAdapter()
        binding.recycleViewDis.adapter = mWeeklyAdapter
        mHistoryAdapter = HistoryAdapter(HistoryAdapter.DiffCallback.OnClickListener {

        })
        binding.recycleViewHis.adapter = mHistoryAdapter
        mDiseaseAdapter = DiseaseAdapter()
        binding.rvDis.adapter = mDiseaseAdapter


        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(accountId,1).observe(viewLifecycleOwner){
                //weekly
                it.data?.data?.irrigation?.irrigationForecast?.let { it1 ->
                    mWeeklyAdapter.setList(
                        it1
                    ) }
                //history
                mHistoryAdapter.submitList(it.data?.data?.irrigation?.historicData)
                Log.d("hostry", "setAdapter: ${it.message}")
                binding.textViewL.text = it.data?.data?.irrigation?.historicData?.get(0)?.irrigation+" L"
                if(it.data?.data?.irrigation?.historicData?.get(0)?.irrigation!=null){
                    binding.dailyIrrigation.visibility = View.GONE
                    binding.perDay.visibility = View.VISIBLE
                }

                //disease
                val data = it.data?.data?.disease?.filter { itt ->
                    itt.disease.diseaseType == "Disease"
                }
                mDiseaseAdapter.submitList(data)
                Log.d("hostry", "setAdapter: ${it.message}")
                val data2 = it.data?.data?.disease?.filter { itt ->
                    itt.disease.diseaseType == "Deficiency"
                }
                if(data2!=null) dificiency = "dif"
                else dificiency = "noData"
            }
        }


//        binding.recycleViewHis.adapter = mHistoryAdapter
//        viewModel.viewModelScope.launch {
//            viewModel.getIrrigationHis(accountId,1).observe(viewLifecycleOwner) {
//                mHistoryAdapter.submitList(it.data?.data?.irrigation?.historicData)
//                Log.d("hostry", "setAdapter: ${it.message}")
//                binding.textViewL.text = it.data?.data?.irrigation?.historicData?.get(0)?.irrigation
//                if(it.data?.data?.irrigation?.historicData?.get(0)?.irrigation!=null){
//                    binding.dailyIrrigation.visibility = View.GONE
//                    binding.perDay.visibility = View.VISIBLE
//                }
//
//
//            }
//        }
//
//        viewModel.viewModelScope.launch {
//            viewModel.getIrrigationHis(accountId, 1).observe(viewLifecycleOwner) {
////                        val i = it.data?.data?.disease?.size?.minus(1)
////                        while (i!=0) {
//                val data = it.data?.data?.disease?.filter { itt ->
//                    itt.disease.diseaseType == "Disease"
//                }
//                mDiseaseAdapter.submitList(data)
//                Log.d("hostry", "setAdapter: ${it.message}")
//
//
//                val data2 = it.data?.data?.disease?.filter { itt ->
//                    itt.disease.diseaseType == "Deficiency"
//                }
//                if(data2!=null) dificiency = "dif"
//                else dificiency = "noData"
//            }
//        }
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
                        viewModel.getIrrigationHis(accountId, 1).observe(viewLifecycleOwner) {
//                        val i = it.data?.data?.disease?.size?.minus(1)
//                        while (i!=0) {
                            val data = it.data?.data?.disease?.filter { itt ->
                                itt.disease.diseaseType == "Disease"
                            }
                            mDiseaseAdapter.submitList(data)
                            Log.d("hostry", "setAdapter: ${it.message}")
//                        }
                        }
                    }
                    1->{viewModel.viewModelScope.launch {
                        viewModel.getIrrigationHis(accountId, 1).observe(viewLifecycleOwner) {
//                        val i = it.data?.data?.disease?.size?.minus(1)
//                        while (i!=0) {
                            val data = it.data?.data?.disease?.filter { itt ->
                                itt.disease.diseaseType == "Pest"
                            }
                            mDiseaseAdapter.submitList(data)
                            Log.d("hostry", "setAdapter: ${it.message}")
//                        }
                        }
                    }}
                    2->{viewModel.viewModelScope.launch {
                        viewModel.getIrrigationHis(accountId, 1).observe(viewLifecycleOwner) {
//                        val i = it.data?.data?.disease?.size?.minus(1)
//                        while (i!=0) {
                            val data = it.data?.data?.disease?.filter { itt ->
                                itt.disease.diseaseType == "Deficiency"
                            }
                            mDiseaseAdapter.submitList(data)
                            Log.d("hostry", "setAdapter: ${it.message}")
//                        }
                        }
                    }}

            }}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
    })
    }

    private fun dialog() {

        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialog)
        dialog.setContentView(R.layout.irrigation_pre_day)
        val close = dialog.findViewById<ImageView>(R.id.close)
        val save = dialog.findViewById<Button>(R.id.savePreDay)
        val irrigation = dialog.findViewById<EditText>(R.id.etPerDay)
        close!!.setOnClickListener() {
            dialog.dismiss()
        }
        save?.setOnClickListener() {

            val value = irrigation?.text.toString().toInt()
            viewModel.updateIrrigation(4, value).observe(viewLifecycleOwner) {
                binding.textViewL.text = value.toString()
                Log.d("ok", "dialog: ${it.message} ")
            }
            dialog.dismiss()
        }

        dialog.show()

    }
    fun exitDialog(){
        binding.btExit.setOnClickListener(){
            val dialog = Dialog(requireContext())

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_delete_irrigartion)
            // val body = dialog.findViewById(R.id.body) as TextView
            val cancel = dialog.findViewById(R.id.cancel) as Button
            val delete = dialog.findViewById(R.id.delete) as Button
            delete.setOnClickListener {
             viewModel.getEditMyCrop(1).observe(viewLifecycleOwner){
             }
                dialog.dismiss()
            }
            cancel.setOnClickListener { dialog.dismiss() }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }



}