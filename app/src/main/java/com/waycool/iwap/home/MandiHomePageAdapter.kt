package com.waycool.iwap.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DistanceAdapter
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.example.soiltesting.databinding.ItemLabsSampleBinding
import com.example.soiltesting.databinding.ItemMandiBinding
import com.example.soiltesting.ui.checksoil.SoilTestingLabsHolder
import com.waycool.data.repository.domainModels.MandiDomainRecord

class MandiHomePageAdapter: PagingDataAdapter<MandiDomainRecord, MandiHomePageAdapter.MandiHomePageHolder>(MandiHomePageAdapter){


    class MandiHomePageHolder(private val binding: ItemMandiBinding):
        RecyclerView.ViewHolder(binding.root) {
//        val distance = binding.distance
//        val imageView = binding.imageViewPrice
        fun bind(data: MandiDomainRecord?) {
            binding.property = data
            binding.executePendingBindings()
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

    override fun onBindViewHolder(holder: MandiHomePageHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
//        if (properties?.distance == null) {
//            holder.distance.visibility = View.GONE
//        } else {
//            holder.distance.visibility = View.VISIBLE
//        }
//        when (properties?.price_status) {
//            1 -> {
//                holder.imageView.setImageResource(R.drawable.ic_uip)
//            }
//            -1 -> {
//                holder.imageView.setImageResource(R.drawable.ic_down)
//            }
//            else -> holder.imageView.visibility = View.GONE
//        }

//        holder.itemView.setOnClickListener() {
//            onClickListener.clickListener(properties!!)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MandiHomePageHolder {
        val binding =
            ItemMandiBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return MandiHomePageAdapter.MandiHomePageHolder(binding)
    }

}