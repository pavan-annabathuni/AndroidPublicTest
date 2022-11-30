package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.example.irrigationplanner.databinding.ItemHistoryDetailsBinding
import com.waycool.data.Network.NetworkModels.HistoricData

class HistoryDetailAdapter(val onClickListener:OnClickListener):
    ListAdapter<HistoricData, HistoryDetailAdapter.MyViewHolder>(HistoryAdapter) {
    var index:Int = 0
    class MyViewHolder(private val binding: ItemHistoryDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        val date = binding.textView16
        val irrigation = binding.irrigation
        val eto = binding.eto
        val etc = binding.textView2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HistoryDetailAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.itemView.setOnClickListener() {
            index = position
            onClickListener.clickListener

        }
        holder.date.text = properties.createdAt
        holder.irrigation.text = "Irrigated ${properties.irrigation}L"
        holder.eto.text = properties.etoCurrent.toString()
        holder.etc.text = properties.etc.toString()
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
    }
    class OnClickListener(val clickListener: (data: HistoricData) -> Unit) {
        fun onClick(data: HistoricData) = clickListener(data)
    }
}