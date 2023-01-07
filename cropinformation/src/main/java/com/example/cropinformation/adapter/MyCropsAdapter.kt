package com.example.cropinformation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropinformation.databinding.ItemMycropsBinding
import com.waycool.data.repository.domainModels.MyCropDataDomain

class MyCropsAdapter(val onClickListener: OnClickListener):ListAdapter<MyCropDataDomain,MyCropsAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding:ItemMycropsBinding):
    RecyclerView.ViewHolder(binding.root) {
     val cropImg = binding.imageView
        val cropName = binding.tvCrops
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemMycropsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        Glide.with(holder.itemView.context).load(properties.cropLogo).into(holder.cropImg)
        holder.cropName.text = properties.cropName
        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<MyCropDataDomain>() {

        override fun areItemsTheSame(oldItem: MyCropDataDomain, newItem: MyCropDataDomain): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyCropDataDomain, newItem:MyCropDataDomain): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: MyCropDataDomain) -> Unit) {
            fun onClick(data: MyCropDataDomain) = clickListener(data)
        }
    }
}