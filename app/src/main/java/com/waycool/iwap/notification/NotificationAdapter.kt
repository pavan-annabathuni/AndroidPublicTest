package com.waycool.iwap.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.Network.NetworkModels.DataNotification
import com.waycool.iwap.databinding.ItemNotificationBinding

class NotificationAdapter:ListAdapter<DataNotification,NotificationAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder (private val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val des = binding.description
        val image = binding.imageView
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
        holder.title.text = properties.data?.title
        holder.des.text = properties.data?.body
        Glide.with(holder.itemView.context).load(properties.data?.image).into(holder.image)
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
    }}