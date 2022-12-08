package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemWeeklyIrrgationBinding
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.data.Network.NetworkModels.IrrigationForecast
import com.waycool.data.Network.NetworkModels.IrrigationPerDay

class WeeklyAdapter: ListAdapter<IrrigationForecast,WeeklyAdapter.MyViewHolder>(DiffCallback) {
    var index:Int = 0
    class MyViewHolder(private val binding: ItemWeeklyIrrgationBinding): RecyclerView.ViewHolder(binding.root) {
        val x = binding.tvTime
        val ll = binding.llHourly

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemWeeklyIrrgationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeeklyAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener() {
            index = position
        }
        val properties = getItem(position)
        holder.x.text = properties.days[position]


        if(index == position) {
            holder.ll.setBackgroundResource(R.drawable.green_border_irrigation)
            holder.x.setTextColor(Color.parseColor("#146133"))
        }
        else{
            holder.ll.setBackgroundResource(R.drawable.border_irrigation)
            holder.x.setTextColor(Color.parseColor("#000000"))
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<IrrigationForecast>() {

        override fun areItemsTheSame(
            oldItem: IrrigationForecast,
            newItem: IrrigationForecast
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: IrrigationForecast,
            newItem: IrrigationForecast
        ): Boolean {
            return oldItem.days == newItem.days
        }
}}

