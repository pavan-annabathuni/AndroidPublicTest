package com.example.cropinformation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cropinformation.apiservice.response.DataX
import com.example.cropinformation.databinding.ItemNewsBinding

class NewsAdapter:androidx.recyclerview.widget.ListAdapter<DataX,NewsAdapter.MyViewHolder>(VideoAdapter.DiffCallback) {
    class MyViewHolder(private val binding: ItemNewsBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataX){
                  binding.property = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
    }

//    override fun getItemCount(): Int {
//        return  3
//    }

    class OnClickListener(val clickListener: (data: DataX) -> Unit) {
        fun onClick(data: DataX) = clickListener(data)
    }
}