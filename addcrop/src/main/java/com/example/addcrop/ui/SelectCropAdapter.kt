package com.example.addcrop.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.addcrop.databinding.ItemSelectedCropListBinding
import com.waycool.data.Repository.DomainModels.CropMasterDomain
import com.waycool.uicomponents.databinding.ViewholderCropBinding

class SelectCropAdapter: ListAdapter<CropMasterDomain, SelectCropAdapter.SelectCropViewHolder>(DiffCallback) {
    var onItemClick: ((CropMasterDomain?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCropViewHolder {
        val binding:ItemSelectedCropListBinding  = ItemSelectedCropListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCropViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SelectCropViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SelectCropViewHolder(val binding:ItemSelectedCropListBinding ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cropData: CropMasterDomain) {
            binding.cropsName.text = cropData.cropName
            Glide.with(binding.cropsImageView)
                .load(cropData.cropLogo)
                .into(binding.cropsImageView)

            binding.parent.setOnClickListener {
                onItemClick?.invoke(getItem(layoutPosition))
            }
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<CropMasterDomain>() {

        override fun areItemsTheSame(
            oldItem: CropMasterDomain,
            newItem: CropMasterDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CropMasterDomain,
            newItem: CropMasterDomain
        ): Boolean {
            return oldItem.cropId == newItem.cropId
        }
    }


}