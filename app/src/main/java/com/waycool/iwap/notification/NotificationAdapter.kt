package com.waycool.iwap.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.waycool.data.Network.NetworkModels.DataNotification
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.iwap.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(val onClickListener:OnClickListener,val context: Context):ListAdapter<DataNotification,NotificationAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder (private val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val des = binding.description
        val image = binding.imageView
        val data = binding.date
        val circleImg = binding.dot
        fun bind(data: DataNotification) {
        }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        val deepLink = properties.data2.link
        holder.itemView.setOnClickListener() {
            if(properties.data2.link!=null) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                context.startActivity(i)
            }
            onClickListener.clickListener(properties!!)
        }
//        val inputDateFormatter: SimpleDateFormat =
//            SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.ENGLISH)
//        val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH)
//        val date: Date = inputDateFormatter.parse(properties.createdAt)
//        holder.data.text = outputDateFormatter.format(date)
        holder.data.text = properties.createdAt
        if(properties.readAt!=null){
            holder.circleImg.visibility = View.GONE
        }else holder.circleImg.visibility = View.VISIBLE
        holder.title.text = properties.data2?.title
        holder.des.text = properties.data2?.body
        Glide.with(holder.itemView.context).load(properties.data2?.image).into(holder.image)
        holder.title.isSelected = true
    }


    companion object DiffCallback : DiffUtil.ItemCallback<DataNotification>() {

        override fun areItemsTheSame(
            oldItem: DataNotification,
            newItem: DataNotification
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DataNotification,
            newItem: DataNotification
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnClickListener(val clickListener: (data: DataNotification) -> Unit) {
        fun onClick(data: DataNotification) = clickListener(data)
    }}