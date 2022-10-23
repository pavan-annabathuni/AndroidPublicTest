package com.waycool.videos.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.videos.Util.AppUtil
import com.waycool.videos.databinding.ViewholderVideosListBinding

class VideosPagerAdapter(
    private val context: Context,
    private val selectedVideo: VansFeederListDomain? = null
) :
    PagingDataAdapter<VansFeederListDomain, VideosPagerAdapter.VideosViewHolder>(COMPARATOR) {

    var onItemClick: ((VansFeederListDomain?) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val binding =
            ViewholderVideosListBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("VansFeeder", "Vans: ${item?.title}")
        holder.bind(item)
    }


    inner class VideosViewHolder(private val itemBinding: ViewholderVideosListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(vans: VansFeederListDomain?) {
            itemBinding.videosListVideoTitle.text = vans?.title
            itemBinding.videoDate.text = AppUtil.changeDateFormat(vans?.startDate)
            Glide.with(itemBinding.root)
                .load(   "https://img.youtube.com/vi/${vans?.contentUrl}/hqdefault.jpg")
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(itemBinding.videosListVideoImage)
            itemBinding.share.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                val sharetext =
                    """Hi, Checkout the video on ${vans?.title} at ${vans?.contentUrl} . For more videos Download Outgrow App from PlayStore 
https://play.google.com/store/apps/details?id=${it.context.packageName}"""
                sendIntent.putExtra(Intent.EXTRA_TEXT, sharetext)
                sendIntent.type = "text/plain"
                it.context.startActivity(Intent.createChooser(sendIntent, "share"))
            }

            itemBinding.videosListVideoCardview.setOnClickListener {
                onItemClick?.invoke(getItem(absoluteAdapterPosition))
            }

        }
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