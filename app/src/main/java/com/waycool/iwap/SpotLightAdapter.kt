package com.waycool.iwap

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.iwap.databinding.ItemSpotlightBinding

class SpotLightAdapter(val context: Context, val imageList: List<Int>) : RecyclerView.Adapter<SpotLightAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemSpotlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val img = binding.idIVImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSpotlightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SpotLightAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setImageResource(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
