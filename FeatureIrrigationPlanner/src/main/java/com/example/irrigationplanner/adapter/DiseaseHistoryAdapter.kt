package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemDiseaseHistoryBinding
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.waycool.data.Network.NetworkModels.Disease

class DiseaseHistoryAdapter: ListAdapter<Disease,DiseaseHistoryAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDiseaseHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
       val date = binding.tvDate
        val slider2 = binding.slider2
        val risk = binding.tvRisk
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemDiseaseHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseHistoryAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.date.text = properties.createdAt
        holder.slider2.value = properties.probability?.toFloat() ?: 0.0.toFloat()

        if(properties.probability!!<50.00)
        holder.risk.text = "Low Risk"
        else if (properties.probability!!>50.00&&properties.probability!!<75.00)
            holder.risk.text = "Medium Risk"
        else
            holder.risk.text = "High Risk"
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Disease>() {

        override fun areItemsTheSame(
            oldItem: Disease,
            newItem: Disease
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Disease,
            newItem: Disease
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

}