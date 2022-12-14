package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemForecastBinding
import com.waycool.data.Network.NetworkModels.IrrigationForecast
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain

class ForecastAdapter:RecyclerView.Adapter<ForecastAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val waterLevel = binding.progressView1
    }
    private var details =IrrigationForecast()
    fun setList(listData: IrrigationForecast) {
        this.details = listData
        notifyDataSetChanged()
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding =
                ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ForecastAdapter.MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = details
        val level = (properties.mad[position].toFloat()) - (properties.depletion[position].toFloat())
        if (properties.mad[position] == 0) {
            val value = 30 - properties.depletion[position].toFloat()
            if (value <= 0) {
                holder.waterLevel.progress = 0F
            } else {
                val value = 30 - properties.depletion[position].toFloat()
                val percentage = (value / 30) * 100
                holder.waterLevel.progress = percentage
            }
        } else {
            val value = properties.mad[position] - properties.depletion[position].toFloat()
            if (value <= 0) {
                holder.waterLevel.progress = 0F
            } else {
                val value = properties.mad[position] - properties.depletion[position].toFloat()
                val percentage = (value / properties.mad[position]) * 100
                holder.waterLevel.progress = percentage
            }
//        if(level<=0)
//        holder.waterLevel.progress = level
        }
    }

//    companion object DiffCallback : DiffUtil.ItemCallback<IrrigationForecast>() {
//
//        override fun areItemsTheSame(
//            oldItem: IrrigationForecast,
//            newItem: IrrigationForecast
//        ): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(
//            oldItem: IrrigationForecast,
//            newItem: IrrigationForecast
//        ): Boolean {
//            return oldItem.days == newItem.days
//        }
//    }

    override fun getItemCount(): Int {
       return details.days.size
    }
}