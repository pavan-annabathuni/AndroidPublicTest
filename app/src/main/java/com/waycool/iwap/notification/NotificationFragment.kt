package com.waycool.iwap.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {
   private lateinit var binding:FragmentNotificationBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private lateinit var mNotificationAdapter: NotificationAdapter
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
        binding = FragmentNotificationBinding.inflate(inflater)
        setAdapter()
        binding.topAppBar.setOnClickListener(){
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun setAdapter(){
        mNotificationAdapter = NotificationAdapter()
        binding.recycleViewHis.adapter = mNotificationAdapter
        viewModel.getNotification().observe(viewLifecycleOwner){
            mNotificationAdapter.submitList(it.data?.data)
            Toast.makeText(context, "${it.data?.data}", Toast.LENGTH_SHORT).show()
        }
    }

}