package com.example.adddevice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adddevice.databinding.ViewholderCropSelectBinding
import com.waycool.data.repository.domainModels.MyCropDataDomain

class SelectCropAdapter :
    ListAdapter<MyCropDataDomain, SelectCropAdapter.SelectCropViewHolder>(DiffCallback) {
    var onItemClick: ((MyCropDataDomain?) -> Unit)? = null

    var selectedItemPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCropViewHolder {
        val binding: ViewholderCropSelectBinding = ViewholderCropSelectBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCropViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCropViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SelectCropViewHolder(val binding: ViewholderCropSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cropData: MyCropDataDomain) {
            binding.cropsName.text = cropData.cropName
            Glide.with(binding.cropsImageView)
                .load(cropData.cropLogo)
                .into(binding.cropsImageView)

            if (layoutPosition == selectedItemPosition) {
                binding.checkIv.visibility = View.VISIBLE
            } else
                binding.checkIv.visibility = View.INVISIBLE

            binding.parent.setOnClickListener {
                onItemClick?.invoke(getItem(layoutPosition))

                val tempPos: Int = selectedItemPosition
                selectedItemPosition = layoutPosition
                notifyItemChanged(tempPos)
                notifyItemChanged(selectedItemPosition)
                onItemClick?.invoke(getItem(layoutPosition))
            }
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
    }


}