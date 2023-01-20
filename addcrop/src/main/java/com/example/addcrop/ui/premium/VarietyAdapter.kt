package com.example.addcrop.ui.premium

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.addcrop.R
import com.example.addcrop.databinding.ItemSandBinding
import com.example.addcrop.ui.CategoryViewHolder

class VarietyAdapter(val varietyList:ArrayList<VariatyModel>,private val itemSelectedListener: ItemSelectedListener) : RecyclerView.Adapter<CategoryViewHolder>() {
//    ,private val itemSelectedListener: ItemSelectedListener
//    var varietyList = mutableListOf<VariatyModel>()
//    fun setMovieList(variety: List<VariatyModel>) {
//        this.varietyList = variety.toMutableList()
//        notifyDataSetChanged()
//    }
    var varietyListTwo = mutableListOf<GraphsModel>()
    fun setMovieListTwo(variety: List<GraphsModel>) {
        this.varietyListTwo = variety.toMutableList()
        notifyDataSetChanged()
    }
private var row_index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemSandBinding .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val details = varietyList[holder.layoutPosition]
        holder.binding.tvSand.setText(varietyList[position].name)
        if (row_index == holder.layoutPosition) {
            holder.binding.clSand.setBackgroundResource(R.drawable.bg_selected)
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
            row_index=holder.layoutPosition
            notifyDataSetChanged()
            itemSelectedListener.clickOnCategory(details)
            holder.binding.ngClick.visibility= View.GONE

        }



    }

    override fun getItemCount(): Int {
        return varietyList.size
    }
//    fun upDateList() {
//        row_index=-1
//        this.details = details
//        notifyDataSetChanged()
//
//    }
}
class CategoryViewHolder(val binding: ItemSandBinding) : RecyclerView.ViewHolder(binding.root) {


}