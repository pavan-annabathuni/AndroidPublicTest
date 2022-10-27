package com.example.mandiprice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.databinding.ItemDateBinding
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.domainModels.MandiHistoryDataDomain

class DateAdapter : ListAdapter<MandiHistoryDataDomain, DateAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MandiHistoryDataDomain?) {
            binding.property = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MandiHistoryDataDomain>() {

        override fun areItemsTheSame(
            oldItem: MandiHistoryDataDomain,
            newItem: MandiHistoryDataDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MandiHistoryDataDomain,
            newItem: MandiHistoryDataDomain
        ): Boolean {
            return oldItem.arrivalDate == newItem.arrivalDate
        }

        class OnClickListener(val clickListener: (data: MandiHistoryDataDomain) -> Unit) {
            fun onClick(data: MandiHistoryDataDomain) = clickListener(data)
        }
    }

}