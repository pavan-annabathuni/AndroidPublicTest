package com.example.mandiprice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.example.mandiprice.databinding.ItemPriceBinding

class PriceAdapter:RecyclerView.Adapter<PriceAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding: ItemPriceBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PriceAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 8
    }
}