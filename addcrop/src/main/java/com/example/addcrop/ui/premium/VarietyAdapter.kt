package com.example.addcrop.ui.premium

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.addcrop.R
import com.example.addcrop.databinding.ItemSandBinding
import com.example.addcrop.ui.CategoryViewHolder
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.repository.domainModels.VarietyCropDomain

class VarietyAdapter(private val itemSelectedListener: ItemSelectedListener) : RecyclerView.Adapter<CropVarietyViewHolder>() {

    var details = mutableListOf<VarietyCropDomain>()
    private var row_index = -1
    fun setCropVariety(cropVariety: List<VarietyCropDomain>) {
        this.details = cropVariety.toMutableList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropVarietyViewHolder {
        val binding =
            ItemSandBinding .inflate(LayoutInflater.from(parent.context), parent, false)
        return CropVarietyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CropVarietyViewHolder, position: Int) {
        val details = details[position]
        holder.binding.tvSand.text=details.variety
        if (row_index == position) {
            holder.binding.clSand.setBackgroundResource(com.waycool.uicomponents.R.drawable.bg_search)
            holder.binding.ngClick.visibility= View.VISIBLE
//            holder.binding.skillName.setTextColor(Color.parseColor("#FFFFFF"))
        }
        else {
            holder.binding.clSand.setBackgroundResource(R.drawable.item_unselected)
            holder.binding.ngClick.visibility= View.GONE
//            holder.binding.skillName.setTextColor(Color.parseColor("#111827"))
//            holder.binding.clTop.setBackgroundResource(R.drawable.bd_flex)
        }
//
        holder.binding.clSand .setOnClickListener {
            row_index=position
            notifyDataSetChanged()
            itemSelectedListener.clickOnCategory(details)
            holder.binding.ngClick.visibility= View.GONE

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
class CropVarietyViewHolder(val binding: ItemSandBinding) : RecyclerView.ViewHolder(binding.root) {


}