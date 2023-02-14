package com.example.addcrop.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.addcrop.R
import com.example.addcrop.databinding.ItemSandBinding
import com.example.addcrop.ui.selectcrop.AddCropItemClick
import com.waycool.data.repository.domainModels.SoilTypeDomain

class CategoryAdapter(private val addCropItemClick: AddCropItemClick) : RecyclerView.Adapter<CategoryViewHolder>()  {
    var details = mutableListOf<SoilTypeDomain>()
    private var row_index = -1
    fun setSoilTypeList(soilTypeList: List<SoilTypeDomain>) {
        this.details = soilTypeList.toMutableList()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemSandBinding .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val details = details[position]
        holder.binding.tvSand .text = details.soil_type
        if (row_index == position) {
            holder.binding.clSand.setBackgroundResource(com.waycool.uicomponents.R.drawable.bg_search)
            holder.binding.ngClick.visibility=View.VISIBLE
//            holder.binding.skillName.setTextColor(Color.parseColor("#FFFFFF"))
        }
        else {
            holder.binding.clSand.setBackgroundResource(R.drawable.item_unselected)
            holder.binding.ngClick.visibility=View.GONE
//            holder.binding.skillName.setTextColor(Color.parseColor("#111827"))
//            holder.binding.clTop.setBackgroundResource(R.drawable.bd_flex)
        }

        holder.binding.clSand .setOnClickListener {
            row_index=position
            notifyDataSetChanged()
            addCropItemClick.clickOnCategory(details)
            holder.binding.ngClick.visibility=View.GONE

        }

    }


    override fun getItemCount(): Int {
        return details.size

    }
    fun upDateList() {
        row_index=-1
        this.details = details
        notifyDataSetChanged()

    }
}
class CategoryViewHolder(val binding: ItemSandBinding) : RecyclerView.ViewHolder(binding.root) {


}