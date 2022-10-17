package com.waycool.newsandarticles.adapter

import android.R
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.Repository.DomainModels.VansFeederListDomain
import com.waycool.newsandarticles.Util.AppUtil
import com.waycool.newsandarticles.databinding.ViewholderNewsArticlesListBinding

class NewsPagerAdapter(
    private val context: Context,
    private val selectedNews: VansFeederListDomain? = null
) :
    PagingDataAdapter<VansFeederListDomain, NewsPagerAdapter.VideosViewHolder>(COMPARATOR) {
    private var lastPosition = -1

    var onItemClick: ((VansFeederListDomain?) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val binding =
            ViewholderNewsArticlesListBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("VansFeeder", "Vans: ${item?.title}")
        holder.bind(item)
        holder.setAnimation(holder.itemView, position)
    }


    override fun onViewDetachedFromWindow(holder: VideosViewHolder) {
        holder.clearAnimation()
    }


    inner class VideosViewHolder(private val itemBinding: ViewholderNewsArticlesListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(vans: VansFeederListDomain?) {
            itemBinding.newsListDesc.text = vans?.title
            itemBinding.newsListDate.text = AppUtil.changeDateFormat(vans?.startDate)
            Glide.with(itemBinding.root).load(vans?.thumbnailUrl)
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(itemBinding.newsListImage)
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

            itemBinding.newsCv.setOnClickListener {
                onItemClick?.invoke(getItem(absoluteAdapterPosition))
            }

        }

        fun clearAnimation() {
            itemBinding.root.clearAnimation()
        }

        fun setAnimation(viewToAnimate: View, position: Int) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                val animation: Animation =
                    AnimationUtils.loadAnimation(context, R.anim.fade_in)
                viewToAnimate.startAnimation(animation)
                lastPosition = position
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