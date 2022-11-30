package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.example.irrigationplanner.databinding.ItemHistoryDetailsBinding

class HistoryDetailAdapter: RecyclerView.Adapter<HistoryDetailAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding: ItemHistoryDetailsBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryDetailAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 8
    }
}