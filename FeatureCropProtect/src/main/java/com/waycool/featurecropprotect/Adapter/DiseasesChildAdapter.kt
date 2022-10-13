package com.waycool.featurecropprotect.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.ViewholderCropProtectChildBinding

class DiseasesChildAdapter() : ListAdapter<String, DiseasesChildAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ViewholderCropProtectChildBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }


    inner class ViewHolder(private val binding: ViewholderCropProtectChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            Glide.with(binding.childImage).load(getItem(layoutPosition))
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(binding.childImage)
            binding.childImage.setOnClickListener {

            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }


}