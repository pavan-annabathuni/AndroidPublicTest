package com.waycool.featurecropprotect.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.PestDiseaseDomain
import com.waycool.featurecropprotect.databinding.ViewholderCropProtectParentBinding

class DiseasesParentAdapter() :
    ListAdapter<PestDiseaseDomain, DiseasesParentAdapter.ViewHolder>(DiffCallback) {

    var onItemClick: ((PestDiseaseDomain?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewholderCropProtectParentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ViewholderCropProtectParentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PestDiseaseDomain) {
            binding.cropProtectDiseaseName.text = item.diseaseName
            Glide.with(binding.cropProtectDiseaseImage).load(item.thumb)
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(binding.cropProtectDiseaseImage)

            binding.viewmoreTv.setOnClickListener {
                onItemClick?.invoke(getItem(layoutPosition))
            }
            val adapter = DiseasesChildAdapter()
            binding.subRecycler.adapter = adapter
            adapter.submitList(listOf("","","","",""))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PestDiseaseDomain>() {

        override fun areItemsTheSame(
            oldItem: PestDiseaseDomain,
            newItem: PestDiseaseDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PestDiseaseDomain,
            newItem: PestDiseaseDomain
        ): Boolean {
            return oldItem.diseaseId == newItem.diseaseId
        }
    }


}