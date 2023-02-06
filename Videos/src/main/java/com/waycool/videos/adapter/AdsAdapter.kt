package com.waycool.videos.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.uicomponents.R
import com.waycool.uicomponents.databinding.ViewholderBannerBinding

class AdsAdapter(val context: Context, private  var bannerViewpager: ViewPager2) :
    ListAdapter<VansFeederListDomain, AdsAdapter.AdsViewHolder>(COMPARATOR) {

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
        val position=position
        holder.bind(item,position)
    }


    inner class AdsViewHolder(private val itemBinding: ViewholderBannerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(vans: VansFeederListDomain?, position: Int) {

            Glide.with(itemBinding.root)
                .load(vans?.thumbnailUrl)
                .placeholder(R.drawable.outgrow_logo_new)
                .into(itemBinding.vhBannerIv)

            itemBinding.vhBannerIv.setOnClickListener {
                if (vans?.contentUrl != null && context != null && URLUtil.isValidUrl(vans.contentUrl)) {
                    val packageName = "com.android.chrome"
                    val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .build()
                    customTabIntent.intent.setPackage(packageName)
                    customTabIntent.launchUrl(
                        context,
                        Uri.parse(vans.contentUrl)
                    )
                }
            }

          /*  if(position==itemCount-1){
                bannerViewpager.post(runnable)
            }*/

          Log.d("ITEM","ITEM$itemCount")

        }
    }

//    override fun getItemCount(): Int {
//        return if (currentList.size >= 5) 5
//        else currentList.size
//    }

//    private val runnable= Runnable {
//        submitList(currentList)
//        notifyDataSetChanged()
//    }
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