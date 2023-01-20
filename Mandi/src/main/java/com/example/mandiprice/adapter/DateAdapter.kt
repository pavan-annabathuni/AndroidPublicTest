package com.example.mandiprice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiprice.databinding.ItemDateBinding
import com.example.mandiprice.databinding.ItemDistanceBinding
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.domainModels.MandiHistoryDataDomain
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter : ListAdapter<MandiHistoryDataDomain, DateAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MandiHistoryDataDomain?) {
            binding.property = data
            binding.executePendingBindings()
        }
        val arrivalDate = binding.textView6
        val avgPrice = binding.avgPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        val inputDateFormatter: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date:Date = inputDateFormatter.parse(properties.updatedAt)

        holder.arrivalDate.text = outputDateFormatter.format(date)


        var s:Float = properties.avgPrice!!.toFloat()
       val price  = "%.2f".format(s)
        holder.avgPrice.text = "\u20B9$price"

    }

    companion object DiffCallback : DiffUtil.ItemCallback<MandiHistoryDataDomain>() {

        override fun areItemsTheSame(
            oldItem: MandiHistoryDataDomain,
            newItem: MandiHistoryDataDomain
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MandiHistoryDataDomain,
            newItem: MandiHistoryDataDomain
        ): Boolean {
            return oldItem.arrivalDate == newItem.arrivalDate
        }

        class OnClickListener(val clickListener: (data: MandiHistoryDataDomain) -> Unit) {
            fun onClick(data: MandiHistoryDataDomain) = clickListener(data)
        }
    }

//    override fun getItemCount(): Int {
//        if(currentList.size>=7)
//            return 7
//        else
//            return currentList.size
//
//    }

}