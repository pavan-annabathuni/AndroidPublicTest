package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemDiseaseHistoryBinding
import com.example.irrigationplanner.databinding.ItemHistoryBinding

class DiseaseHistoryAdapter: RecyclerView.Adapter<DiseaseHistoryAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding: ItemDiseaseHistoryBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDiseaseHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseHistoryAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 8
    }
}