package com.waycool.featurecropprotect.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.waycool.featurecropprotect.databinding.ViewholderCropProtectChildBinding

class DiseasesChildAdapter() : ListAdapter<String, DiseasesChildAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ViewholderCropProtectChildBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }


    inner class ViewHolder(private val binding: ViewholderCropProtectChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(url:String) {
            Glide.with(binding.childImage)
                .load(url)
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(binding.childImage)
            binding.childImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(binding.childImage.context, currentList ,
                    ImageLoader { imageView: ImageView, image: String? ->
                        Glide.with(binding.childImage.context)
                            .load(image)
                            .into(imageView)
                    }).allowSwipeToDismiss(true)
                    .withStartPosition(layoutPosition)
                    .allowZooming(true)
                    .withTransitionFrom(binding.childImage)
                    .show(true)
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