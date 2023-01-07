package com.example.mandiprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.R
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.waycool.data.repository.domainModels.MandiRecordDomain

class DistanceAdapter(val onClickListener: OnClickListener) :
    PagingDataAdapter<MandiRecordDomain, DistanceAdapter.MyViewHolder>(DiffCallback) {


    class MyViewHolder(private val binding: ItemDistanceBinding):
        RecyclerView.ViewHolder(binding.root) {
        val distance = binding.distance
        val imageView = binding.imageViewPrice
        val source = binding.tvSource
        val kg = binding.tvKg
        fun bind(data: MandiRecordDomain?) {
            binding.property = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemDistanceBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
//        if (properties?.distance == null) {
//            holder.distance.visibility = View.GONE
//        } else {
//            holder.distance.visibility = View.VISIBLE
//        }
        when (properties?.price_status) {
            1 -> {
                holder.imageView.setImageResource(R.drawable.ic_uip)
                holder.imageView.visibility = View.VISIBLE
            }
            -1 -> {
                holder.imageView.setImageResource(R.drawable.ic_down)
                holder.imageView.visibility = View.VISIBLE
            }
            else -> holder.imageView.visibility = View.GONE
        }
        when(properties?.source){
            "benchmarker" -> holder.source.visibility = View.INVISIBLE
            else -> {
                holder.source.visibility = View.VISIBLE
                holder.source.text = "Source: ${properties?.source}"
            }
        }

        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
        }
         // TranslationsManager().loadString("Rate / Kg",holder.kg)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<MandiRecordDomain>() {

        override fun areItemsTheSame(oldItem: MandiRecordDomain, newItem: MandiRecordDomain): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MandiRecordDomain, newItem: MandiRecordDomain): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: MandiRecordDomain) -> Unit) {
            fun onClick(data: MandiRecordDomain) = clickListener(data)
        }
    }

}