package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemWeeklyIrrgationBinding
import com.waycool.data.Network.NetworkModels.IrrigationForecast
import java.text.SimpleDateFormat
import java.util.*


class WeeklyAdapter: RecyclerView.Adapter<WeeklyAdapter.MyViewHolder>() {
    var index:Int = 0
    var i = 0
    private var selectedPosition = 0

    class MyViewHolder(private val binding: ItemWeeklyIrrgationBinding): RecyclerView.ViewHolder(binding.root) {
        val x = binding.tvTime
        val ll = binding.llHourly
        val  image = binding.image

    }
    private var details =IrrigationForecast()
    fun setList(listData: IrrigationForecast) {
        this.details = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemWeeklyIrrgationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeeklyAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener() {
            index = position
        }
        val properties = details

//        val inputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd-MM-yy", Locale.ENGLISH)
//        val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
//        val date: Date = inputDateFormatter.parse(properties.days[position])

//        for(i in properties.days.indices){
        val inputDateFormatter: SimpleDateFormat =
            SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
        val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        val date: Date = inputDateFormatter.parse(properties.days[position])

            holder.x.text = outputDateFormatter.format(date)

        val level = (properties.mad[position].toFloat()) - (properties.depletion[position].toFloat())
        if(level<=0) {
            holder.image.setImageResource(R.drawable.ic_irrigation_2)
            holder.ll.setBackgroundResource(R.drawable.green_border_irrigation)
            holder.x.setTextColor(Color.parseColor("#146133"))
        }
        else{
            holder.ll.setBackgroundResource(R.drawable.border_irrigation)
            holder.x.setTextColor(Color.parseColor("#000000"))
            holder.image.setImageResource(R.drawable.ic_irrigation_his2)
        }
    }

    override fun getItemCount(): Int {
        if(details.days.isNullOrEmpty())
            return 0
        return details.days.size
    }

}

