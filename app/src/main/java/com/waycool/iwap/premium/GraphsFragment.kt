package com.waycool.iwap.premium

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.soiltesting.utils.Constant
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.utils.Resource
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentGraphsBinding
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import com.waycool.iwap.utils.Constant.TAG
import kotlinx.coroutines.launch
import java.text.ParseException
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val serial_no = arguments?.getInt("serial_no")
            val device_model_id = arguments?.getInt("device_model_id")
            val value = arguments?.getString("value")
            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
            initObserveGraphs(serial_no,device_model_id,value)
        }
        tabs()

    }
    private fun tabs() {

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Today").setCustomView(com.example.mandiprice.R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Last 7 Days").setCustomView(com.example.mandiprice.R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("1 Month").setCustomView(com.example.mandiprice.R.layout.item_tab)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(binding.tabLayout.selectedTabPosition) {
                    0->{
                        if (arguments != null) {
                            val serial_no = arguments?.getInt("serial_no")
                            val device_model_id = arguments?.getInt("device_model_id")
                            val value = arguments?.getString("value")
                            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
                            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
                            initObserveGraphs(serial_no,device_model_id,value)
//                            initObserveGraphs(serial_no,device_model_id,value)
                        }
                    }
                    1->{

                    }
                    2->{

                    }

                }
                }
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

                }
        })
    }
















    private fun initObserveGraphs(serialNo: Int?, deviceModelId: Int?,value:String?) {
        viewDevice.getGraphsViewDevice(serialNo,deviceModelId,value).observe(viewLifecycleOwner) { it ->
             listLine = ArrayList()
                if (it.data?.data != null) {
                    for (i in it.data?.data!!.LastTodayData?.dataTimestamp!!.indices) {
//                val inputDate:SimpleDateFormat = SimpleDateFormat(it.data!!.data[0].arrivalDate)
//                val outputDate:SimpleDateFormat = SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ")
//                val date:Date = inputDate.parse(it.data!!.data[0].arrivalDate)
//                val formateDate = outputDate.format(date)
//                Log.d("DATE", "graph: $formateDate ")
                        val xAxis: XAxis = binding.lineChart.getXAxis()
                        listLine.add(
                            Entry(
                                i.toFloat(),it.data!!.data?.LastTodayData?.dataTimestamp  .toString().toFloat()
                            )
                        )
                    }
                }
//        listLine.add(Entry(20f,13f))
//        listLine.add(Entry(30f,11f))
//        listLine.add(Entry(40f,13f))
//        listLine.add(Entry(60f,12f))

                lineDataSet = LineDataSet(listLine, "")

                val datesList = it.data?.data?.LastTodayData?.dataTimestamp?. map { date ->
                    try {
                        val date: Date = inputDateFormatter.parse(it.data?.data?.LastTodayData?.dataTimestamp.toString())
                        outputDateFormatter.format(date)
                    } catch (e: ParseException) {
                        Log.d("Mandi Graphs",e.message.toString())
                        ""
                    }
                }
                val valueFormatter2 = IndexAxisValueFormatter()

                var xAxis2 = datesList?.toTypedArray()
                valueFormatter2.values = xAxis2
                binding.lineChart.xAxis.valueFormatter = valueFormatter2
                binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                binding.lineChart.xAxis.labelRotationAngle = -45f

                binding.lineChart.axisLeft.axisMinimum = 0f

                lineData = LineData(lineDataSet)
                lineDataSet.color = resources.getColor(com.example.mandiprice.R.color.WoodBrown)
                binding.lineChart.data = lineData
                lineDataSet.setCircleColor(Color.WHITE)
                lineDataSet.circleHoleColor = resources.getColor(com.example.mandiprice.R.color.DarkGreen)
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
                lineDataSet.fillDrawable = resources.getDrawable(com.example.mandiprice.R.drawable.bg_graph)
                binding.lineChart.xAxis.spaceMax = 1f
                binding.lineChart.fitScreen()
                // binding.lineChart.axisLeft.isEnabled = false;
                binding.lineChart.isScaleXEnabled = false
            }

//            when (it) {
//                    is Resource.Success -> {
//                        Log.d(TAG, "initObserveGraphs: ${it.data?.data}")
//                    }
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), it.data?.message.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                    is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
//
//                    }
//                }

        }
    }


//
//    private fun graph() {
//        viewModel.viewModelScope.launch {
//            viewModel.getMandiHistoryDetails().observe(viewLifecycleOwner) { it ->
//
//
//                listLine = ArrayList()
//                if (it.data?.data != null) {
//                    for (i in it.data?.data!!.indices) {
////                val inputDate:SimpleDateFormat = SimpleDateFormat(it.data!!.data[0].arrivalDate)
////                val outputDate:SimpleDateFormat = SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ")
////                val date:Date = inputDate.parse(it.data!!.data[0].arrivalDate)
////                val formateDate = outputDate.format(date)
////                Log.d("DATE", "graph: $formateDate ")
//                        val xAxis: XAxis = binding.lineChart.getXAxis()
//                        listLine.add(
//                            Entry(
//                                i.toFloat(),it.data!!.data[i].avgPrice!!.toFloat()
//                            )
//                        )
//                    }
//                }
////        listLine.add(Entry(20f,13f))
////        listLine.add(Entry(30f,11f))
////        listLine.add(Entry(40f,13f))
////        listLine.add(Entry(60f,12f))
//
//                lineDataSet = LineDataSet(listLine, "")
//
//                val datesList = it.data?.data?.map { mandi ->
//                    try {
//                        val date: Date = inputDateFormatter.parse(mandi.arrivalDate)
//                        outputDateFormatter.format(date)
//                    } catch (e: ParseException) {
//                        Log.d("Mandi Graphs",e.message.toString())
//                        ""
//                    }
//                }
//                val valueFormatter2 = IndexAxisValueFormatter()
//
//                var xAxis2 = datesList?.toTypedArray()
//                valueFormatter2.values = xAxis2
//                binding.lineChart.xAxis.valueFormatter = valueFormatter2
//                binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
////                binding.lineChart.xAxis.labelRotationAngle = -45f
//
//                binding.lineChart.axisLeft.axisMinimum = 0f
//
//                lineData = LineData(lineDataSet)
//                lineDataSet.color = resources.getColor(com.example.mandiprice.R.color.WoodBrown)
//                binding.lineChart.data = lineData
//                lineDataSet.setCircleColor(Color.WHITE)
//                lineDataSet.circleHoleColor = resources.getColor(com.example.mandiprice.R.color.DarkGreen)
//                lineDataSet.circleRadius = 6f
//                lineDataSet.mode = LineDataSet.Mode.LINEAR
//
//
//                lineDataSet.setDrawFilled(true)
//                binding.lineChart.setDrawGridBackground(false)
//                binding.lineChart.setDrawBorders(true)
//                binding.lineChart.setBorderColor(com.example.mandiprice.R.color.LightGray)
//                binding.lineChart.setBorderWidth(1f)
//                binding.lineChart.axisRight.setDrawGridLines(false)
//                binding.lineChart.axisLeft.setDrawGridLines(false)
//                //binding.lineChart.xAxis.setDrawGridLines(false)
//                binding.lineChart.description.isEnabled = false
//                binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                binding.lineChart.axisRight.isEnabled = false
//                lineDataSet.fillDrawable = resources.getDrawable(com.example.mandiprice.R.drawable.bg_graph)
//                binding.lineChart.xAxis.spaceMax = 1f
//                binding.lineChart.fitScreen()
//                // binding.lineChart.axisLeft.isEnabled = false;
//                binding.lineChart.isScaleXEnabled = false
//            }
//        }
//
//    }
//    }