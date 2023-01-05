package com.waycool.featurecropprotect.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.waycool.data.repository.domainModels.PestDiseaseDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.ViewholderCropProtectParentBinding

class DiseasesParentAdapter() :
    ListAdapter<PestDiseaseDomain, DiseasesParentAdapter.ViewHolder>(DiffCallback) {

    var onItemClick: ((PestDiseaseDomain?) -> Unit)? = null
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            ViewholderCropProtectParentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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

            binding.cropProtectDiseaseImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(binding.cropProtectDiseaseImage.context,
                    listOf(item.thumb),
                    ImageLoader { imageView: ImageView, image: String? ->
                        Glide.with(binding.cropProtectDiseaseImage.context)
                            .load(image)
                            .into(imageView)
                    }).allowSwipeToDismiss(true)
                    .allowZooming(true)
                    .withTransitionFrom(binding.cropProtectDiseaseImage)
                    .show(true)
            }

            binding.viewmoreTv.setOnClickListener {
                onItemClick?.invoke(getItem(layoutPosition))
            }
            val adapter = DiseasesChildAdapter()
            binding.subRecycler.adapter = adapter
            if (item.imageUrl == null){
                adapter.submitList(emptyList())
            }
            else adapter.submitList(item.imageUrl){

            }
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