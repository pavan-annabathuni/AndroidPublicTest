package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemHistoryBinding

class HistoryAdapter(val onClickListener:OnClickListener):RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {
    var index:Int = 0
    class MyViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        val x = binding.tvTime
        val ll = binding.llHourly
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener() {
            index = position
            onClickListener.clickListener

        }

            if (index == position) {
                holder.ll.setBackgroundResource(R.drawable.green_border_irrigation)
                holder.x.setTextColor(Color.parseColor("#146133"))
            } else {
                holder.ll.setBackgroundResource(R.drawable.border_irrigation)
                holder.x.setTextColor(Color.parseColor("#000000"))
            }
        }

    override fun getItemCount(): Int {
        return 8
    }

    class OnClickListener(val clickListener: () -> Unit) {
        fun onClick() = clickListener()
    }
}