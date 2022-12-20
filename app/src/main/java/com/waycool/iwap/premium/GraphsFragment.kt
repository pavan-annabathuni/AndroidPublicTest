package com.waycool.iwap.premium

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.utils.Constant
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.waycool.data.utils.Resource

import com.waycool.iwap.databinding.FragmentGraphsBinding
import com.waycool.iwap.utils.Constant.TAG
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class GraphsFragment : Fragment() {
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    lateinit var listLine: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData
    private val inputDateFormatter: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    private val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }
    private enum class GraphSelection {
        LAST12HRS, LAST7DAYS, LAST30DAYS
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val serial_no = arguments?.getInt("serial_no")
            val device_model_id = arguments?.getInt("device_model_id")
            val value = arguments?.getString("value")
            val data_degree = arguments?.getString("temp_value")
            val update_date = arguments?.getString("date_time")
            val toolbar=arguments?.getString("toolbar")
            binding.tvToolbar.text=toolbar
            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
            Log.d(Constant.TAG, "onCreateViewONPID:$data_degree")
            Log.d(Constant.TAG, "onCreateViewONPID:$update_date ")
            binding.degree.text = data_degree
            binding.date.text = update_date
            graphApiData(serial_no, device_model_id, value,"one_day")
//            binding.today.text=

//            binding.tvToolbar.
//            graphApiData(serial_no, device_model_id, value)

        }
        initClicks()

        tabs()

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
                        if (arguments != null) {
                            val serial_no = arguments?.getInt("serial_no")
                            val device_model_id = arguments?.getInt("device_model_id")
                            val value = arguments?.getString("value")
                            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
                            graphApiData(serial_no, device_model_id, value,"one_day")
                        }
                    }
                    1 -> {
                        if (arguments != null) {
                            val serial_no = arguments?.getInt("serial_no")
                            val device_model_id = arguments?.getInt("device_model_id")
                            val value = arguments?.getString("value")
                            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
                            graphApiData(serial_no, device_model_id, value,"seven_days")

                        }


                    }
                    2 -> {
                        if (arguments != null) {
                            val serial_no = arguments?.getInt("serial_no")
                            val device_model_id = arguments?.getInt("device_model_id")
                            val value = arguments?.getString("value")
                            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
                            graphApiData(serial_no, device_model_id, value,"last_month")
//                            }


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

    private fun getUints(paramType: String): String? {
        return when (paramType) {
            "temp" -> " °C"
            "rain" -> " mm"
            "humidity", "leaf", "soil1", "soil2" -> " %"
            "wind" -> " Kmph"
            "lux" -> " lux"
            else -> " "
        }
    }

    private fun getParamNote(paramType: String, valList: List<Double>): String? {
        return when (paramType) {
            "temperature" -> "Avg Temperature: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUints(paramType)
            "rainfall" -> "Total Rainfall: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateSum(valList)
            ) + getUints(paramType)
            "humidity" -> "Avg Humidity: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUints(paramType)
            "windspeed" -> "Avg Windspeed: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUints(paramType)
            "leaf" -> "Avg Leaf Wetness: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUints(paramType)
            "soil1", "soil2" -> "Avg Soil Moisture: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUints(paramType)
            "lux" -> "Avg Lux: " + String.format(
                Locale.ENGLISH,
                "%.2f",
                calculateAvg(valList)
            ) + getUints(paramType)
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

    private fun graphApiData(serialNo: Int?, deviceModelId: Int?, value: String?, timePeriod: String) {
        viewDevice.viewModelScope.launch {
            viewDevice.getGraphsViewDevice(serialNo, deviceModelId, value)
                .observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data?.data != null) {
                                val listone = arrayListOf<Double>()
                                listone.addAll(it.data?.data!!.LastTodayData?.values!!)
//                                val d: Double = it.data?.data!!.LastTodayData?.values.toString().toDouble()
//                                val list: List<Double> = listOf(d)
                                binding.tvDramatic.text = getParamNote(value!!, listone)

                                Log.d(TAG, "dataGraDataPoints: ${it.data?.data}")
                                listLine = ArrayList()
                                if (it.data?.data != null) {
                                    val response= it.data!!.data
                                    if (timePeriod=="one_day"){
                                        for (i in it.data?.data?.LastTodayData?.keys!!.indices) {
                                            val xAxis: XAxis = binding.lineChart.getXAxis()
                                            listLine.add(
                                                Entry(
                                                    i.toFloat(),
                                                    it.data!!.data?.LastTodayData?.values!!.toList()[i].toFloat()
                                                )
                                            )
                                        }
                                        lineDataSet = LineDataSet(listLine, "")

                                        val datesList = it.data?.data?.LastTodayData?.keys
                                        val valueFormatter2 = IndexAxisValueFormatter()

                                        var xAxis2 = datesList?.toTypedArray()
                                        valueFormatter2.values = xAxis2
                                        binding.lineChart.xAxis.valueFormatter = valueFormatter2
                                        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        binding.lineChart.axisLeft.axisMinimum = 0f
                                        binding.lineChart.fitScreen()
                                        binding.lineChart.setScaleEnabled(false)

                                        lineData = LineData(lineDataSet)
                                        lineDataSet.color =
                                            resources.getColor(com.example.mandiprice.R.color.WoodBrown)
                                        binding.lineChart.data = lineData
                                        lineDataSet.setCircleColor(Color.WHITE)
                                        lineDataSet.circleHoleColor =
                                            resources.getColor(com.example.mandiprice.R.color.DarkGreen)
                                        lineDataSet.circleRadius = 6f
                                        lineDataSet.mode = LineDataSet.Mode.LINEAR
                                        lineDataSet.setDrawFilled(true)
                                        binding.lineChart.setDrawGridBackground(false)
                                        binding.lineChart.setDrawBorders(true)
                                        binding.lineChart.setBorderColor(com.example.mandiprice.R.color.LightGray)
                                        binding.lineChart.setBorderWidth(1f)
                                        binding.lineChart.axisRight.setDrawGridLines(false)
                                        binding.lineChart.axisLeft.setDrawGridLines(false)
                                        //binding.lineChart.xAxis.setDrawGridLines(false)
                                        binding.lineChart.description.isEnabled = false
                                        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        binding.lineChart.axisRight.isEnabled = false
                                        binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
                                        lineDataSet.fillDrawable =
                                            resources.getDrawable(com.example.mandiprice.R.drawable.bg_graph)
//                                        binding.lineChart.xAxis.spaceMax = 1f
                                        binding.lineChart.fitScreen()
                                        // binding.lineChart.axisLeft.isEnabled = false;
                                        binding.lineChart.isScaleXEnabled = false


                                    }
                                    else if (timePeriod=="seven_days"){
                                        for (i in it.data?.data?.sevenDaysData?.keys!!.indices) {
                                            val xAxis: XAxis = binding.lineChart.getXAxis()
                                            listLine.add(
                                                Entry(
                                                    i.toFloat(),
                                                    it.data!!.data?.sevenDaysData?.values!!.toList()[i].toFloat()
                                                )
                                            )
                                        }
                                        lineDataSet = LineDataSet(listLine, "")

                                        val datesList = it.data?.data?.sevenDaysData?.keys
                                        val valueFormatter2 = IndexAxisValueFormatter()

                                        var xAxis2 = datesList?.toTypedArray()
                                        valueFormatter2.values = xAxis2
                                        binding.lineChart.xAxis.valueFormatter = valueFormatter2
                                        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        binding.lineChart.axisLeft.axisMinimum = 0f

                                        lineData = LineData(lineDataSet)
                                        lineDataSet.color =
                                            resources.getColor(com.example.mandiprice.R.color.WoodBrown)
                                        binding.lineChart.data = lineData
                                        lineDataSet.setCircleColor(Color.WHITE)
                                        lineDataSet.circleHoleColor =
                                            resources.getColor(com.example.mandiprice.R.color.DarkGreen)
                                        lineDataSet.circleRadius = 6f
                                        binding.lineChart.fitScreen()
                                        binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
                                        binding.lineChart.setScaleEnabled(false)
                                        lineDataSet.mode = LineDataSet.Mode.LINEAR
                                        lineDataSet.setDrawFilled(true)
                                        binding.lineChart.setDrawGridBackground(false)
                                        binding.lineChart.setDrawBorders(true)
                                        binding.lineChart.setBorderColor(com.example.mandiprice.R.color.LightGray)
                                        binding.lineChart.setBorderWidth(1f)
                                        binding.lineChart.axisRight.setDrawGridLines(false)
                                        binding.lineChart.axisLeft.setDrawGridLines(false)
//                                        binding.lineChart.fitScreen()
//                                        binding.lineChart.setScaleEnabled(false)
                                        //binding.lineChart.xAxis.setDrawGridLines(false)
                                        binding.lineChart.description.isEnabled = false
                                        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        binding.lineChart.axisRight.isEnabled = false
                                        lineDataSet.fillDrawable =
                                            resources.getDrawable(com.example.mandiprice.R.drawable.bg_graph)
                                        binding.lineChart.xAxis.spaceMax = 1f
                                        binding.lineChart.fitScreen()
                                        // binding.lineChart.axisLeft.isEnabled = false;
                                        binding.lineChart.isScaleXEnabled = false
                                    }
                                    else if (timePeriod=="last_month"){
                                        for (i in it.data?.data?.MonthDaysData ?.keys!!.indices) {
                                            val xAxis: XAxis = binding.lineChart.getXAxis()
                                            listLine.add(
                                                Entry(
                                                    i.toFloat(),
                                                    it.data!!.data?.MonthDaysData?.values!!.toList()[i].toFloat()
                                                )
                                            )
                                        }
                                        lineDataSet = LineDataSet(listLine, "")

                                        val datesList = it.data?.data?.MonthDaysData?.keys

                                        val valueFormatter2 = IndexAxisValueFormatter()

                                        var xAxis2 = datesList?.toTypedArray()

                                        valueFormatter2.values = xAxis2
                                        binding.lineChart.xAxis.valueFormatter = valueFormatter2
                                        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        binding.lineChart.axisLeft.axisMinimum = 0f

                                        lineData = LineData(lineDataSet)
                                        lineDataSet.color =
                                            resources.getColor(com.example.mandiprice.R.color.WoodBrown)
                                        binding.lineChart.data = lineData
                                        lineDataSet.setCircleColor(Color.WHITE)
                                        lineDataSet.circleHoleColor =
                                            resources.getColor(com.example.mandiprice.R.color.DarkGreen)
                                        lineDataSet.circleRadius = 6f
                                        lineDataSet.mode = LineDataSet.Mode.LINEAR
                                        lineDataSet.setDrawFilled(true)
//                                        binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
                                        binding.lineChart.fitScreen()
                                        binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
                                        binding.lineChart.setScaleEnabled(false)
                                        binding.lineChart.setDrawGridBackground(false)
                                        binding.lineChart.setDrawBorders(true)
                                        binding.lineChart.setBorderColor(com.example.mandiprice.R.color.LightGray)
                                        binding.lineChart.setBorderWidth(1f)
                                        binding.lineChart.axisRight.setDrawGridLines(false)
                                        binding.lineChart.axisLeft.setDrawGridLines(false)
                                        //binding.lineChart.xAxis.setDrawGridLines(false)
                                        binding.lineChart.description.isEnabled = false
                                        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        binding.lineChart.axisRight.isEnabled = false
                                        lineDataSet.fillDrawable =
                                            resources.getDrawable(com.example.mandiprice.R.drawable.bg_graph)
                                        binding.lineChart.xAxis.spaceMax = 1f
                                        binding.lineChart.fitScreen()
                                        // binding.lineChart.axisLeft.isEnabled = false;
                                        binding.lineChart.isScaleXEnabled = false

                                    }


                                }


                            }

                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                        }
                    }

                }

        }
    }

//
//    private fun graphAccess(datesList:String,keys:String,valueFormatter2:Int) {
//
//
//
//    }

}
