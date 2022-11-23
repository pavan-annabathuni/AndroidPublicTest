package com.example.irrigationplanner

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.DiseaseAdapter
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.example.irrigationplanner.adapter.WeeklyAdapter
import com.example.irrigationplanner.databinding.FragmentIrrigationBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout

class IrrigationFragment : Fragment() {
     private lateinit var binding: FragmentIrrigationBinding
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
        binding = FragmentIrrigationBinding.inflate(inflater)
        setAdapter()

        binding.tvCropInfo.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.actionIrrigationFragmentToCropOverviewFragment())
        }
        tabs()
        dialog()
        onClick()

        return binding.root
    }

    private fun onClick() {
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
            this.findNavController().navigate(IrrigationFragmentDirections.
            actionIrrigationFragmentToIrrigationHistoryFragment())
        }
        binding.btDisease.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.actionIrrigationFragmentToDiseaseHistoryFragment())
        }

        binding.btForecast.setOnClickListener(){
            this.findNavController().navigate(IrrigationFragmentDirections.
            actionIrrigationFragmentToForecastFragment())
        }
    }

    private fun setAdapter(){
        binding.recycleViewDis.adapter = WeeklyAdapter()
        binding.recycleViewHis.adapter = HistoryAdapter(HistoryAdapter.OnClickListener {

        })
        binding.rvDis.adapter = DiseaseAdapter()
    }

    private fun tabs() {

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Disease").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Pest").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Deficiency").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
    })
    }

    private fun dialog(){
        binding.irrigationYes.setOnClickListener(){
            binding.dailyIrrigation.visibility = View.GONE
            binding.perDay.visibility = View.VISIBLE
            val dialog = BottomSheetDialog(this.requireContext(),R.style.BottomSheetDialog)
            dialog.setContentView(R.layout.irrigation_pre_day)
           val close = dialog.findViewById<ImageView>(R.id.close)
            close!!.setOnClickListener(){
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btExit.setOnClickListener(){
            val dialog = Dialog(requireContext())

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_delete)
            // val body = dialog.findViewById(R.id.body) as TextView
            val yesBtn = dialog.findViewById(R.id.cancel) as Button
            val noBtn = dialog.findViewById(R.id.delete) as Button
            yesBtn.setOnClickListener {
                dialog.dismiss()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

    }



}