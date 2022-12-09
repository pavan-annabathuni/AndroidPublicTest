package com.waycool.iwap.premium

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentGraphsBinding
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class GraphsFragment : Fragment() {
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
//    lateinit var listLine: ArrayList<Entry>
//    lateinit var lineDataSet: LineDataSet
//    lateinit var lineData: LineData
//    private val inputDateFormatter: SimpleDateFormat =
//        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
//    private val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
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


}