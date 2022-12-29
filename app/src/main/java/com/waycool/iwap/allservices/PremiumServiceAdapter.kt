package com.waycool.iwap.allservices

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.iwap.databinding.ItemViewallServiceBinding

class PremiumServiceAdapter(private val onClickListener:OnClickListener): ListAdapter<ModuleMasterDomain, PremiumServiceAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemViewallServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivOne
        val name = binding.tvSoilTest
        val view = binding.transparentLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemViewallServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PremiumServiceAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties)
        }
        holder.name.text = properties.title
        Glide.with(holder.itemView.context).load(properties.moduleIcon).into(holder.image)
        if(properties.subscription==0){
            holder.view.visibility = View.GONE
        }else  holder.view.visibility = View.VISIBLE
    }
    companion object DiffCallback : DiffUtil.ItemCallback<ModuleMasterDomain>() {

        override fun areItemsTheSame(
            oldItem: ModuleMasterDomain,
            newItem: ModuleMasterDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ModuleMasterDomain,
            newItem: ModuleMasterDomain
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnClickListener(val clickListener: (data: ModuleMasterDomain) -> Unit) {
        fun onClick(data: ModuleMasterDomain) = clickListener(data)
    }
}