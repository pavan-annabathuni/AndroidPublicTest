package com.waycool.iwap.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.Network.NetworkModels.DataNotification
import com.waycool.data.Network.NetworkModels.Notification
import com.waycool.iwap.databinding.ItemNotificationBinding

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
        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties.data2,properties)
            notifyDataSetChanged()
        }

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
    class OnClickListener(val clickListener: (data2: Notification,data:DataNotification) -> Unit) {
        fun onClick(data2: Notification,data:DataNotification) = clickListener(data2,data)

    }
}