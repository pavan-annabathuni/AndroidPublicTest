package com.example.mandiprice.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.uicomponents.databinding.ViewholderBannerBinding

class AdsAdapter :
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
                onItemClick?.invoke(getItem(layoutPosition))
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