package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemForecastBinding
import com.example.irrigationplanner.databinding.ItemHistoryBinding

class ForecastAdapter: RecyclerView.Adapter<ForecastAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 7
    }
}