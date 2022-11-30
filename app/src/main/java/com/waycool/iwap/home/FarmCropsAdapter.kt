package com.waycool.iwap.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropinformation.databinding.ItemMycropsBinding
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.iwap.databinding.ViewholderFarmCropsBinding

class FarmCropsAdapter :
    ListAdapter<MyCropDataDomain, FarmCropsAdapter.MyViewHolder>(DiffCallback) {


    class MyViewHolder(val binding: ViewholderFarmCropsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(crop: MyCropDataDomain) {
            Glide.with(binding.crop).load(crop.cropLogo).into(binding.crop)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MyCropDataDomain>() {

        override fun areItemsTheSame(
            oldItem: MyCropDataDomain,
            newItem: MyCropDataDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MyCropDataDomain,
            newItem: MyCropDataDomain
        ): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: MyCropDataDomain) -> Unit) {
            fun onClick(data: MyCropDataDomain) = clickListener(data)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding =
            ViewholderFarmCropsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}