package com.example.irrigationplanner.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.example.irrigationplanner.databinding.ItemHistoryDetailsBinding
import com.waycool.data.Network.NetworkModels.HistoricData
import com.waycool.data.translations.TranslationsManager

class HistoryDetailAdapter(val onClickListener:OnClickListener):
    ListAdapter<HistoricData, HistoryDetailAdapter.MyViewHolder>(HistoryAdapter) {
    var index:Int = 0
    class MyViewHolder(private val binding: ItemHistoryDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        val date = binding.textView16
        val irrigation = binding.irrigation
        val image = binding.imageView9
        val view = binding.sideView
        val eto = binding.eto
        val etc = binding.textView2
        val eva = binding.textView19
        val rainfall = binding.textView4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HistoryDetailAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.itemView.setOnClickListener() {
            index = position
            onClickListener.clickListener

        }
        holder.date.text = properties.createdAt
        holder.irrigation.text = "Irrigated ${properties.irrigation}L"
        holder.eto.text = properties.etoCurrent.toString()
        holder.etc.text = properties.etc.toString()

        if (properties.irrigation?.toFloat()!!>0) {
            holder.view.setBackgroundResource(R.color.DarkGreen)
            holder.image.setImageResource(R.drawable.ic_holo_green)
            holder.date.setTextColor(Color.parseColor("#146133"))
        } else {
            holder.image.setImageResource(R.drawable.ic_irrigation_his2)
            holder.view.setBackgroundResource(R.color.LightGray)
            holder.image.setImageResource(R.drawable.ic_holo_gray)
            holder.date.setTextColor(Color.parseColor("#070D09"))
        }
        //translation
        TranslationsManager().loadString("str_evapotranspiration",holder.eva)
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