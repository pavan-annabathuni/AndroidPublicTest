package com.waycool.videos.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.videos.Util.AppUtil
import com.waycool.videos.databinding.ViewholderVideosListBinding

class VideosPagerAdapter(
    private val context: Context,
    private val onItemClickOne: itemClick,
    private val selectedVideo: VansFeederListDomain? = null
) :
    PagingDataAdapter<VansFeederListDomain, VideosPagerAdapter.VideosViewHolder>(COMPARATOR) {

//    var onItemClick: ((VansFeederListDomain?) -> Unit)? = null
//    var onItemShareClick: ((VansFeederListDomain?) -> Unit)? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val binding =
            ViewholderVideosListBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val item = getItem(position)
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
                Log.d("ItemPos","ItemPosShare${absoluteAdapterPosition}")
                onItemClickOne.onShareItemClick(vans)

//                onItemShareClick?.invoke(getItem(absoluteAdapterPosition))
            }
            itemBinding.videosListVideoCardview.setOnClickListener {
                Log.d("ItemPos","ItemPosItem${absoluteAdapterPosition}")
                onItemClickOne.onItemClick(vans)

//                onItemClick?.invoke(getItem(absoluteAdapterPosition))
                Log.d("ItemPos","ItemPosItemClick${absoluteAdapterPosition}")

            }

            TranslationsManager().loadString("share",itemBinding.share)

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
interface itemClick{
    fun onItemClick(van:VansFeederListDomain?)
    fun onShareItemClick(van:VansFeederListDomain?)

}