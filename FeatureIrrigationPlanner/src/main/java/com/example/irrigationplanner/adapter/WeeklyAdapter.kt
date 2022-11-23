package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemWeeklyIrrgationBinding

class WeeklyAdapter: RecyclerView.Adapter<WeeklyAdapter.MyViewHolder>() {
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


        if(index == position) {
            holder.ll.setBackgroundResource(R.drawable.green_border_irrigation)
            holder.x.setTextColor(Color.parseColor("#146133"))
        }
        else{
            holder.ll.setBackgroundResource(R.drawable.border_irrigation)
            holder.x.setTextColor(Color.parseColor("#000000"))
        }
    }


    override fun getItemCount(): Int {
        return 8
    }
}
