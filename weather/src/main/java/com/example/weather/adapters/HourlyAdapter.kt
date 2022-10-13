package com.example.weather.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.apiService.apiResponse.Daily
import com.example.weather.apiService.apiResponse.Hourly
import com.example.weather.databinding.ItemHourlyBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyAdapter(val onClickListener:OnClickListener)
    :ListAdapter<Hourly,HourlyAdapter.Viewholder>(DiffCallback) {
    var index:Int = 0

    class Viewholder(private val binding:ItemHourlyBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: Hourly){
            binding.property = data
            binding.executePendingBindings()
        }
        val x = binding.tvTime
        val ll = binding.llHourly
        var tv = binding.tvTemp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val properties = getItem(position)
        holder.bind(properties)
        holder.bind(properties)
        val date = properties.dt
        val formatter = SimpleDateFormat("hh:mm aa",Locale.ENGLISH)//or use getDateInstance()
        val formatDate = formatter.format(date*1000L)
        holder.x.text = formatDate

        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties)
            index = position
            notifyDataSetChanged()
        }
        if(index == position) {
            holder.ll.setBackgroundResource(R.drawable.green_border)
            holder.tv.setTextColor(Color.parseColor("#146133"))
        }
        else{
            holder.ll.setBackgroundResource(R.drawable.border)
            holder.tv.setTextColor(Color.parseColor("#000000"))
        }



    }


    companion object DiffCallback : DiffUtil.ItemCallback<Hourly>() {

        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Hourly, newItem:Hourly): Boolean {
            return oldItem.temp == newItem.temp
        }
}
    class OnClickListener(val clickListener: (data: Hourly) -> Unit) {
        fun onClick(data:Hourly) = clickListener(data)
    }
}
