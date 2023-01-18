package com.waycool.iwap.graphs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.utils.Constant
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
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentGraphsBinding
import com.waycool.iwap.premium.ViewDeviceViewModel
import com.waycool.iwap.utils.CustomMarkerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class GraphsFragment : Fragment() {
    private var updateDate: String? = null
    private var paramValue: String? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@GraphsFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
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
            paramValue = arguments?.getString("temp_value")
            updateDate = arguments?.getString("date_time")
            binding.tvToolbar.text = arguments?.getString("toolbar")

            Log.d(Constant.TAG, "onCreateViewONPID:$serialNo ")
            Log.d(Constant.TAG, "onCreateViewONPID:$deviceModelId ")
            Log.d(Constant.TAG, "onCreateViewONPID:$paramType ")
            Log.d(Constant.TAG, "onCreateViewONPID:$paramValue")
            Log.d(Constant.TAG, "onCreateViewONPID:$updateDate ")
            binding.paramValue.text = "$paramValue${paramType?.let { getUnits(it) }}"
            binding.date.text = updateDate

            populateGraph(paramType, GraphSelection.LAST12HRS)
            graphApiData(serialNo, deviceModelId, paramType)

//            binding.today.text=

//            binding.tvToolbar.
//            graphApiData(serial_no, device_model_id, value)

        }
        initClicks()
        setTranslation()

        tabs()

    }

    private fun populateGraph(paramType: String?, duration: GraphSelection) {
        if (paramType != null && viewDevice != null) {

            val keysList = getKeyList(duration)
            val valList = getValueList(duration)

            if (valList.isNullOrEmpty()) {
                binding.tvParamNote.text = "Loading..."
                binding.paramProgressBar.visibility = View.VISIBLE
                return
            }

            binding.tvParamNote.text = getParamNote(paramType, valList, duration)
            binding.paramProgressBar.visibility = View.GONE

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until valList?.size?.let { keysList?.size?.coerceAtMost(it) }!!) {
                val entryVal: Float = if (valList[i] == null) 0.0F else valList[i].toFloat()
                entries.add(Entry(i.toFloat(), entryVal))
            }
            binding.lineChart.axisLeft.setDrawGridLines(false)
            binding.lineChart.getXAxis().setDrawGridLines(false)
            binding.lineChart.getAxisRight().setDrawGridLines(false)
            binding.lineChart.getAxisRight().setDrawAxisLine(false)
            val valueFormatter2 = IndexAxisValueFormatter()
            val xAxis2 = keysList?.toTypedArray()
            valueFormatter2.values = xAxis2
            binding.lineChart.getXAxis().setValueFormatter(valueFormatter2)
            binding.lineChart.getXAxis().setValueFormatter(valueFormatter2)
            binding.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM)
            val line: MutableList<ILineDataSet> = ArrayList()
            val lDataSet = LineDataSet(entries, getGraphDataSetTitle(paramType))
            lDataSet.color = resources.getColor(com.example.mandiprice.R.color.WoodBrown)
            lDataSet.setCircleColor(Color.WHITE)
            lDataSet.lineWidth = 4f
            lDataSet.setDrawValues(false)
            if (paramType.equals(
                    "leaf_wetness",
                    ignoreCase = true
                ) && duration == GraphSelection.LAST12HRS
            ) {
                lDataSet.mode = LineDataSet.Mode.STEPPED
                val yAxisVals = ArrayList(Arrays.asList("Dry", "Wet"))
                binding.lineChart.getAxisLeft()
                    .setValueFormatter(IndexAxisValueFormatter(yAxisVals))
                binding.lineChart.getAxisLeft().setLabelCount(2)
                binding.lineChart.getAxisLeft().setAxisMaximum(1f)
            } else if (paramType.equals("leaf_wetness", ignoreCase = true)) {
                binding.lineChart.getAxisLeft().setValueFormatter(DefaultValueFormatter(1))
                binding.lineChart.getAxisLeft().resetAxisMaximum()
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
            binding.lineChart.getAxisRight().setDrawLabels(false)
            binding.lineChart.getXAxis().setLabelRotationAngle(-45f)
            binding.lineChart.getAxisLeft().setAxisMinimum(0f)
            if (paramType.equals("humidity", ignoreCase = true)) {
                binding.lineChart.getAxisLeft().setAxisMaximum(100f)
            }

            binding.lineChart.getXAxis().setLabelCount(keysList!!.size, true)
            binding.lineChart.setData(LineData(line))
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
                    if (totalList?.size!! >= 15) {
                        totalList.subList(totalList.size - 16, totalList.size - 1)
                    } else {
                        totalList
                    }
                }else emptyList()
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
                    if (totalList?.size!! >= 15) {
                        totalList.subList(totalList.size - 16, totalList.size - 1)
                    } else {
                        totalList
                    }
                }else emptyList()
            }
        }
    }

    private fun getGraphDataSetTitle(paramType: String): String {
        return when (paramType) {
            "temperature", "soil_temperature_1" -> "Temperature in °C"
            "rainfall" -> "Rainfall in mm"
            "humidity" -> "Humidity in %"
            "windspeed" -> "Windspeed in Kmph"
            "leaf_wetness" -> "Leaf Wetness in %"
            "pressure" -> "Pressure in KPa"
            "soil_moisture_1", "soil_moisture_2" -> "Soil Moisture in KPa"
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
            binding.tabLayout.newTab().setText("Last 7 Days")
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
                        populateGraph(paramType, GraphSelection.LAST12HRS)
                    }
                    1 -> {
                        populateGraph(paramType, GraphSelection.LAST7DAYS)
                    }
                    2 -> {
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
            "pressure", "soil_moisture_1", "soil_moisture_2" -> " KPa"
            "lux" -> " lux"
            "leaf_wetness" -> " Hrs"
            else -> " "
        }
    }
    private fun setTranslation() {
        TranslationsManager().loadString("str_today",binding.today)
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
            "leaf_wetness" -> {
                if (duration == GraphSelection.LAST12HRS) "Total Leaf Wetness: " + String.format(
                    Locale.ENGLISH, "%.2f", calculateSum(valList)
                ) + getUnits(paramType) else "Avg Leaf Wetness: " + String.format(
                    Locale.ENGLISH, "%.2f", calculateAvg(valList)
                ) + getUnits(paramType)
            }
            "soil_moisture_1", "soil_moisture_2" -> "Avg Soil Moisture: " + String.format(
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
                        }
                        is Resource.Loading -> {
                            ToastStateHandling.toastWarning(
                                requireContext(),
                                "Loading",
                                Toast.LENGTH_SHORT
                            )

                        }
                    }

                }

        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("GraphsFragment")
    }

}
