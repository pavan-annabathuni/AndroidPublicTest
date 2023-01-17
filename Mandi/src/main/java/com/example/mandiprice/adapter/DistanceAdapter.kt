package com.example.mandiprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.R
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.MandiRecord
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.*

class DistanceAdapter(val onClickListener: OnClickListener,val langCode:String) :
    PagingDataAdapter<MandiDomainRecord, DistanceAdapter.MyViewHolder>(DiffCallback) {

    var cropName: String? = null
    var marketName:String?=null

    class MyViewHolder(private val binding: ItemDistanceBinding):
        RecyclerView.ViewHolder(binding.root) {
        val cropName = binding.textView3
        val markerName = binding.textView4
        val distance = binding.distance
        val imageView = binding.imageViewPrice
        val source = binding.tvSource
        val kg = binding.tvKg
        fun bind(data: MandiDomainRecord?) {
            binding.property = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemDistanceBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return MyViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
//        if (properties?.distance == null) {
//            holder.distance.visibility = View.GONE
//        } else {
//            holder.distance.visibility = View.VISIBLE
//        }
        when (properties?.price_status) {
            1 -> {
                holder.imageView.setImageResource(R.drawable.ic_uip)
                holder.imageView.visibility = View.VISIBLE
            }
            -1 -> {
                holder.imageView.setImageResource(R.drawable.ic_down)
                holder.imageView.visibility = View.VISIBLE
            }
            else -> holder.imageView.visibility = View.GONE
        }
        when (properties?.source) {
            "benchmarker" -> holder.source.visibility = View.INVISIBLE
            else -> {
                holder.source.visibility = View.VISIBLE
                holder.source.text = "Source: ${properties?.source}"
            }
        }

        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
        }
        // TranslationsManager().loadString("Rate / Kg",holder.kg)
//        GlobalScope.launch(Dispatchers.Main){
//        val langCode = LocalSource.getLanguageCode() ?: "en"
        when (langCode) {
            "en" -> {
                holder.cropName.text = properties?.crop
                holder.markerName.text = properties?.market
            }
            "hi" -> {
                holder.cropName.text = properties?.crop_hi
                holder.markerName.text = properties?.market_hi
            }
            "kn" -> {
                holder.cropName.text = properties?.crop_kn
                holder.markerName.text = properties?.market_kn
            }
            "te" -> {
                holder.cropName.text = properties?.crop_te
                holder.markerName.text = properties?.market_te
            }
            "ta" -> {
                holder.cropName.text = properties?.crop_ta
                holder.markerName.text = properties?.market_ta
            }
            "mr" -> {
                holder.cropName.text = properties?.crop_mr
                holder.markerName.text = properties?.market_mr
            }

        }
        cropName = holder.cropName.text.toString()
        marketName = holder.markerName.text.toString()

    }


    companion object DiffCallback : DiffUtil.ItemCallback<MandiDomainRecord>() {

        override fun areItemsTheSame(oldItem: MandiDomainRecord, newItem: MandiDomainRecord): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MandiDomainRecord, newItem: MandiDomainRecord): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: MandiDomainRecord) -> Unit) {
            fun onClick(data: MandiDomainRecord) = clickListener(data)
        }
    }

}