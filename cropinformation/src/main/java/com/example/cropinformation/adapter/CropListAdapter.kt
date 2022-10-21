package com.example.cropinformation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.Repository.DomainModels.CropMasterDomain
import com.waycool.uicomponents.databinding.ViewholderCropBinding

class CropListAdapter : ListAdapter<CropMasterDomain, CropListAdapter.CropsListVH>(DiffCallback) {

    var onItemClick: ((CropMasterDomain?) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropsListVH {
        val binding: ViewholderCropBinding = ViewholderCropBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return CropsListVH(binding)
    }

    override fun onBindViewHolder(holder: CropsListVH, position: Int) {
        holder.bind(getItem(position))
    }



    inner class CropsListVH(val binding: ViewholderCropBinding) :
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