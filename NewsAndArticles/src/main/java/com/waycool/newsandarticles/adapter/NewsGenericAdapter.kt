package com.waycool.newsandarticles.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.VansFeederListDomain

import com.waycool.newsandarticles.Util.AppUtil
import com.waycool.newsandarticles.databinding.ViewholderNewsGenricBinding

class NewsGenericAdapter (private val context: Context?, private val onItemClick: onItemClick):
    PagingDataAdapter<VansFeederListDomain, NewsGenericAdapter.VideosViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val binding =
            ViewholderNewsGenricBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return VideosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("VansFeeder", "Vans: ${item?.title}")
        holder.bind(item)
    }


    inner class VideosViewHolder(private val itemBinding: ViewholderNewsGenricBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(vans: VansFeederListDomain?) {
            itemBinding.newsName.text = vans?.title
            itemBinding.newsDate.text = AppUtil.changeDateFormat(vans?.startDate)
            Glide.with(itemBinding.root)
                .load(vans?.thumbnailUrl)
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(itemBinding.newsUpdateImage)

            itemBinding.newsRoot.setOnClickListener {
                onItemClick.onItemClickListener(vans)
//                onItemClick?.invoke(getItem(absoluteAdapterPosition))
            }


        }
    }

    override fun getItemCount(): Int {
        return if (snapshot().items.size >= 3) 3
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

interface onItemClick{
    fun onItemClickListener(vans: VansFeederListDomain?)
}