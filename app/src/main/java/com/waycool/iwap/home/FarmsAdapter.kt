package com.waycool.iwap.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemFlexBoxAddFormBinding
import com.waycool.data.repository.domainModels.MyFarmsDomain

class FarmsAdapter(val context: Context) :
    ListAdapter<MyFarmsDomain, FarmsAdapter.ViewHolder>(DiffCallback) {

    var onItemClick: ((MyFarmsDomain?) -> Unit)? = null
    var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFlexBoxAddFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (selectedPosition == -1) {
            selectedPosition = position
            onItemClick?.invoke(getItem(position))
        }

        holder.bind(getItem(position))

    }


    inner class ViewHolder(val binding: ItemFlexBoxAddFormBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(farm: MyFarmsDomain) {
            binding.skillName.text = farm.farmName
            if (layoutPosition == selectedPosition) {
                binding.clTop.backgroundTintList =
                    ContextCompat.getColorStateList(
                        context,
                        com.waycool.uicomponents.R.color.green
                    )
                binding.skillName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        com.waycool.uicomponents.R.color.white
                    )
                )

            } else {
                binding.clTop.backgroundTintList =
                    ContextCompat.getColorStateList(
                        context,
                        com.waycool.uicomponents.R.color.strokegrey
                    )
                binding.skillName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        com.waycool.uicomponents.R.color.textdark
                    )
                )
            }

            binding.clTop.setOnClickListener {
                val temp=selectedPosition
                selectedPosition = layoutPosition
                notifyItemChanged(temp)
                notifyItemChanged(selectedPosition)
                onItemClick?.invoke(getItem(layoutPosition))
            }
        }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<MyFarmsDomain>() {

        override fun areItemsTheSame(oldItem: MyFarmsDomain, newItem: MyFarmsDomain): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyFarmsDomain, newItem: MyFarmsDomain): Boolean {
            return oldItem.id == newItem.id
        }
    }

}