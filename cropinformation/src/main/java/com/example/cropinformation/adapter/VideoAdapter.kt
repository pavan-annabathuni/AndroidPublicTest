package com.example.cropinformation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropinformation.apiservice.response.Data
import com.example.cropinformation.apiservice.response.DataX
import com.example.cropinformation.databinding.FragmentCropInfoBinding
import com.example.cropinformation.databinding.ItemVideoBinding

class VideoAdapter: ListAdapter<DataX,VideoAdapter.MyViewHolder>(DiffCallback) {
    private val item:List<DataX> = ArrayList<DataX>()
    class MyViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataX) {
            binding.property = data
            binding.executePendingBindings()
        }
        val img = binding.imgVid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        //Glide.with(holder.img.context).load(properties.tumb_url).into(holder.img)
    }

//        override fun getItemCount(): Int {
//        return 0
//    }


    companion object DiffCallback : DiffUtil.ItemCallback<DataX>() {

        override fun areItemsTheSame(oldItem: DataX, newItem: DataX): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataX, newItem: DataX): Boolean {
            return oldItem.id == newItem.id
        }

        class OnClickListener(val clickListener: (data: DataX) -> Unit) {
            fun onClick(data: DataX) = clickListener(data)
        }
    }
}