package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemHistoryDetailsBinding
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryDetailAdapter(val onClickListener:OnClickListener):
    ListAdapter<HistoricData, HistoryDetailAdapter.MyViewHolder>(HistoryAdapter) {
    var index:Int = 0
    class MyViewHolder(private val binding: ItemHistoryDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        val date = binding.textView16
        val irrigation = binding.irrigation
        val image = binding.imageView9
        val irrigationImage = binding.irrigationImage
        val view = binding.sideView
        val eto = binding.eto
        val rainfallValue = binding.textView2
        val eva = binding.textView19
        val rainfall = binding.textView4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HistoryDetailAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.itemView.setOnClickListener {
            index = position
            onClickListener.clickListener

        }
        var irrigated = ""
        GlobalScope.launch {
          irrigated   = TranslationsManager().getString("str_Irrigated ")
        }
        holder.date.text = properties.createdAt
        val inputDateFormatter: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMMM yy hh:mm a", Locale.ENGLISH)
        val date: Date = inputDateFormatter.parse(properties.createdAt)
        holder.date.text = "${outputDateFormatter.format(date)}"

        if(properties.irrigation!=null)
        holder.irrigation.text = "$irrigated ${properties.irrigation}L"
        else
            holder.irrigation.text = "0L"
        holder.eto.text = properties.etoCurrent.toString()

        if(!properties.rainfall.isNullOrEmpty())
        holder.rainfallValue.text = properties.rainfall.toString()
        else
            holder.rainfallValue.text = "0"

        if (properties.irrigation!=null && properties.irrigation?.toFloat()!!>0) {
            holder.irrigationImage.setImageResource(R.drawable.ic_irrigation_2)
            holder.image.setImageResource(R.drawable.ic_holo_green)
            holder.date.setTextColor(Color.parseColor("#146133"))
        } else {
            holder.irrigationImage.setImageResource(R.drawable.ic_irrigation_his2)
            holder.image.setImageResource(R.drawable.ic_holo_gray)
            holder.date.setTextColor(Color.parseColor("#070D09"))
        }
        //translation
        TranslationsManager().loadString("str_evapotranspiration",holder.eva)
        TranslationsManager().loadString("str_rainfall",holder.rainfall)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<HistoricData>() {

        override fun areItemsTheSame(
            oldItem: HistoricData,
            newItem: HistoricData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HistoricData,
            newItem: HistoricData
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnClickListener(val clickListener: (data: HistoricData) -> Unit) {
        fun onClick(data: HistoricData) = clickListener(data)
    }
}