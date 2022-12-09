package com.waycool.iwap.premium

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.soiltesting.databinding.ItemFlexBoxAddFormBinding
import com.waycool.data.Network.NetworkModels.ViewDeviceDTO
import com.waycool.data.Network.NetworkModels.ViewDeviceData

import com.waycool.data.Network.NetworkModels.ViewIOTModel
import com.waycool.iwap.R


class ViewDeviceListAdapter(val viewDeviceFlexListener: ViewDeviceFlexListener) : RecyclerView.Adapter<ViewDeviceListViewHolder>()  {
    private var row_index = -1
    var itemsList = mutableListOf<ViewDeviceData>()
    private var selectedPosition = 0
    fun setMovieList(movies: ArrayList<ViewDeviceData>) {
        if (movies != null) {
            this.itemsList = movies.toMutableList()
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewDeviceListViewHolder {
        val binding =
            ItemFlexBoxAddFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewDeviceListViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewDeviceListViewHolder, position: Int) {
        val details = itemsList[position]
        holder.binding.skillName.text=details.model?.modelName
        if (position==0){
            viewDeviceFlexListener.viewDevice(details)
            holder.binding.skillName.setTextColor(Color.parseColor("#FFFFFF"))
        }
//        holder.binding.skillName.setOnClickListener {
//            viewDeviceFlexListener.viewDevice(details)
        if (row_index == position) {
            holder.binding.clTop.setBackgroundResource(com.waycool.featurecrophealth.R.drawable.btn_background)
            holder.binding.skillName.setTextColor(Color.parseColor("#FFFFFF"))
        }
        else {
            holder.binding.skillName.setTextColor(Color.parseColor("#111827"))
            holder.binding.clTop.setBackgroundResource(com.example.soiltesting.R.drawable.bd_flex)
        }
        holder.itemView.setOnClickListener {
            row_index=position
            notifyDataSetChanged()
            viewDeviceFlexListener.viewDevice(details)
        }
//        }
//        holder.binding.skillName.setOnClickListener { view ->
//            if (selectedPosition == position) {
//                holder.binding.skillName.setBackground(mContext.resources.getDrawable(R.drawable.bg_selected_item))
//            } else {
//                holder.binding.skillName.setBackground(mContext.resources.getDrawable(com.example.soiltesting.R.drawable.bd_flex))
//            }
//            selectedPosition = holder.adapterPosition
//            viewDeviceFlexListener.viewDevice(details)
////            checkSoilTestListener.checkBoxSoilTest(details)
//        }
//        val checkItem = holder.binding.ivCheck


    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


}
class ViewDeviceListViewHolder(val binding: ItemFlexBoxAddFormBinding) :
    RecyclerView.ViewHolder(binding.root) {
}