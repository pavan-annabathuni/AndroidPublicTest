package com.waycool.featurecropprotect.Adapter

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.Repository.DomainModels.CropMasterDomain
import com.waycool.uicomponents.R

class BannerAdapter(private val context: Context) :
    ListAdapter<AdBannerImage, BannerAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.viewholder_banner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context)
            .load(getItem(position).url)
            .apply(requestOptions)
            .into(holder.banneriv)
        holder.banneriv.setOnClickListener { view1: View? ->
            if (getItem(position).url.isNotEmpty()) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(getItem(position).url)
                context.startActivity(i)
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var banneriv: ImageView

        init {
            banneriv = itemView.findViewById(R.id.vh_banner_iv)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AdBannerImage>() {

        override fun areItemsTheSame(
            oldItem: AdBannerImage,
            newItem: AdBannerImage
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AdBannerImage,
            newItem: AdBannerImage
        ): Boolean {
            return oldItem.url == newItem.url
        }
    }
}