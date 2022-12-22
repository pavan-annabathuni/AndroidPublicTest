package com.waycool.weather.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.repository.domainModels.weather.HourlyDomain
import com.waycool.weather.R
import com.waycool.weather.databinding.ItemHourlyBinding
import com.waycool.weather.utils.WeatherIcons
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(val onClickListener:OnClickListener)
    :ListAdapter<HourlyDomain,HourlyAdapter.Viewholder>(DiffCallback) {
    var index:Int = 0

    class Viewholder(private val binding:ItemHourlyBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: HourlyDomain?){
            binding.property = data?: HourlyDomain()
            binding.executePendingBindings()
        }
        val x = binding.tvTime
        val ll = binding.cvHourly
        var tv = binding.tvTemp
        val icon = binding.icon
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
        val date = properties.dt?.times(1000L)
        val dateTime= Date()
        if (date != null) {
            dateTime.time=date
        }
        val formatter = SimpleDateFormat("hh:mm aa",Locale.ENGLISH)//or use getDateInstance()
        val formatDate = formatter.format(dateTime)
        holder.x.text = formatDate

        holder.itemView.setOnClickListener() {
            onClickListener.clickListener(properties)
            index = position
            notifyDataSetChanged()
        }
        if(index == position) {
           // holder.ll.setBackgroundResource(R.drawable.green_border)
            holder.tv.setTextColor(Color.parseColor("#146133"))
        }
        else{
          //  holder.ll.setBackgroundResource(R.drawable.border)
            holder.tv.setTextColor(Color.parseColor("#000000"))
        }
        val image = holder.icon
        properties.weather[0].icon?.let { WeatherIcons.setWeatherIcon(it,image)}
    }


    companion object DiffCallback : DiffUtil.ItemCallback<HourlyDomain>() {

        override fun areItemsTheSame(oldItem: HourlyDomain, newItem: HourlyDomain): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: HourlyDomain, newItem: HourlyDomain): Boolean {
            return oldItem.temp == newItem.temp
        }
}
    class OnClickListener(val clickListener: (data: HourlyDomain) -> Unit) {
        fun onClick(data: HourlyDomain) = clickListener(data)
    }

    override fun getItemCount(): Int {
        if(currentList.size>=12)
            return 12
        else
            return currentList.size

    }
}
