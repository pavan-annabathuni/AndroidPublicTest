package com.waycool.iwap.premium

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemFlexBoxAddFormBinding

import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.iwap.R

class ViewDeviceListAdapter(val viewDeviceFlexListener: ViewDeviceFlexListener) : RecyclerView.Adapter<ViewDeviceListViewHolder>()  {
    private var row_index = 0
    var itemsList = mutableListOf<ViewDeviceDomain>()
    private var selectedPosition = 0
    fun setMovieList(movies: List<ViewDeviceDomain>) {
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
        val details = itemsList[holder.layoutPosition]
        holder.binding.skillName.text=details.modelName

        if (row_index == holder.layoutPosition) {
            holder.binding.clTop.setBackgroundResource(R.drawable.bg_selected_item)
            holder.binding.skillName.setTextColor(Color.parseColor("#FFFFFF"))
            viewDeviceFlexListener.viewDevice(details)

        }
        else {
            holder.binding.skillName.setTextColor(Color.parseColor("#111827"))
            holder.binding.clTop.setBackgroundResource(com.waycool.uicomponents.R.drawable.bd_flex)

        }
        holder.binding.skillName.setOnClickListener {
            row_index=holder.layoutPosition
            notifyDataSetChanged()
            viewDeviceFlexListener.viewDevice(details)
        }




    }
    fun upDateList() {
        selectedPosition=-1
        this.itemsList = itemsList
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


}
class ViewDeviceListViewHolder(val binding: ItemFlexBoxAddFormBinding) :
    RecyclerView.ViewHolder(binding.root) {
}