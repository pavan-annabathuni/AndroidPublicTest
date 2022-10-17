package com.waycool.videos.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.Repository.DomainModels.VansFeederListDomain
import com.waycool.videos.Util.AppUtil
import com.waycool.videos.databinding.ViewholderVideoGenericBinding

class VideosGenericAdapter :
    PagingDataAdapter<VansFeederListDomain, VideosGenericAdapter.VideosViewHolder>(COMPARATOR) {

    var onItemClick: ((VansFeederListDomain?) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val binding =
            ViewholderVideoGenericBinding.inflate(
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


    inner class VideosViewHolder(private val itemBinding: ViewholderVideoGenericBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(vans: VansFeederListDomain?) {
            itemBinding.videoDesc.text = vans?.title
            itemBinding.videoDate.text = AppUtil.changeDateFormat(vans?.startDate)
            Glide.with(itemBinding.root)
                .load("https://img.youtube.com/vi/${vans?.contentUrl}/hqdefault.jpg")
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(itemBinding.videoImage)

            itemBinding.videoCardview.setOnClickListener {
                onItemClick?.invoke(getItem(absoluteAdapterPosition))
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