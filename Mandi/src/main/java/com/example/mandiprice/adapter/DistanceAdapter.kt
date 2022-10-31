package com.example.mandiprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.R
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.waycool.data.Network.NetworkModels.MandiRecord
import com.waycool.data.repository.domainModels.MandiDomainRecord

class DistanceAdapter(val onClickListener: OnClickListener) :
    PagingDataAdapter<MandiDomainRecord, DistanceAdapter.MyViewHolder>(DiffCallback) {


    class MyViewHolder(private val binding: ItemDistanceBinding):
        RecyclerView.ViewHolder(binding.root) {
        val distance = binding.distance
        val imageView = binding.imageViewPrice
        fun bind(data: MandiDomainRecord?) {
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
        if (properties?.distance == null) {
            holder.distance.visibility = View.GONE
        } else {
            holder.distance.visibility = View.VISIBLE
        }
        when (properties?.price_status) {
            1 -> {
                holder.imageView.setImageResource(R.drawable.ic_uip)
            }
            -1 -> {
                holder.imageView.setImageResource(R.drawable.ic_down)
            }
            else -> holder.imageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
        }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<MandiDomainRecord>() {

        override fun areItemsTheSame(oldItem: MandiDomainRecord, newItem: MandiDomainRecord): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MandiDomainRecord, newItem: MandiDomainRecord): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: MandiDomainRecord) -> Unit) {
            fun onClick(data: MandiDomainRecord) = clickListener(data)
        }
    }

}