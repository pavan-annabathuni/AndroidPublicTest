package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemForecastBinding
import com.waycool.data.Network.NetworkModels.IrrigationForecast
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val waterLevel = binding.progressView1
    }

    private var details = IrrigationForecast()
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
        val level = (properties.mad[position]?.toFloat()) ?: 0f - (properties.depletion[position]?.toFloat()?:0f)
        if (properties.mad[position] == 0.0) {
            val value = 30 - (properties.depletion[position]?.toFloat()?:0f)
            if (value <= 0) {
                holder.waterLevel.progress = 0F
            } else {
                val value = 30 - (properties.depletion[position]?.toFloat()?:0f)
                val percentage = (value / 30) * 100
                holder.waterLevel.progress = percentage
            }
        } else {
            val value = (properties.mad[position] ?: 0.0) - (properties.depletion[position]?.toDouble()?:0.0)
            if (value <= 0) {
                holder.waterLevel.progress = 0F
            } else {
                val value:Double = (properties.mad[position] ?: 0f - (properties.depletion[position]?.toFloat()?:0f)) as Double
                if (properties.mad[position] != null) {
                    val percentage: Double = (value / properties.mad[position]!!) * 100
                    holder.waterLevel.progress = percentage.toFloat()
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return details.days.size
    }
}