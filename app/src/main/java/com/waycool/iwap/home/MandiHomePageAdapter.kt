package com.waycool.iwap.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mandiprice.R
import com.example.soiltesting.databinding.ItemMandiBinding
import com.waycool.data.repository.domainModels.MandiRecordDomain

class MandiHomePageAdapter(val onClickListener:OnClickListener):
    PagingDataAdapter<MandiRecordDomain, MandiHomePageAdapter.MandiHomePageHolder>(MandiHomePageAdapter){


    class MandiHomePageHolder(private val binding: ItemMandiBinding):
        RecyclerView.ViewHolder(binding.root) {
//        val distance = binding.distance
       val imageView = binding.ivPriceIndex
        val source = binding.tvSource
         val image = binding.ivMandi
        fun bind(data: MandiRecordDomain?) {
            binding.property = data
            binding.executePendingBindings()

        }
    }




    override fun onBindViewHolder(holder: MandiHomePageHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        Glide.with(holder.itemView.context).load(properties?.crop_logo).into(holder.image)
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
            "benchmarker" -> holder.source.visibility = View.GONE
            else -> {
                holder.source.visibility = View.VISIBLE
                holder.source.text = "Source: ${properties?.source}"
            }
        }
        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MandiHomePageHolder {
        val binding =
            ItemMandiBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return MandiHomePageAdapter.MandiHomePageHolder(binding)
    }


    override fun getItemCount(): Int {
        val size=snapshot().items.size
        return if(size>=5) 5
        else size
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
