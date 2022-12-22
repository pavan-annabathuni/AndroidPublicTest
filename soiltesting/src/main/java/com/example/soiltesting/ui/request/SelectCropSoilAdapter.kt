package com.example.soiltesting.ui.request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.soiltesting.databinding.ItemSelectSoilTestGroupBinding
import com.waycool.data.repository.domainModels.CropMasterDomain

import com.waycool.uicomponents.databinding.ViewholderCropBinding

class SelectCropSoilAdapter  : ListAdapter<CropMasterDomain, SelectCropSoilAdapter.SelectCropSoilVH>(DiffCallback) {
    var onItemClick: ((CropMasterDomain?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCropSoilVH {
        val binding: ItemSelectSoilTestGroupBinding = ItemSelectSoilTestGroupBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SelectCropSoilVH(binding)
    }

    override fun onBindViewHolder(holder: SelectCropSoilVH, position: Int) {
        holder.bind(getItem(position))
    }
    inner class SelectCropSoilVH(val binding: ItemSelectSoilTestGroupBinding) :
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