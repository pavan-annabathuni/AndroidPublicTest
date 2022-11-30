package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemForecastBinding
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.data.Network.NetworkModels.Irrigation

class ForecastAdapter():ListAdapter<HistoricData,ForecastAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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