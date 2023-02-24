package com.example.irrigationplanner

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.irrigationplanner.adapter.DiseaseAdapter
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.example.irrigationplanner.adapter.WeeklyAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.example.irrigationplanner.databinding.FragmentSheetHarvestBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.data.Network.NetworkModels.Irrigation
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.Sync.syncer.MyCropSyncer
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
    val myCalendar = Calendar.getInstance()


    //private lateinit var args:Bundle
    var dificiency: String = "noData"

    private var CropStageDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plotId = it.getInt("plotId")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIrrigationBinding.inflate(inflater)
setDetails()
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
        /** calling user detail api and passing the value account id in set adapter function*/
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountId = it.data?.accountId!!
            if (accountId != null) {
                setAdapter(accountId!!)
                cropStageCheck(accountId!!)
            }
            /** checking user role id and */
            if (it.data!!.roleId == 31) {
                binding.btExit.visibility = View.GONE
                binding.btHarvest.visibility = View.GONE
            } else binding.btExit.visibility = View.VISIBLE

        }

        // binding.recycleViewHis.adapter = mHistoryAdapter

        EventClickHandling.calculateClickEvent("Irrigation_landing")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("cropID", "onCreateView: $cropId")
        /** checking for crop stage if it is grape crop then showing crop stage */
//        if (cropId == 97) {
//            binding.clCropStage.visibility = View.VISIBLE
//        } else {
//            binding.clCropStage.visibility = View.GONE
//        }
        tabs()
        deleteCropDialog()

        /** if irrigation need to be added clicking yes and showing dialog*/
        binding.irrigationYes.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_irrigated_today_yes")
            dialog()
        }
        /** if irrigation need to be added clicking edit and showing dialog*/
        binding.tvEdit.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_irrigated_today_edit")
            binding.perDay.visibility = View.VISIBLE
            dialog()
        }


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

        /** when user click on no then hiding the view */
        binding.irrigationNo.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_irrigated_today_no")
            binding.dailyIrrigation.visibility = View.GONE
            binding.perDay.visibility = View.VISIBLE
        }
        /** clicking view all history  navigation history fragment*/
        binding.btHistory.setOnClickListener {
            EventClickHandling.calculateClickEvent("Irrigation_history_view")
            val args = Bundle()

            args.putParcelable("IrrigationHis", irrigation)
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_irrigationHistoryFragment, args)
        }
        /** clicking view all history  navigation disease fragment*/
        binding.btDisease.setOnClickListener {
            EventClickHandling.calculateClickEvent("Disease_outbreak_chances_view")
            val args = Bundle()
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_diseaseHistoryFragment, args)
        }
        /** clicking view all history  navigation forecast fragment*/
        binding.btForecast.setOnClickListener {
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
        /** clicking view all history  navigation cropStage fragment*/
        binding.cropStage.setOnClickListener {
            val args = Bundle()
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_cropStageFragment, args)
        }
        binding.topAppBar.setOnClickListener {
            this.findNavController().navigateUp()
        }
        /** clicking view all history  navigation harvest dialog*/
        binding.btHarvest.setOnClickListener {
            EventClickHandling.calculateClickEvent("Harvest_crop")
            val args = Bundle()
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            cropId?.let { it1 -> args.putInt("cropId", it1) }
//            this.findNavController()
//                .navigate(R.id.action_irrigationFragment_to_sheetHarvestFragment, args)
            harvestDialog()

        }
        /** clicking view all history  navigation history fragment */
        binding.tvCropInfo.setOnClickListener {
            EventClickHandling.calculateClickEvent("Irrigation_landing_crop_overview")
            val args = Bundle()
            args.putInt("plotId", plotId)
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_cropOverviewFragment, args)
        }

        binding.clCropStageClick.setOnClickListener {
            val args = Bundle()
            args.putInt("plotId", plotId)
            accountId?.let { it1 -> args.putInt("accountId", it1) }
            this.findNavController()
                .navigate(R.id.action_irrigationFragment_to_cropStageFragment, args)
        }

//        viewModel.cropHarvestedLiveData.observe(viewLifecycleOwner){
//            if(it==true){
//                viewModel.cropHarvestedLiveData.postValue(false)
//                findNavController().navigateUp()
//            }else{
//                setDetails()
//            }
//        }
        setDetails()
    }

    private fun setAdapter(accountId: Int) {
        //  viewModel.viewModelScope.launch {
//            viewModel.getIrrigationHis(accountId,plotId).observe(viewLifecycleOwner){
//                // args.putO("allHistory",it.data?.data)
//
//        }
        /** setting adapter for weekly data */
        mWeeklyAdapter = WeeklyAdapter()
        binding.recycleViewDis.adapter = mWeeklyAdapter
        mHistoryAdapter = HistoryAdapter(HistoryAdapter.DiffCallback.OnClickListener {

        })
        /** setting adapter for disease data */
        binding.recycleViewHis.adapter = mHistoryAdapter
        mDiseaseAdapter = DiseaseAdapter()
        binding.rvDis.adapter = mDiseaseAdapter



        viewModel.getIrrigationHis(accountId, plotId).observe(viewLifecycleOwner) {
//                if(it.data?.data?.irrigation?.currentData?.id!=null) {
//                    irrigationId = it.data?.data?.irrigation?.currentData?.id!!
            //args.putParcelable("irrigationHis", it.data?.data?.irrigation)
//                }
            Log.d("irrig", "${it.data}")

            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btForecast.visibility = View.GONE
                    binding.btHistory.visibility = View.GONE
                    binding.dailyIrrigation.visibility = View.GONE
                    Log.d("History", "setAdapter: loading")

                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btForecast.visibility = View.GONE
                    binding.btHistory.visibility = View.GONE
                    binding.dailyIrrigation.visibility = View.GONE
                    AppUtils.translatedToastServerErrorOccurred(context)
                    Log.d("History", "setAdapter: Error")
                }
                is Resource.Success -> {
                    /** setting adapter data for historicData data */
                    if (!it.data?.data?.irrigation?.historicData.isNullOrEmpty())
                        mHistoryAdapter.submitList(it.data?.data?.irrigation?.historicData as MutableList<HistoricData>)
                    //  binding.textViewL.text = it.data?.data?.irrigation?.historicData?.get(0)?.irrigation+" L"
                    if (!it.data?.data?.irrigation?.historicData.isNullOrEmpty() && it.data?.data?.irrigation?.historicData?.get(
                            0
                        )?.irrigation != null
                    ) {
                        binding.dailyIrrigation.visibility = View.GONE
                        binding.perDay.visibility = View.VISIBLE
                        binding.textViewL.text =
                            it.data?.data?.irrigation?.historicData?.get(0)?.irrigation + "L"
                    }
                    irrigation = it.data?.data?.irrigation!!
                    binding.progressBar.visibility = View.GONE
                    binding.btForecast.visibility = View.VISIBLE
                    binding.btHistory.visibility = View.VISIBLE
                    Log.d("History", "setAdapter: Success")
                    if (it.data?.data?.irrigation?.historicData.isNullOrEmpty()) {
                        binding.progressBar.visibility = View.GONE
                        binding.btHistory.visibility = View.GONE
                        binding.dailyIrrigation.visibility = View.GONE
                        binding.view3.visibility = View.GONE
                    }
                    if (it.data?.data?.irrigation?.irrigationForecast?.days.isNullOrEmpty()) {
                        binding.btForecast.visibility = View.GONE
                    }
                }
            }
            /** setting adapter data for weekly data */
            it.data?.data?.irrigation?.irrigationForecast?.let { it1 ->
                mWeeklyAdapter.setList(
                    it1
                )
            }
            /** setting irrigation id */
            irrigationId = it.data?.data?.irrigation?.historicData?.get(0)?.id

        }


        /** calling disease api */
        viewModel.getDisease(accountId, plotId).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    val data = it.data?.data?.currentData?.filter { itt ->
                        itt.disease?.diseaseType == "Disease"
                    }

//                    if (data.isNullOrEmpty()) {
//                        binding.noPest.visibility = View.VISIBLE
//                    } else {
//                        binding.noPest.visibility = View.GONE
//                    }
                    mDiseaseAdapter.submitList(data)
                    val data2 = it.data?.data?.currentData?.filter { itt ->
                        itt.disease?.diseaseType == "Deficiency"
                    }
                    if (data2 != null) dificiency = "dif"
                    else dificiency = "noData"
                    binding.btDisease.visibility = View.VISIBLE
                    if (it.data?.data?.historicData.isNullOrEmpty())
                        binding.btDisease.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.btDisease.visibility = View.GONE
                    binding.noPest.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.btDisease.visibility = View.GONE
                }
            }
        }
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
        }
        if (dificiency == "diff") {
            viewModel.viewModelScope.launch {
                val dif: String = TranslationsManager().getString("str_deficiency")
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(dif).setCustomView(R.layout.item_tab)
                )
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    /** tab for disease */
                    0 -> viewModel.viewModelScope.launch {
                        accountId?.let {
                            viewModel.getDisease(accountId!!, plotId).observe(viewLifecycleOwner) {
                                when (it) {
                                    is Resource.Loading -> {
                                        binding.noPest.visibility = View.GONE
                                    }
                                    is Resource.Success -> {
                                        val data = it.data?.data?.currentData?.filter { itt ->
                                            itt.disease?.diseaseType == "Disease"
                                        }

                                        if (data.isNullOrEmpty()) {
                                            binding.noPest.visibility = View.VISIBLE
                                        } else {
                                            binding.noPest.visibility = View.GONE
                                        }
                                        mDiseaseAdapter.submitList(data)
                                    }
                                    is Resource.Error -> {}
                                }


                                Log.d("hostry", "setAdapter: ${it.message}")
                            }
                        }
                    }
                    /** tab for pest*/
                    1 -> {
                        viewModel.viewModelScope.launch {
                            accountId?.let {
                                viewModel.getDisease(accountId!!, plotId)
                                    .observe(viewLifecycleOwner) {
                                        when (it) {
                                            is Resource.Loading -> {
                                                binding.noPest.visibility = View.GONE
                                            }
                                            is Resource.Success -> {
                                                val data =
                                                    it.data?.data?.currentData?.filter { itt ->
                                                        itt.disease?.diseaseType == "Pest"
                                                    }
                                                Log.d("irrigationPlanner", "setAdapter: $data")
                                                if (data.isNullOrEmpty()) {
                                                    binding.noPest.visibility = View.VISIBLE
                                                } else {
                                                    binding.noPest.visibility = View.GONE
                                                }
                                                mDiseaseAdapter.submitList(data)
                                                Log.d("hostry", "setAdapter: ${it.message}")
                                            }
                                            is Resource.Error -> {}
                                        }
                                    }
                            }
                        }
                    }
                    /** tab for Deficiency*/
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

    /** dialog for irrigation for today */
    private fun dialog() {
        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialog)
        dialog.setContentView(R.layout.irrigation_pre_day)
        val close = dialog.findViewById<ImageView>(R.id.close)
        val save = dialog.findViewById<Button>(R.id.savePreDayL) as Button
        val irrigation = dialog.findViewById<EditText>(R.id.etPerDay)
        val irrigationDone = dialog.findViewById<TextView>(R.id.textView13)
        if (irrigationDone != null) {
            TranslationsManager().loadString("str_irrigation_per_plant", irrigationDone)
        }
        viewModel.viewModelScope.launch {
            val saveTv = TranslationsManager().getString("str_save")
            save.text = saveTv
        }
        save.setOnClickListener {
            EventClickHandling.calculateClickEvent("Irrigation_plant_save")
            val value = irrigation?.text.toString().toInt()
            if (irrigationId != null) {
                viewModel.updateIrrigation(irrigationId!!, value).observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            binding.dailyIrrigation.visibility = View.GONE
                            binding.perDay.visibility = View.VISIBLE
                            binding.textViewL.text = value.toString() + "L"
                        }
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                    }


                }
            }
            dialog.dismiss()
        }
        close!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    /** delete a crop dialog */
    private fun deleteCropDialog() {
        EventClickHandling.calculateClickEvent("Irrigation_delete")
        binding.btExit.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_delete_irrigartion)
            val cancel = dialog.findViewById(R.id.cancel) as Button
            val delete = dialog.findViewById(R.id.delete) as Button
            val deleteTv = dialog.findViewById<TextView>(R.id.textView14)
            val deleteDesc = dialog.findViewById<TextView>(R.id.textView15)

            /** yes button to delete crop*/
            delete.setOnClickListener {
                EventClickHandling.calculateClickEvent("Confirm_delete_crop")
                viewModel.getEditMyCrop(plotId).observe(viewLifecycleOwner) {
                    this.findNavController().navigateUp()
                }
                dialog.dismiss()
            }
            /** no button to delete crop*/
            cancel.setOnClickListener {
                dialog.dismiss()
                EventClickHandling.calculateClickEvent("Confirm_cancel_crop")
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            //translation
            TranslationsManager().loadString("str_delete", deleteTv, "Delete")
            TranslationsManager().loadString(
                "str_delete_crop_desc",
                deleteDesc,
                "Are you sure? Do you want to delete this crop?"
            )
            viewModel.viewModelScope.launch {
                val deletetv = TranslationsManager().getString("str_delete")
                delete.text = deletetv
                val cancelTv = TranslationsManager().getString("str_cancel")
                cancel.text = cancelTv
            }

        }
    }

    private fun setDetails() {

//        Log.d("CropName2", "setDetails: $cropName")

        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            val data = it.data?.first { plot ->
                plot.id == plotId
            }
            cropId = data?.cropId
            cropLogo = data?.cropLogo
            cropName = data?.cropName

            Glide.with(requireContext()).load(cropLogo).into(binding.imageView)
            binding.textView.text = cropName

            if (cropId == 97) {
                binding.clCropStage.visibility = View.VISIBLE
            } else {
                binding.clCropStage.visibility = View.GONE
            }

            if(data?.farmId == null){
                binding.gwxIrrigation.visibility = View.GONE
                binding.noDeviceCv.visibility = View.VISIBLE
                binding.tvIrrigationMessage.text = "Crop is not associated with the farm. Kindly add crop to the farm."
            }else if(data.device == null){
                binding.gwxIrrigation.visibility = View.GONE
                binding.noDeviceCv.visibility = View.VISIBLE
                binding.tvIrrigationMessage.text = "Device is not added to farm. Kindly add the device to farm."
            }
            else if(!data.device.equals("GWX", ignoreCase = true)){
                binding.gwxIrrigation.visibility = View.GONE
                binding.noDeviceCv.visibility = View.VISIBLE
                binding.tvIrrigationMessage.text = "Irrigation planner is not availble. Contact the customer support for further information."
            }
            else if(data.irrigationPlannerForThisCrop != true){
                binding.gwxIrrigationCl.visibility = View.GONE
                binding.noDeviceCv.visibility = View.VISIBLE
                binding.tvIrrigationMessage.text = "Currently irrigation planner is not avialble for the selected crop."
            }
            else if(checkForCropMissingInfo(data)){
                binding.gwxIrrigationCl.visibility = View.GONE
                binding.noDeviceCv.visibility = View.VISIBLE
                binding.tvIrrigationMessage.text = "Crop have some missing information. Kindly delete the selected crop and add the same crop again with necessary information."
            }
            else if(checkingSowingFutureDate(data.sowingDate)){
                binding.gwxIrrigationCl.visibility = View.GONE
                binding.noDeviceCv.visibility = View.VISIBLE
                binding.tvIrrigationMessage.text = "Irrigation planner will be available, once the sowing date is reached."
            }

            if(data?.diseaseDetectionForThisCrop!= true){
                binding.gwxDiseaseCl.visibility = View.GONE
            }


            viewModel.viewModelScope.launch {
                if (data?.irrigationRequired == false) {
                    TranslationsManager().loadString(
                        "str_irrigation_not_req",
                        binding.irrigationReq,
                        "Irrigation Not Required"
                    )
                    binding.cardView.setCardBackgroundColor(resources.getColor(R.color.LightGreen))
                    binding.ivCloudy.setImageDrawable(resources.getDrawable(R.drawable.ic_green_irrigation))
                    binding.irrigationReq.text =
                        TranslationsManager().getString("str_irrigation_not_req")
                } else if (data?.irrigationRequired == true) {
                    TranslationsManager().loadString(
                        "str_irrigation_req",
                        binding.irrigationReq,
                        "Irrigation Required"
                    )
                    binding.cardView.setCardBackgroundColor(resources.getColor(R.color.LightREd))
                    binding.ivCloudy.setImageDrawable(resources.getDrawable(R.drawable.ic_red_irrigation))
                } else {
                    binding.irrigationReq.text = "-NA-"
                }
            }

            if (data?.irrigationType == "Drip Irrigation") {
                TranslationsManager().loadString(
                    "str_irrigation_done",
                    binding.tvPerDay,
                    "Irrigation done today per Plant"
                )
            } else {
                TranslationsManager().loadString(
                    "str_irrigation_done_area",
                    binding.tvPerDay,
                    "Irrigation done today per area"
                )
            }

            CropStageDate = data?.sowingDate

//            CropStageDate?.let { it1 -> checkingSowingFutureDate(it1) }
        }
    }

    private fun checkForCropMissingInfo(data: MyCropDataDomain): Boolean {
        if(data.sowingDate.isNullOrEmpty())
            return true
        if(data.area.isNullOrEmpty() || data.area?.toDouble()!! <= 0.0)
            return true
        if(data.soilType.isNullOrEmpty())
            return true
        if(data.irrigationType.isNullOrEmpty())
            return true
        return false
    }

    private fun translation() {
//        TranslationsManager().loadString("",binding.graps)
        TranslationsManager().loadString("str_view_all", binding.cropStage, "View all")
        TranslationsManager().loadString("str_irrigation", binding.textView3, "Irrigation")
        TranslationsManager().loadString(
            "str_crop _nformation",
            binding.tvCropInfo,
            "Crop information"
        )
        TranslationsManager().loadString("str_today", binding.textView4, "Today")
        TranslationsManager().loadString(
            "str_weekly_irrigation",
            binding.textView5,
            "Weekly Irrigation Forecast"
        )
        TranslationsManager().loadString(
            "str_have_you _irrigated",
            binding.textView6,
            "Have you Irrigated your Crop today?"
        )
        TranslationsManager().loadString("view_details", binding.tvViewDeatils, "View details")
        TranslationsManager().loadString("view_details", binding.tvViewDetails2, "View details")
        TranslationsManager().loadString("view_details", binding.viewDetails3, "View details")
        TranslationsManager().loadString("str_edit", binding.tvEdit, "Edit")
        TranslationsManager().loadString(
            "str_risk_outbreak",
            binding.tvTodayRisk,
            "Today Risk Outbreak Chances"
        )
        TranslationsManager().loadString(
            "str_irrigation_history",
            binding.textView8,
            "Irrigation History"
        )
        TranslationsManager().loadString("str_crop_stage", binding.graps, "Crop Stage")
        TranslationsManager().loadString("topping", binding.topping, "Topping")
//        TranslationsManager().loadString("str_irrigation_history",binding.textView8)
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("IrrigationFragment")
    }

    private fun harvestDialog() {

        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialog)
        val harvestBinding: FragmentSheetHarvestBinding =
            FragmentSheetHarvestBinding.inflate(layoutInflater)
        TranslationsManager().loadString(
            "str_harvest_details",
            harvestBinding.tvHarvestDetails,
            "Harvest Details"
        )
        TranslationsManager().loadString(
            "str_actual_yeild",
            harvestBinding.textView14,
            "Actual Yield in Tone"
        )
        TranslationsManager().loadString(
            "str_actual_harvest_date",
            harvestBinding.tvHarvestDetails,
            "Actual Harvest Date"
        )
        dialog.setContentView(harvestBinding.root)
        harvestBinding.close.setOnClickListener {
            dialog.dismiss()
        }
        harvestBinding.save.setOnClickListener {
            EventClickHandling.calculateClickEvent("Harvest_details_save")
            var date = harvestBinding.editText2.text.toString()
            if (harvestBinding.editText.text.toString() != "" && date != "") {
                var yield_tone = harvestBinding.editText.text.toString().toInt()
                viewModel.updateHarvest(plotId, accountId!!, cropId!!, date, yield_tone)
                    .observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                    MyCropSyncer().invalidateSync()
                                    LocalSource.deleteAllMyCrops()
                                    MyCropSyncer().getMyCrop()
                                }
                                findNavController().navigateUp()
//                                    viewModel.setCropHarvested()
                                dialog.dismiss()
                            }
                            is Resource.Loading -> {}
                            is Resource.Error -> {
                                AppUtils.translatedToastServerErrorOccurred(context)
                            }
                        }
                    }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastEnterFields = TranslationsManager().getString("toast_enter_all_field")
                    if (!toastEnterFields.isNullOrEmpty()) {
                        context?.let { it1 ->
                            ToastStateHandling.toastError(
                                it1, toastEnterFields,
                                Toast.LENGTH_SHORT
                            )
                        }
                    } else {
                        context?.let { it1 ->
                            ToastStateHandling.toastError(
                                it1, "Please enter all the mandatory fields",
                                Toast.LENGTH_SHORT
                            )
                        }
                    }
                }

            }
        }
        harvestBinding.editText2.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val month = monthOfYear + 1
                    // Display Selected date in textbox
                    val inputDateFormatter: SimpleDateFormat =
                        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val outputDateFormatter: SimpleDateFormat =
                        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val date2: Date = inputDateFormatter.parse("$year-$month-$dayOfMonth")
                    harvestBinding.editText2.text = outputDateFormatter.format(date2)
                    // date1 = binding.cal1.text.toString()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        dialog.show()
    }

    private fun cropStageCheck(accountId: Int) {
        viewModel.getCropStage(accountId, plotId).observe(viewLifecycleOwner) {
            if (it.data?.data != null)
                if (it.data?.data!![0].date?.first() != null) {
                    for (i in 0 until it.data?.data!!.size)
                        if (it.data?.data!![i].date == null) {
                            binding.topping.text = it.data?.data!![i - 1].stageName
                            val image = it.data?.data!![i - 1].stageIcon
                            Glide.with(requireContext()).load(image).into(binding.cropStageImg)
                            binding.cropStageDate.text = it.data?.data!![i - 1].date
                            break
                        }
                } else {
                    binding.topping.text = it.data?.data!!.first().stageName
                    val image = it.data?.data!!.first().stageIcon
                    Glide.with(requireContext()).load(image).into(binding.cropStageImg)
                    binding.cropStageDate.text = CropStageDate
                }
        }
    }

    private fun checkingSowingFutureDate(sowingDate: String?):Boolean {
        val currentTime = Calendar.getInstance().time

// Create a Date object for the date you want to compare
        val sowingDate = SimpleDateFormat("yyyy-MM-dd").parse(sowingDate)

// Compare the two dates
        if (sowingDate != null) {
            if (sowingDate.after(currentTime)) {
                return true
                // The compareDate is in the future
                // Do something
//                binding.noDeviceCv.visibility = View.GONE
//                binding.FutureCv.visibility = View.VISIBLE
//                binding.gwxIrrigation.visibility = View.GONE
//                binding.btHarvest.visibility = View.GONE
            }
        }
        return false
    }
}