package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.data.repository.domainModels.MandiHistoryDataDomain
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(val onClickListener:OnClickListener):ListAdapter<HistoricData,HistoryAdapter.MyViewHolder>(DiffCallback) {
    var index:Int = 0
    class MyViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        val x = binding.tvTime
        val ll = binding.llHourly
        fun bind(data: HistoricData?) {
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
            index = position
            notifyDataSetChanged()
        }
        val inputDateFormatter: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("MMM dd", Locale.ENGLISH)
        val date: Date = inputDateFormatter.parse(properties.createdAt)

        holder.x.text = outputDateFormatter.format(date)

            if (index == position) {
                holder.ll.setBackgroundResource(R.drawable.green_border_irrigation)
                holder.x.setTextColor(Color.parseColor("#146133"))
            } else {
                holder.ll.setBackgroundResource(R.drawable.border_irrigation)
                holder.x.setTextColor(Color.parseColor("#000000"))
            }
        }

    companion object DiffCallback : DiffUtil.ItemCallback<HistoricData>() {

        override fun areItemsTheSame(
            oldItem: HistoricData,
            newItem: HistoricData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HistoricData,
            newItem: HistoricData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: HistoricData) -> Unit) {
            fun onClick(data: HistoricData) = clickListener(data)
        }
    }
}