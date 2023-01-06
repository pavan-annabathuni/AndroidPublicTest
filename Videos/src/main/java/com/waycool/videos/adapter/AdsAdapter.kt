package com.waycool.videos.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.uicomponents.R
import com.waycool.uicomponents.databinding.ViewholderBannerBinding

class AdsAdapter(val context: Context) :
    PagingDataAdapter<VansFeederListDomain, AdsAdapter.AdsViewHolder>(COMPARATOR) {

    var onItemClick: ((VansFeederListDomain?) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsViewHolder {
        val binding =
            ViewholderBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AdsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdsViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("VansFeeder", "Vans: ${item?.title}")
        holder.bind(item)
    }


    inner class AdsViewHolder(private val itemBinding: ViewholderBannerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(vans: VansFeederListDomain?) {

            Glide.with(itemBinding.root)
                .load(vans?.thumbnailUrl)
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(itemBinding.vhBannerIv)

            itemBinding.vhBannerIv.setOnClickListener {
                val packageName = "com.android.chrome"
                val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context,R.color.primaryColor))
                    .build()
                customTabIntent.intent.setPackage(packageName)
                customTabIntent.launchUrl(
                    context,
                    Uri.parse(vans?.contentUrl)
                )

            }


        }
    }

    override fun getItemCount(): Int {
        return if (snapshot().items.size >= 5) 5
        else snapshot().items.size
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<VansFeederListDomain>() {
            override fun areItemsTheSame(
                oldItem: VansFeederListDomain,
                newItem: VansFeederListDomain
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: VansFeederListDomain,
                newItem: VansFeederListDomain
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}