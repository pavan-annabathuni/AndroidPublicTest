package com.waycool.featurecrophealth.ui.details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.ItemFlexBinding

class CategoryAdapter(private val categoryListener: CategoryListener): RecyclerView.Adapter<CategoryViewHolder>() {
    private var row_index = -1
    var details = mutableListOf<com.waycool.featurecrophealth.model.cropcate.Data>()
    fun setMovieList(movies: List<com.waycool.featurecrophealth.model.cropcate.Data>) {
        this.details = movies.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemFlexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val details = details[position]
        holder.binding.skillName .text = details.category_name
        if (row_index == position) {
            holder.binding.clTop.setBackgroundResource(R.drawable.btn_background)
            holder.binding.skillName.setTextColor(Color.parseColor("#FFFFFF"))
        }
        else {
            holder.binding.skillName.setTextColor(Color.parseColor("#111827"))
            holder.binding.clTop.setBackgroundResource(R.drawable.bd_flex)


        }
        holder.itemView.setOnClickListener {
            row_index=position
            notifyDataSetChanged()
            categoryListener.clickOnCategory(details)

        }




    }

    override fun getItemCount(): Int {
        return details.size
    }
}
class CategoryViewHolder(val binding: ItemFlexBinding) : RecyclerView.ViewHolder(binding.root) {


}