package com.waycool.iwap.premium

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.soiltesting.utils.Constant
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentDeviceOneBinding
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import java.text.SimpleDateFormat
import java.util.*


class DeviceFragmentOne : Fragment() {
    private var _binding: FragmentDeviceOneBinding? = null
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
        _binding = FragmentDeviceOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val serial_no = arguments?.getInt("serial_no")
            val device_model_id = arguments?.getInt("device_model_id")
            val value = arguments?.getString("value")
            val data_degree=arguments?.getString("temp_value")
            val update_date=arguments?.getString("date_time")
            Log.d(Constant.TAG, "onCreateViewONPID:$serial_no ")
            Log.d(Constant.TAG, "onCreateViewONPID:$device_model_id ")
            Log.d(Constant.TAG, "onCreateViewONPID:$value ")
            Log.d(Constant.TAG, "onCreateViewONPID:$data_degree")
            Log.d(Constant.TAG, "onCreateViewONPID:$update_date ")

//            graphApiData(serial_no, device_model_id, value)
//            binding.today.text=

//            binding.tvToolbar.
//            graphApiData(serial_no, device_model_id, value)

        }




    }

}