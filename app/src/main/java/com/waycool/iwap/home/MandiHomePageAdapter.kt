package com.waycool.iwap.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mandiprice.R

import com.example.soiltesting.databinding.ItemMandiBinding
import com.waycool.data.repository.domainModels.MandiDomainRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MandiHomePageAdapter(val onClickListener:OnClickListener):
    ListAdapter<MandiDomainRecord, MandiHomePageAdapter.MandiHomePageHolder>(MandiHomePageAdapter){
    var cropName: String? = null
    var marketName:String?=null

    class MandiHomePageHolder(private val binding: ItemMandiBinding):
        RecyclerView.ViewHolder(binding.root) {
//        val distance = binding.distance
       val imageView = binding.ivPriceIndex
        val source = binding.tvSource
         val image = binding.ivMandi
        val cropName = binding.tvMango
        val markerName = binding.tvMarket
        fun bind(data: MandiDomainRecord?) {
            binding.property = data
            binding.executePendingBindings()

        }
    }




    override fun onBindViewHolder(holder: MandiHomePageHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        Glide.with(holder.itemView.context).load(properties?.crop_logo).into(holder.image)
//        if (properties?.distance == null) {
//            holder.distance.visibility = View.GONE
//        } else {
//            holder.distance.visibility = View.VISIBLE
//        }
        Log.d("mandiCheck", "onBindViewHolder: ${properties?.crop_logo}")
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
        when(properties?.source){
            "benchmarker" -> holder.source.visibility = View.GONE
            else -> {
                holder.source.visibility = View.VISIBLE
                holder.source.text = "Source: ${properties?.source}"
            }
        }
        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties!!)
        }
//        GlobalScope.launch(Dispatchers.Main) {
//            val langCode = LocalSource.getLanguageCode() ?: "en"
//            when(langCode){
//                "en"->{
//                    holder.cropName.text = properties?.crop
//                    holder.markerName.text = properties?.market
//
//                }
//                "hi"->{
//                    holder.cropName.text = properties?.crop_hi
//                    holder.markerName.text = properties?.market_hi
//
//                }
//                "kn"->{
//                    holder.cropName.text = properties?.crop_kn
//                    holder.markerName.text = properties?.market_kn
//
//                }
//                "te"->{
//                    holder.cropName.text = properties?.crop_te
//                    holder.markerName.text = properties?.market_te
//                }
//                "ta"->{
//                    holder.cropName.text = properties?.crop_ta
//                    holder.markerName.text = properties?.market_ta
//                }
//                "mr"->{
//                    holder.cropName.text = properties?.crop_mr
//                    holder.markerName.text = properties?.market_mr
//                }
//
//            }
//            cropName = holder.cropName.text.toString()
//            marketName = holder.markerName.text.toString()
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MandiHomePageHolder {
        val binding =
            ItemMandiBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return MandiHomePageAdapter.MandiHomePageHolder(binding)
    }


//    override fun getItemCount(): Int {
//        val size= .items.size
//        return if(size>=5) 5
//        else size
//    }
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
