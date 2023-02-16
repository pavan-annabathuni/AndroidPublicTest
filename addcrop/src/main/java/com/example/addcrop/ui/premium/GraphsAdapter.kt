package com.example.addcrop.ui.premium

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.addcrop.R
import com.example.addcrop.databinding.ItemSandBinding
import com.example.addcrop.ui.CategoryViewHolder

class GraphsAdapter (val varietyList:ArrayList<GraphsModel>,private val itemGraphsClicked: ItemGraphsClicked) : RecyclerView.Adapter<GraphsViewHolder>() {
    private var row_index = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphsViewHolder {
        val binding =
            ItemSandBinding .inflate(LayoutInflater.from(parent.context), parent, false)
        return GraphsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GraphsViewHolder, position: Int) {
        val details = varietyList[holder.layoutPosition]
        holder.binding.tvSand .text = details.name
        if (row_index == holder.layoutPosition) {
            holder.binding.clSand.setBackgroundResource(com.waycool.uicomponents.R.drawable.bg_search)
            holder.binding.ngClick.visibility= View.VISIBLE
        }
        else {
            holder.binding.clSand.setBackgroundResource(R.drawable.item_unselected)
            holder.binding.ngClick.visibility= View.GONE
        }

        holder.binding.clSand .setOnClickListener {
            row_index=holder.layoutPosition
            notifyDataSetChanged()
            itemGraphsClicked.clickGraphs(details)
//            itemSelectedListener.clickOnCategory(details.name)
            holder.binding.ngClick.visibility= View.GONE

        }
    }

    override fun getItemCount(): Int {
     return varietyList.size
    }

}
class GraphsViewHolder(val binding: ItemSandBinding) : RecyclerView.ViewHolder(binding.root) {


}