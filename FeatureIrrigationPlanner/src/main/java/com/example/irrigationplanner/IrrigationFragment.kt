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
import android.widget.*
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
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Network.NetworkModels.Irrigation
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch

class IrrigationFragment : Fragment() {
    private lateinit var binding: FragmentIrrigationBinding
    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    private lateinit var mHistoryAdapter: HistoryAdapter
    private lateinit var mDiseaseAdapter: DiseaseAdapter
    private lateinit var mWeeklyAdapter: WeeklyAdapter
    private var irrigation: Irrigation? = null
    private var irrigationReq = 1
    private var plotId: Int = 0
    var accountId: Int? = null
    private var cropId: Int? = null
    private var cropName: String? = null
    private var cropLogo: String? = null
    private var irrigationId: Int? = null

    //private lateinit var args:Bundle
    var dificiency: String = "noData"

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

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountId = it.data?.accountId!!
            if (accountId != null)
                setAdapter(accountId!!)
            if(it.data!!.roleId==31)
            binding.btExit.visibility = View.GONE
            else binding.btExit.visibility = View.VISIBLE
        }

        // binding.recycleViewHis.adapter = mHistoryAdapter

        EventClickHandling.calculateClickEvent("Irrigation_landing")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("cropID", "onCreateView: $cropId")
        if (cropId == 97) {
            binding.clCropStage.visibility = View.VISIBLE
        } else {
            binding.clCropStage.visibility = View.GONE
        }
        tabs()
        exitDialog()

        binding.irrigationYes.setOnClickListener() {
            EventClickHandling.calculateClickEvent("crop_irrigated_today_yes")
            dialog()
        }
        binding.tvEdit.setOnClickListener() {
            EventClickHandling.calculateClickEvent("crop_irrigated_today_edit")
            binding.perDay.visibility = View.VISIBLE
            dialog()
        }


        setDetails()
        translation()


        var yes: String
        var no: String
        var delete: String
        var harvest: String
        viewModel.viewModelScope.launch {
            yes = TranslationsManager().getString("str_yes")
            binding.irrigationYes.text = yes
            no = TranslationsManager().getString("str_no")
            binding.irrigationNo.text = no
            delete = TranslationsManager().getString("str_delete_crop")
            binding.btExit.text = delete
            harvest = TranslationsManager().getString("str_harvest_crop")
            binding.btHarvest.text = harvest

        }

        binding.irrigationNo.setOnClickListener() {
            EventClickHandling.calculateClickEvent("crop_irrigated_today_no")
            binding.dailyIrrigation.visibility = View.GONE
            binding.perDay.visibility = View.VISIBLE
        }
        binding.tvEdit.setOnClickListener() {
            binding.perDay.visibility = View.VISIBLE
//            val dialog = BottomSheetDialog(this.requireContext(),R.style.BottomSheetDialog)
//            dialog.setContentView(R.layout.irrigation_pre_day)
//            val close = dialog.findViewById<ImageView>(R.id.close)
//            close!!.setOnClickListener(){
//                dialog.dismiss()
//            }
//            dialog.show()
            dialog()
        }

        binding.btHistory.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Irrigation_history_view")
            val args = Bundle()
            args.putParcelable("IrrigationHis", irrigation)
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_irrigationHistoryFragment, args)
        }
        binding.btDisease.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Disease_outbreak_chances_view")
            val args = Bundle()
            args.putParcelable("IrrigationHis", irrigation)
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_diseaseHistoryFragment, args)
        }

        binding.btForecast.setOnClickListener() {
            EventClickHandling.calculateClickEvent("weekly_irrigation_forecast_view")
            val args = Bundle()
            if (irrigation != null) {
                args.putParcelable("IrrigationHis", irrigation)
                args.putInt("plotId", plotId)
            }
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_forecastFragment, args)
        }
        binding.cropStage.setOnClickListener() {
            val args = Bundle()
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_cropStageFragment, args)
        }
        binding.topAppBar.setOnClickListener() {
            this.findNavController().navigateUp()
        }

        binding.btHarvest.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Harvest_crop")
            val args = Bundle()
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            cropId?.let { it1 -> args.putInt("cropId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_sheetHarvestFragment, args)

        }
        binding.tvCropInfo.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Irrigation_landing_crop_overview")
            val args = Bundle()
            args.putInt("plotId", plotId)
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_cropOverviewFragment, args)
        }
    }

    private fun setAdapter(accountId: Int) {
        //  viewModel.viewModelScope.launch {
//            viewModel.getIrrigationHis(accountId,plotId).observe(viewLifecycleOwner){
//                // args.putO("allHistory",it.data?.data)
//
//        }
        mWeeklyAdapter = WeeklyAdapter()
        binding.recycleViewDis.adapter = mWeeklyAdapter
        mHistoryAdapter = HistoryAdapter(HistoryAdapter.DiffCallback.OnClickListener {

        })
        binding.recycleViewHis.adapter = mHistoryAdapter
        mDiseaseAdapter = DiseaseAdapter()
        binding.rvDis.adapter = mDiseaseAdapter


        viewModel.viewModelScope.launch {
            viewModel.getIrrigationHis(accountId, plotId).observe(viewLifecycleOwner) {
//                if(it.data?.data?.irrigation?.currentData?.id!=null) {
//                    irrigationId = it.data?.data?.irrigation?.currentData?.id!!
                //args.putParcelable("irrigationHis", it.data?.data?.irrigation)
//                }
                viewModel.viewModelScope.launch {
                    if (it.data?.data?.irrigation?.currentData?.irrigation != null)
                        binding.irrigationReq.text =
                            TranslationsManager().getString("str_irrigation_not_req")
                    else binding.irrigationReq.text =
                        TranslationsManager().getString("str_Irrigation_req")
                }

                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btForecast.visibility = View.GONE
                        binding.btDisease.visibility = View.GONE
                        binding.btHistory.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btForecast.visibility = View.GONE
                        binding.btDisease.visibility = View.GONE
                        binding.btHistory.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        //history
                        mHistoryAdapter.submitList(it.data?.data?.irrigation?.historicData)
                        //  binding.textViewL.text = it.data?.data?.irrigation?.historicData?.get(0)?.irrigation+" L"
                        if (it.data?.data?.irrigation?.historicData?.get(0)?.irrigation != null) {
                            binding.dailyIrrigation.visibility = View.GONE
                            binding.perDay.visibility = View.VISIBLE
                            binding.textViewL.text =
                                it.data?.data?.irrigation?.historicData?.get(0)?.irrigation + "L"
                        }
                        irrigation = it.data?.data?.irrigation!!
                        binding.progressBar.visibility = View.GONE
                        binding.btForecast.visibility = View.VISIBLE
                        binding.btDisease.visibility = View.VISIBLE
                        binding.btHistory.visibility = View.VISIBLE
                    }
                }
                //weekly
                it.data?.data?.irrigation?.irrigationForecast?.let { it1 ->
                    mWeeklyAdapter.setList(
                        it1
                    )
                }


                //disease


                irrigationId = it.data?.data?.irrigation?.historicData?.get(0)?.id

            }

        }
        viewModel.getDisease(accountId, plotId).observe(viewLifecycleOwner) {
            val data = it.data?.data?.currentData?.filter { itt ->
                itt.disease?.diseaseType == "Disease"
            }
            mDiseaseAdapter.submitList(data)
            val data2 = it.data?.data?.currentData?.filter { itt ->
                itt.disease?.diseaseType == "Deficiency"
            }
            if (data2 != null) dificiency = "dif"
            else dificiency = "noData"
        }
    }

    private fun tabs() {

        var Pest: String
        var Def: String
        viewModel.viewModelScope.launch {
            var Disease: String
            Disease = TranslationsManager().getString("str_disease")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(Disease).setCustomView(R.layout.item_tab)
            )

            var pest: String
            pest = TranslationsManager().getString("str_pest")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(pest).setCustomView(R.layout.item_tab)
            )
        }
        if (dificiency == "diff") {
            viewModel.viewModelScope.launch {
                var dif: String
                dif = TranslationsManager().getString("str_deficiency")
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(dif).setCustomView(R.layout.item_tab)
                )
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> viewModel.viewModelScope.launch {
                        accountId?.let {
                            viewModel.getDisease(accountId!!, plotId).observe(viewLifecycleOwner) {
                                //                        val i = it.data?.data?.disease?.size?.minus(1)
                                //                        while (i!=0) {
                                val data = it.data?.data?.currentData?.filter { itt ->
                                    itt.disease?.diseaseType == "Disease"
                                }
                                mDiseaseAdapter.submitList(data)
                                Log.d("hostry", "setAdapter: ${it.message}")
                                //                        }
                            }
                        }
                    }
                    1 -> {
                        viewModel.viewModelScope.launch {
                            accountId?.let {
                                viewModel.getDisease(accountId!!, plotId)
                                    .observe(viewLifecycleOwner) {
                                        //                        val i = it.data?.data?.disease?.size?.minus(1)
                                        //                        while (i!=0) {
                                        val data = it.data?.data?.currentData?.filter { itt ->
                                            itt.disease?.diseaseType == "Pest"
                                        }
                                        mDiseaseAdapter.submitList(data)
                                        Log.d("hostry", "setAdapter: ${it.message}")
                                        //                        }
                                    }
                            }
                        }
                    }
                    2 -> {
                        viewModel.viewModelScope.launch {
                            accountId?.let {
                                viewModel.getDisease(accountId!!, plotId)
                                    .observe(viewLifecycleOwner) {
                                        //                        val i = it.data?.data?.disease?.size?.minus(1)
                                        //                        while (i!=0) {
                                        val data = it.data?.data?.currentData?.filter { itt ->
                                            itt.disease?.diseaseType == "Deficiency"
                                        }
                                        mDiseaseAdapter.submitList(data)
                                        Log.d("hostry", "setAdapter: ${it.message}")
                                        //                        }
                                    }
                            }
                        }
                    }

                }
            }

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
        val irrigationDone = dialog.findViewById<TextView>(R.id.textView13)


        //translation
        if (irrigationDone != null) {
            TranslationsManager().loadString("str_irrigation_per_plant", irrigationDone)
        }
//            if (irrigationPer != null) {
//                TranslationsManager().loadString("str_enter_water",irrigationPer)
//            }
        viewModel.viewModelScope.launch {
            val saveTv = TranslationsManager().getString("str_save")
            save.text = saveTv
        }


        save.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Irrigation_plant_save")
            // context?.let { it1 -> ToastStateHandling.toastSuccess(it1, "Worked", Toast.LENGTH_SHORT) }
            val value = irrigation?.text.toString().toInt()
            if (irrigationId != null) {
                viewModel.updateIrrigation(irrigationId!!, value).observe(viewLifecycleOwner) {
                    binding.textViewL.text = value.toString() + "L"
                }
            }
            dialog.dismiss()
        }
        close!!.setOnClickListener() {
            dialog.dismiss()
            //context?.let { it1 -> ToastStateHandling.toastSuccess(it1, "Worked", Toast.LENGTH_SHORT) }
        }
        dialog.show()

    }

    private fun exitDialog() {
        EventClickHandling.calculateClickEvent("Irrigation_delete")
        binding.btExit.setOnClickListener() {

            val dialog = Dialog(requireContext())

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_delete_irrigartion)
            // val body = dialog.findViewById(R.id.body) as TextView
            val cancel = dialog.findViewById(R.id.cancel) as Button
            val delete = dialog.findViewById(R.id.delete) as Button
            val deleteTv = dialog.findViewById<TextView>(R.id.textView14)
            val deleteDesc = dialog.findViewById<TextView>(R.id.textView15)

            delete.setOnClickListener {
                EventClickHandling.calculateClickEvent("Confirm_delete_crop")
                viewModel.getEditMyCrop(plotId).observe(viewLifecycleOwner) {
                    this.findNavController().navigateUp()
                }
                dialog.dismiss()
            }
            cancel.setOnClickListener { dialog.dismiss()
                EventClickHandling.calculateClickEvent("Confirm_cancel_crop")}
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            //translation
            TranslationsManager().loadString("str_delete", deleteTv,"Delete")
            TranslationsManager().loadString("str_delete_crop_desc", deleteDesc,"Are you sure? Do you want to delete this crop?")
            viewModel.viewModelScope.launch {
                val deletetv = TranslationsManager().getString("str_delete")
                delete.text = deletetv
                val cancelTv = TranslationsManager().getString("str_cancel")
                cancel.text = cancelTv
            }

        }
    }

    private fun setDetails() {
        Glide.with(requireContext()).load(cropLogo).into(binding.imageView)
        binding.textView.text = cropName
        Log.d("CropName2", "setDetails: $cropName")

        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            val data = it.data?.first { plot->
                plot.id == plotId
            }
            if(data?.irrigationRequired==null){
                binding.gwxIrrigation.visibility=View.GONE
                binding.btHarvest.visibility=View.GONE
                binding.noDeviceCv.visibility=View.VISIBLE
            }else{
                binding.gwxIrrigation.visibility=View.VISIBLE
                binding.btHarvest.visibility=View.VISIBLE
                binding.noDeviceCv.visibility=View.GONE


            }
        }
    }

    private fun translation() {
//        TranslationsManager().loadString("",binding.graps)
        TranslationsManager().loadString("str_view_all", binding.cropStage,"View all")
        TranslationsManager().loadString("str_irrigation", binding.textView3,"Irrigation")
        TranslationsManager().loadString("str_crop _nformation", binding.tvCropInfo,"Crop information")
        TranslationsManager().loadString("str_today", binding.textView4,"Today")
        TranslationsManager().loadString("str_weekly_irrigation", binding.textView5,"Weekly Irrigation Forecast")
        TranslationsManager().loadString("str_have_you _irrigated", binding.textView6,"Have you Irrigated your Crop today?")
        TranslationsManager().loadString("str_irrigation_done", binding.tvPerDay,"Irrigation done today per Plant")
        TranslationsManager().loadString("str_view_all", binding.tvViewDeatils,"View details")
        TranslationsManager().loadString("str_view_all", binding.tvViewDetails2,"View details")
        TranslationsManager().loadString("str_view_all", binding.viewDetails3,"View details")
        TranslationsManager().loadString("str_edit", binding.tvEdit,"Edit")
        TranslationsManager().loadString("str_risk_outbreak", binding.textView9,"Risk Outbreak Chances")
        TranslationsManager().loadString("str_irrigation_history", binding.textView8,"Irrigation History")
//        TranslationsManager().loadString("str_irrigation_history",binding.textView8)


    }
}