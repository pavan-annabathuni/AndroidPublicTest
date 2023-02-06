package com.waycool.iwap.allservices

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.iwap.databinding.ItemViewallServiceBinding
import com.waycool.uicomponents.R

class PremiumServiceAdapter(private val onClickListener:OnClickListener,val context: Context): ListAdapter<ModuleMasterDomain, PremiumServiceAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemViewallServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivOne
        val name = binding.tvSoilTest
        val view = binding.transparentLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemViewallServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PremiumServiceAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.itemView.setOnClickListener {
            Log.d("CropProtectNavigation","click")
            onClickListener.clickListener(properties)
            val deepLink = properties.deepLink
            if(!deepLink.isNullOrEmpty() && URLUtil.isValidUrl(deepLink.trim())) {
                try {
                    val packageName = "com.android.chrome"
                    val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .build()
                    customTabIntent.intent.setPackage(packageName)
                    customTabIntent.launchUrl(
                        context,
                        Uri.parse(deepLink.trim())
                    )
                }catch (e:Exception){
                    Log.d("link", "onBindViewHolder: $e")
                }

            }else context?.let {
                ToastStateHandling.toastError(
                    it,
                    "No Link",
                    Toast.LENGTH_SHORT
                )
            }
        }
        holder.name.text = properties.title
        Glide.with(holder.itemView.context).load(properties.moduleIcon).into(holder.image)
        if(properties.subscription==0){
            holder.view.visibility = View.GONE
        }else  holder.view.visibility = View.VISIBLE
    }
    companion object DiffCallback : DiffUtil.ItemCallback<ModuleMasterDomain>() {

        override fun areItemsTheSame(
            oldItem: ModuleMasterDomain,
            newItem: ModuleMasterDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ModuleMasterDomain,
            newItem: ModuleMasterDomain
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnClickListener(val clickListener: (data: ModuleMasterDomain) -> Unit) {
        fun onClick(data: ModuleMasterDomain) = clickListener(data)
    }
}