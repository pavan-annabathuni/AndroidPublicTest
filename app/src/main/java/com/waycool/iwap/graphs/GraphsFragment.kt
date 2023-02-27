package com.waycool.iwap.graphs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Network.NetworkModels.GraphViewData
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentGraphsBinding
import com.waycool.iwap.premium.ViewDeviceViewModel
import com.waycool.iwap.utils.CustomMarkerView
import com.waycool.uicomponents.utils.DateFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class GraphsFragment : Fragment() {
    private var updateDate: String? = null
    private var paramValue: Double? = null
    private var paramType: String? = null
    private var deviceModelId: Int? = null
    private var serialNo: Int? = null
    private var graphsData: GraphViewData? = null

    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }

    private val inputDateFormatter: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    private val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    private val LAST_DAYS: Int = 7
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) activity?.let { it.finish() }
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }

        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) activity?.let { it1 -> it1.finish() }
        }
        return binding.root
    }

    enum class GraphSelection {
        LAST12HRS, LAST7DAYS, LAST30DAYS
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            serialNo = arguments?.getInt("serial_no")
            deviceModelId = arguments?.getInt("device_model_id")
            paramType = arguments?.getString("value")
            paramValue = arguments?.getDouble("temp_value")
            updateDate = arguments?.getString("date_time")

           val data = arguments?.getString("toolbar")
            translationToolBar(data.toString())
            binding.tvToolbar.text = arguments?.getString("toolbar")
            binding.paramValue.text = "$paramValue${paramType?.let { getUnits(it) }}"
            if(paramType.equals("leaf_wetness_hrs",ignoreCase = true)){
                binding.paramValue.text = if(paramValue == 0.0) "Dry" else "Wet"
            }
            binding.date.text = DateFormatUtils.dateFormatterDevice(updateDate)

            populateGraph(paramType, GraphSelection.LAST12HRS)
            graphApiData(serialNo, deviceModelId, paramType)


        }
        initClicks()
        setTranslation()

        tabs()

    }

    private fun populateGraph(paramType: String?, duration: GraphSelection) {
        if(graphsData ==null){
            binding.tvParamNote.text = "Loading..."
            binding.paramProgressBar.visibility = View.VISIBLE
            binding.lineChart.invalidate()
        }
        if (paramType != null && graphsData != null) {

            val keysList = getKeyList(duration)
            val valList = getValueList(duration)

            if (valList.isNullOrEmpty()) {
                binding.tvParamNote.text = "Data not available"
                binding.paramProgressBar.visibility = View.INVISIBLE
                binding.lineChart.invalidate()
                binding.lineChart.clear();
                return
            }

            binding.tvParamNote.text = getParamNote(paramType, valList, duration)
            binding.paramProgressBar.visibility = View.GONE

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until valList.size.let { keysList?.size?.coerceAtMost(it) }!!) {
                val entryVal: Float = if (valList[i] == null) 0.0F else valList[i].toFloat()
                entries.add(Entry(i.toFloat(), entryVal))
            }
            binding.lineChart.axisLeft.setDrawGridLines(false)
            binding.lineChart.xAxis.setDrawGridLines(false)
            binding.lineChart.axisRight.setDrawGridLines(false)
            binding.lineChart.axisRight.setDrawAxisLine(false)
            val valueFormatter2 = IndexAxisValueFormatter()
            val xAxis2 = keysList?.toTypedArray()
            valueFormatter2.values = xAxis2
            binding.lineChart.xAxis.valueFormatter = valueFormatter2
            binding.lineChart.xAxis.valueFormatter = valueFormatter2
            binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            val line: MutableList<ILineDataSet> = ArrayList()
            val lDataSet = LineDataSet(entries, getGraphDataSetTitle(paramType,duration))
            lDataSet.color = resources.getColor(com.example.mandiprice.R.color.WoodBrown)
            lDataSet.setCircleColor(Color.WHITE)
            lDataSet.lineWidth = 4f
            lDataSet.setDrawValues(false)
            if (paramType.equals(
                    "leaf_wetness_hrs",
                    ignoreCase = true
                ) && duration == GraphSelection.LAST12HRS
            ) {
                lDataSet.mode = LineDataSet.Mode.LINEAR
                val yAxisVals = ArrayList(Arrays.asList("Dry", "Wet"))
                binding.lineChart.axisLeft.valueFormatter = IndexAxisValueFormatter(yAxisVals)
                binding.lineChart.axisLeft.labelCount = 2
                binding.lineChart.axisLeft.axisMaximum = 1f
            } else if (paramType.equals("leaf_wetness_hrs", ignoreCase = true)) {
                binding.lineChart.axisLeft.valueFormatter = DefaultValueFormatter(1)
                binding.lineChart.axisLeft.resetAxisMaximum()
                lDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            } else lDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lDataSet.setDrawFilled(true)
            lDataSet.circleHoleColor =
                resources.getColor(com.example.mandiprice.R.color.DarkGreen)
            lDataSet.circleRadius = 6f
            lDataSet.fillDrawable =
                resources.getDrawable(com.example.mandiprice.R.drawable.bg_graph)

            lDataSet.fillAlpha = 30
            line.add(lDataSet)
            binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
            binding.lineChart.xAxis.setDrawGridLines(true)
            binding.lineChart.axisRight.setDrawLabels(false)
            binding.lineChart.xAxis.labelRotationAngle = -45f
            binding.lineChart.axisLeft.axisMinimum = 0f
            binding.lineChart.axisLeft.spaceTop = 150f
            if (paramType.equals("humidity", ignoreCase = true)) {
                binding.lineChart.axisLeft.axisMaximum = 100f
            }

//            binding.lineChart.xAxis.setLabelCount(keysList!!.size, false)

            binding.lineChart.data = LineData(line)
            binding.lineChart.setTouchEnabled(true)
            val mv2: IMarker = CustomMarkerView(
                requireContext(),
                R.layout.viewholder_marker_custom,
                getUnits(paramType),
                keysList,
                paramType,
                duration
            )
            binding.lineChart.marker = mv2
            binding.lineChart.setDrawBorders(true)
            binding.lineChart.setBorderColor(com.example.mandiprice.R.color.LightGray)
            binding.lineChart.setBorderWidth(2f)
            binding.lineChart.xAxis.granularity = 1f
            binding.lineChart.isHighlightPerTapEnabled = true
            binding.lineChart.isScaleYEnabled = false
            binding.lineChart.description = null
            binding.lineChart.invalidate()
        }
    }

    private fun getKeyList(duration: GraphSelection): List<String>? {
        return when (duration) {
            GraphSelection.LAST12HRS -> graphsData?.last12HrsData?.keys?.toList()
            GraphSelection.LAST7DAYS -> {
                val totalList = graphsData?.last30DaysData?.keys?.toList()
                if (!totalList.isNullOrEmpty()) {
                    if (totalList.size > LAST_DAYS) {
                        totalList.subList(totalList.size - LAST_DAYS, totalList.size)
                    } else {
                        totalList
                    }
                } else emptyList()
            }
            GraphSelection.LAST30DAYS -> graphsData?.last30DaysData?.keys?.toList()
        }
    }

    private fun getValueList(duration: GraphSelection): List<Double>? {
        return when (duration) {
            GraphSelection.LAST12HRS -> graphsData?.last12HrsData?.values?.toList()
            GraphSelection.LAST30DAYS -> graphsData?.last30DaysData?.values?.toList()
            GraphSelection.LAST7DAYS -> {
                val totalList = graphsData?.last30DaysData?.values?.toList()
                if (!totalList.isNullOrEmpty()) {
                    if (totalList.size > LAST_DAYS) {
                        totalList.subList(totalList.size - LAST_DAYS, totalList.size)
                    } else {
                        totalList
                    }
                } else emptyList()
            }
        }
    }

    private fun getGraphDataSetTitle(paramType: String,duration: GraphSelection): String {
        return when (paramType) {
            "temperature", "soil_temperature_1" -> "Temperature in °C"
            "rainfall" -> "Rainfall in mm"
            "humidity" -> "Humidity in %"
            "windspeed" -> "Windspeed in Kmph"
            "leaf_wetness_hrs" -> {
                if(duration == GraphSelection.LAST12HRS)
                    "Leaf Wetness - Dry/Wet"
                else "Leaf Wetness in Hrs"
            }
            "pressure" -> "Pressure in kpa"
            "soil_moisture_1_kpa", "soil_moisture_2_kpa" -> "Soil Moisture in kpa"
            "lux" -> "Lux in lux"
            else -> " "
        }
    }


    private fun initClicks() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun tabs() {
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Today")
                .setCustomView(com.example.mandiprice.R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("7 Days")
                .setCustomView(com.example.mandiprice.R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("1 Month")
                .setCustomView(com.example.mandiprice.R.layout.item_tab)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> {
                        when (paramType) {
                            "temperature" -> { EventClickHandling.calculateClickEvent("Temparature_card_last12hrs") }
                            "rainfall" -> { EventClickHandling.calculateClickEvent("rainfall_card_last12hrs") }
                            "humidity" -> { EventClickHandling.calculateClickEvent("humidity_card_last12hrs") }
                            "windspeed" -> { EventClickHandling.calculateClickEvent("windspeed_card_last12hrs") }
                            "leaf_wetness_hrs" -> { EventClickHandling.calculateClickEvent("leaf_wetness_card_last12hrs") }
                            "pressure" -> { EventClickHandling.calculateClickEvent("pressure_card_last12hrs") }
                            "soil_moisture_1_kpa" -> { EventClickHandling.calculateClickEvent("soil_moisture_top_card_last12hrs") }
                            "soil_moisture_2_kpa" -> { EventClickHandling.calculateClickEvent("soil_moisture_bottom_card_last12hrs") }
                            "lux" -> { EventClickHandling.calculateClickEvent("lux_card_last12hrs") }
                            "soil_temperature_1" -> { EventClickHandling.calculateClickEvent("soil_temperature_card_last12hrs") }

                        }
                        populateGraph(paramType, GraphSelection.LAST12HRS)
                    }
                    1 -> {
                        when (paramType) {
                            "temperature" -> { EventClickHandling.calculateClickEvent("Temparature_card_last7days") }
                            "rainfall" -> { EventClickHandling.calculateClickEvent("Rainfall_card_last7days") }
                            "humidity" -> { EventClickHandling.calculateClickEvent("humidity_card_last7days") }
                            "windspeed" -> { EventClickHandling.calculateClickEvent("windspeed_card_last7days")}
                            "leaf_wetness_hrs" -> { EventClickHandling.calculateClickEvent("leaf_wetness_card_last7days")}
                            "pressure" -> { EventClickHandling.calculateClickEvent("pressure_card_last7days")}
                            "soil_moisture_1_kpa" -> { EventClickHandling.calculateClickEvent("soil_moisture_top_card_last7days")}
                            "soil_moisture_2_kpa" -> { EventClickHandling.calculateClickEvent("soil_moisture_bottom_last7days")}
                            "lux" -> { EventClickHandling.calculateClickEvent("lux_last7days")}
                            "soil_temperature_1" -> { EventClickHandling.calculateClickEvent("soil_temperature_card_last7days") }

                        }
                        populateGraph(paramType, GraphSelection.LAST7DAYS)
                    }
                    2 -> {
                        when (paramType) {
                            "temperature" -> { EventClickHandling.calculateClickEvent("Temparature_card_last1month") }
                            "rainfall" -> { EventClickHandling.calculateClickEvent("Rainfall_card_last1month") }
                            "humidity" -> { EventClickHandling.calculateClickEvent("humidity_card_last1month") }
                            "windspeed" -> { EventClickHandling.calculateClickEvent("windspeed_card_last1month")}
                            "leaf_wetness_hrs" -> { EventClickHandling.calculateClickEvent("leaf_wetness_card_last1month")}
                            "pressure" -> { EventClickHandling.calculateClickEvent("pressure_card_last1month")}
                            "soil_moisture_1_kpa" -> { EventClickHandling.calculateClickEvent("soil_moisture_top_card_last1month")}
                            "soil_moisture_2_kpa" -> { EventClickHandling.calculateClickEvent("soil_moisture_bottom_card_last1month")}
                            "lux" -> { EventClickHandling.calculateClickEvent("lux_card_last1month")}
                            "soil_temperature_1" -> { EventClickHandling.calculateClickEvent("soil_temperature_card_last1month") }

                        }
                        populateGraph(paramType, GraphSelection.LAST30DAYS)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun getUnits(paramType: String): String? {
        return when (paramType) {
            "temperature", "soil_temperature_1" -> " °C"
            "rainfall" -> " mm"
            "humidity" -> " %"
            "windspeed" -> " Kmph"
            "pressure", "soil_moisture_1_kpa", "soil_moisture_2_kpa" -> " KPa"
            "lux" -> " lux"
            "leaf_wetness_hrs" -> " Hrs"
            else -> " "
        }
    }

    private fun setTranslation() {
        TranslationsManager().loadString("str_today",binding.today)
//         TranslationsManager().loadString("view_tepm", binding.tvToolbar,"Temprature")
//        TranslationsManager().loadString("view_rainfall", binding.tvToolbar,"Rainfall")
//        TranslationsManager().loadString("str_humidity", binding.tvToolbar,"Humidity")
//        TranslationsManager().loadString("str_wind_speed", binding.tvToolbar,"Wind Speed")
//        TranslationsManager().loadString("view_leaf", binding.tvToolbar,"Leaf wetness")
//        TranslationsManager().loadString("view_pressure", binding.tvToolbar,"Pressure")
//        TranslationsManager().loadString("view_light", binding.tvToolbar,"Light Intensity")
//        TranslationsManager().loadString("soil_moisture", binding.tvToolbar,"Soil Moisture")
//        TranslationsManager().loadString("view_top", binding.tvToolbar,"Top")
//        TranslationsManager().loadString("view_bottom", binding.tvToolbar,"Bottom")
//        TranslationsManager().loadString("view_soil_temp", binding.tvToolbar,"Soil Temperature")
    }

    private fun getParamNote(
        paramType: String,
        valList: List<Double>,
        duration: GraphSelection
    ): String? {
        return when (paramType) {
            "temperature", "soil_temperature_1" -> "Avg Temperature: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUnits(paramType)
            "rainfall" -> "Total Rainfall: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateSum(valList)
            ) + getUnits(paramType)
            "humidity" -> "Avg Humidity: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUnits(paramType)
            "windspeed" -> "Avg Windspeed: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUnits(paramType)
            "leaf_wetness_hrs" -> {
                if (duration == GraphSelection.LAST12HRS) "Total Leaf Wetness: " + String.format(
                    Locale.ENGLISH, "%.2f", calculateSum(valList)
                ) + getUnits(paramType) else "Avg Leaf Wetness: " + String.format(
                    Locale.ENGLISH, "%.2f", calculateAvg(valList)
                ) + getUnits(paramType)
            }
            "soil_moisture_1_kpa", "soil_moisture_2_kpa" -> "Avg Soil Moisture: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUnits(paramType)
            "lux" -> "Avg Lux: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUnits(paramType)
            "pressure" -> "Avg Pressure: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUnits(paramType)
//            "leaf_wetness_hrs" -> "Avg Leaf: " + String.format(
//                Locale.ENGLISH,
//                "kmph ",
//                calculateAvg(valList)
//            ) + getUnits(paramType)
            else -> ""
        }
    }

    private fun calculateAvg(valList: List<Double>): Double? {
        var sum = 0.0
        if (!valList.isEmpty()) {
            for (mark in valList) {
                sum += mark
            }
            return sum / valList.size
        }
        return sum
    }

    private fun calculateSum(valList: List<Double>): Double? {
        var sum = 0.0
        if (!valList.isEmpty()) {
            for (mark in valList) {
                sum += mark
            }
            return sum
        }
        return sum
    }


    private fun graphApiData(
        serialNo: Int?,
        deviceModelId: Int?,
        value: String?
    ) {
        viewDevice.viewModelScope.launch {
            viewDevice.getGraphsViewDevice(serialNo, deviceModelId, value)
                .observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data?.data != null) {
                                graphsData = it.data?.data
                                populateGraph(paramType, GraphSelection.LAST12HRS)
                            }
                        }
                        is Resource.Error -> {
                            AppUtils.translatedToastServerErrorOccurred(context)

                        }
                        is Resource.Loading -> {

                        }
                    }

                }

        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("GraphsFragment")
    }
    fun translationToolBar(toolBarTranslation:String){
        when (toolBarTranslation) {
            "Temperature" -> {
                TranslationsManager().loadString("view_tepm", binding.tvToolbar,"Temperature")
            }
            "Rainfall" -> {
                TranslationsManager().loadString("view_rainfall", binding.tvToolbar,"Rainfall")
            }
            "Humidity" -> {
                TranslationsManager().loadString("str_humidity", binding.tvToolbar,"Humidity")

            }
            "Wind Speed" -> {
                TranslationsManager().loadString("str_wind_speed", binding.tvToolbar,"Wind Speed")

            }
            "Leaf wetness" -> {
                TranslationsManager().loadString("view_leaf", binding.tvToolbar,"Leaf wetness")

            }
            "Pressure" -> {
                TranslationsManager().loadString("view_pressure", binding.tvToolbar,"Pressure")

            }
            "Soil Moisture Top" -> {
                TranslationsManager().loadString("soil_moisture_top", binding.tvToolbar,"Soil Moisture Top")

            }
            "Soil Moisture Bottom" -> {
                TranslationsManager().loadString("soil_moisture_bottom", binding.tvToolbar,"Soil Moisture Bottom")
            }
            "Light Intensity" -> {
                TranslationsManager().loadString("view_light", binding.tvToolbar,"Light Intensity")

            }
            "Soil Temperature" -> {
                TranslationsManager().loadString("soil_temperature", binding.tvToolbar,"Soil Temperature")

            }
        }
    }

}
