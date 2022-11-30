package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.databinding.ItemDiseaseBinding
import com.example.irrigationplanner.databinding.ItemDiseaseHistoryBinding
import com.waycool.data.Network.NetworkModels.Disease
import com.waycool.data.Network.NetworkModels.HistoricData

class DiseaseAdapter:ListAdapter<Disease,DiseaseAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDiseaseBinding): RecyclerView.ViewHolder(binding.root) {
       // val disName =binding.disName
        val slider = binding.slider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return DiseaseAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
       // holder.disName.text = properties.
        holder.slider.value = properties.probability!!.toFloat()
    }
//    override fun getItemCount(): Int {
//        val size=data
//        return if(size>=5) 5
//        else size
//    }
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

    override fun getItemCount(): Int {
        if(currentList.size>=7)
            return 7
        else
            return currentList.size

    }
}