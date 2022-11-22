package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemDiseaseBinding
import com.example.irrigationplanner.databinding.ItemDiseaseHistoryBinding

class DiseaseAdapter:RecyclerView.Adapter<DiseaseAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding: ItemDiseaseBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 5
    }
}