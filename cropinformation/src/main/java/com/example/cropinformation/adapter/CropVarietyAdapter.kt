package com.example.cropinformation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cropinformation.databinding.ItemCropVarietyBinding
import com.google.android.material.chip.Chip
import com.waycool.data.repository.domainModels.CropVarityDomain
import com.waycool.data.repository.domainModels.MandiDomainRecord

class CropVarietyAdapter :
    ListAdapter<CropVarityDomain, CropVarietyAdapter.viewHolder>(DiffCallback) {
    class viewHolder(private val binding: ItemCropVarietyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CropVarityDomain) {
            binding.property = data
            binding.executePendingBindings()
            binding.labelValue.text = data.stateName

            binding.cropVarietyChipGroup.removeAllViews()
            for (category in data.cropVarietyValue) {
                binding.cropVarietyChipGroup.addView(createChip(category, binding.root.context))
            }

        }

        private fun createChip(category: String, context: Context): Chip {
            val chip = Chip(context)
            chip.text = category
            chip.isCheckable = false
            chip.isClickable = false
            chip.isCheckedIconVisible = false
            chip.setTextColor(
                AppCompatResources.getColorStateList(
                    context,
                    com.waycool.uicomponents.R.color.bg_chip_text
                )
            )
            chip.setChipBackgroundColorResource(com.waycool.uicomponents.R.color.chip_bg)
            chip.chipStrokeWidth = 1f
            chip.chipStrokeColor = AppCompatResources.getColorStateList(
                context,
                com.waycool.uicomponents.R.color.bg_chip_text
            )
            return chip
//            binding..addView(chip)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            ItemCropVarietyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CropVarietyAdapter.viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CropVarityDomain>() {

        override fun areItemsTheSame(
            oldItem: CropVarityDomain,
            newItem: CropVarityDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CropVarityDomain,
            newItem: CropVarityDomain
        ): Boolean {
            return oldItem.cropVarietyValue == newItem.cropVarietyValue
        }
    }
}