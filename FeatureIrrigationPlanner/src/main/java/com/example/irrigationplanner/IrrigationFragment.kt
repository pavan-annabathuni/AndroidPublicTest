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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.irrigationplanner.adapter.DiseaseAdapter
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.example.irrigationplanner.adapter.WeeklyAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Network.NetworkModels.Irrigation
import kotlinx.coroutines.launch

class IrrigationFragment : Fragment() {
     private lateinit var binding: FragmentIrrigationBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private lateinit var mHistoryAdapter: HistoryAdapter
    private lateinit var mDiseaseAdapter: DiseaseAdapter
    private lateinit var mWeeklyAdapter: WeeklyAdapter
    private lateinit var irrigation: Irrigation
    private var irrigationReq = 1
    private var plotId:Int = 0
    var accountId:Int?= null
    private var cropId:Int? = null
    private var cropName:String? = null
    private var cropLogo:String? = null
    private var irrigationId:Int? = null
    //private lateinit var args:Bundle
     var dificiency:String = "noData"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plotId = it.getInt("plotId")
            cropId = it.getInt("cropId")
            cropLogo = it.getString("cropLogo")
            cropName = it.getString("cropName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIrrigationBinding.inflate(inflater)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@IrrigationFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
    if(cropId == 97){
            binding.clCropStage.visibility = View.VISIBLE
        }else{
            binding.clCropStage.visibility = View.GONE
        }
        viewModel.getUserDetails().observe(viewLifecycleOwner){
             accountId = it.data?.accountId!!
        if(accountId!=null)
            setAdapter(accountId!!)
        }
        binding.tvCropInfo.setOnClickListener(){
            this.findNavController().navigate(R.id.action_irrigationFragment_to_cropOverviewFragment)
        }
       // binding.recycleViewHis.adapter = mHistoryAdapter


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        setDetails()

    }

    private fun onClick() {
        val args = Bundle()
        args.putInt("plotId",plotId)
        binding.btHarvest.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.
            actionIrrigationFragmentToSheetHarvestFragment())

        }

        binding.irrigationNo.setOnClickListener(){
            binding.dailyIrrigation.visibility = View.GONE
            binding.perDay.visibility = View.VISIBLE
        }
        binding.tvEdit.setOnClickListener(){
            binding.perDay.visibility = View.VISIBLE
            val dialog = BottomSheetDialog(this.requireContext(),R.style.BottomSheetDialog)
            dialog.setContentView(R.layout.irrigation_pre_day)
            val close = dialog.findViewById<ImageView>(R.id.close)
            close!!.setOnClickListener(){
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btHistory.setOnClickListener(){
            val args = Bundle()
            args.putParcelable("IrrigationHis",irrigation)
            args.putInt("plotId",plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController().navigate(R.id.action_irrigationFragment_to_irrigationHistoryFragment,args)
        }
        binding.btDisease.setOnClickListener(){
            this.findNavController().navigate(R.id.action_irrigationFragment_to_diseaseHistoryFragment)
        }

        binding.btForecast.setOnClickListener(){
            val args = Bundle()
            args.putParcelable("IrrigationHis",irrigation)
            args.putInt("plotId",plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController().navigate(R.id.action_irrigationFragment_to_forecastFragment,args)
        }
        binding.cropStage.setOnClickListener(){
            val args = Bundle()
            args.putInt("plotId",plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController().navigate(R.id.action_irrigationFragment_to_cropStageFragment,args)
        }
        binding.topAppBar.setOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

    private fun setAdapter(accountId:Int) {
        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(accountId,plotId).observe(viewLifecycleOwner){
                // args.putO("allHistory",it.data?.data)
                if(it.data?.data?.irrigation?.currentData?.irrigation !=null)
                    binding.irrigationReq.text = "Irrigation Not Required"
                else binding.irrigationReq.text = "Irrigation Required"
            }
        }
        mWeeklyAdapter = WeeklyAdapter()
        binding.recycleViewDis.adapter = mWeeklyAdapter
        mHistoryAdapter = HistoryAdapter(HistoryAdapter.DiffCallback.OnClickListener {

        })
        binding.recycleViewHis.adapter = mHistoryAdapter
        mDiseaseAdapter = DiseaseAdapter()
        binding.rvDis.adapter = mDiseaseAdapter


        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(accountId,plotId).observe(viewLifecycleOwner){
//                if(it.data?.data?.irrigation?.currentData?.id!=null) {
//                    irrigationId = it.data?.data?.irrigation?.currentData?.id!!
                //args.putParcelable("irrigationHis", it.data?.data?.irrigation)
//                }
                //weekly
                it.data?.data?.irrigation?.irrigationForecast?.let { it1 ->
                    mWeeklyAdapter.setList(
                        it1
                    ) }
                //history
                mHistoryAdapter.submitList(it.data?.data?.irrigation?.historicData)
              //  binding.textViewL.text = it.data?.data?.irrigation?.historicData?.get(0)?.irrigation+" L"
                if(it.data?.data?.irrigation?.historicData?.get(0)?.irrigation!=null){
                    binding.dailyIrrigation.visibility = View.GONE
                    binding.perDay.visibility = View.VISIBLE
                    binding.textViewL.text = it.data?.data?.irrigation?.historicData?.get(0)?.irrigation+"L"
                }
                irrigation = it.data?.data?.irrigation!!

                //disease
                val data = it.data?.data?.disease?.filter { itt ->
                    itt.disease.diseaseType == "Disease"
                }
                mDiseaseAdapter.submitList(data)
                val data2 = it.data?.data?.disease?.filter { itt ->
                    itt.disease.diseaseType == "Deficiency"
                }
                if(data2!=null) dificiency = "dif"
                else dificiency = "noData"

                irrigationId = it.data?.data?.irrigation?.historicData?.get(0)?.id

            }

        }
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
                       accountId?.let {
                           viewModel.getIrrigationHis(it,plotId).observe(viewLifecycleOwner) {
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
                    }
                    1->{viewModel.viewModelScope.launch {
                        accountId?.let {
                            viewModel.getIrrigationHis(it,plotId).observe(viewLifecycleOwner) {
                    //                        val i = it.data?.data?.disease?.size?.minus(1)
                    //                        while (i!=0) {
                                val data = it.data?.data?.disease?.filter { itt ->
                                    itt.disease.diseaseType == "Pest"
                                }
                                mDiseaseAdapter.submitList(data)
                                Log.d("hostry", "setAdapter: ${it.message}")
                    //                        }
                            }
                        }
                    }}
                    2->{viewModel.viewModelScope.launch {
                        accountId?.let {
                            viewModel.getIrrigationHis(it,plotId).observe(viewLifecycleOwner) {
                    //                        val i = it.data?.data?.disease?.size?.minus(1)
                    //                        while (i!=0) {
                                val data = it.data?.data?.disease?.filter { itt ->
                                    itt.disease.diseaseType == "Deficiency"
                                }
                                mDiseaseAdapter.submitList(data)
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

    private fun dialog() {

        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialog)
        dialog.setContentView(R.layout.irrigation_pre_day)
        val close = dialog.findViewById<ImageView>(R.id.close)
        val save = dialog.findViewById<Button>(R.id.savePreDayL) as Button
        val irrigation = dialog.findViewById<EditText>(R.id.etPerDay)

        save.setOnClickListener() {
            val value = irrigation?.text.toString().toInt()
            irrigationId?.let { it1 ->
                viewModel.updateIrrigation(it1, value).observe(viewLifecycleOwner) {
                  //  Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        close!!.setOnClickListener() {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun exitDialog(){
        binding.btExit.setOnClickListener(){
            val dialog = Dialog(requireContext())

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_delete_irrigartion)
            // val body = dialog.findViewById(R.id.body) as TextView
            val cancel = dialog.findViewById(R.id.cancel) as Button
            val delete = dialog.findViewById(R.id.delete) as Button
            delete.setOnClickListener {
             viewModel.getEditMyCrop(plotId).observe(viewLifecycleOwner){
             }
                dialog.dismiss()
            }
            cancel.setOnClickListener { dialog.dismiss() }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    private fun setDetails(){
        Glide.with(requireContext()).load(cropLogo).into(binding.imageView)
        binding.textView.text = cropName
        Log.d("CropName2", "setDetails: $cropName")
    }
}