package com.waycool.addfarm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.addfarm.databinding.ViewholderCropSelectBinding
import com.waycool.data.repository.domainModels.MyCropDataDomain

class SelectCropAdapter :
    ListAdapter<MyCropDataDomain, SelectCropAdapter.SelectCropViewHolder>(DiffCallback) {
    var onItemClick: ((MutableList<MyCropDataDomain>?) -> Unit)? = null

    var checkedCropList: MutableList<MyCropDataDomain> = mutableListOf()
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

            binding.parent.setOnClickListener {
                if (checkedCropList.contains(getItem(layoutPosition))) {
                    checkedCropList.remove(getItem(layoutPosition))
                    binding.checkIv.visibility = View.INVISIBLE
                } else {
                    checkedCropList.add(getItem(layoutPosition))
                    binding.checkIv.visibility = View.VISIBLE
                }
                onItemClick?.invoke(checkedCropList)
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