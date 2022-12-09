package com.waycool.iwap.premium

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropinformation.databinding.ItemMycropsBinding
import com.example.soiltesting.databinding.ItemPremiumCropsBinding
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.iwap.databinding.ItemViewallServiceBinding

import java.util.ArrayList

class MyCropPremiumAdapter  :  RecyclerView.Adapter<MyCropPremiumViewHolder>(){
    var details = mutableListOf<MyCropDataDomain>()
    fun setMovieList(movies: ArrayList<MyCropDataDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCropPremiumViewHolder {
        val binding =
            ItemPremiumCropsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCropPremiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCropPremiumViewHolder, position: Int) {
        val details = details[position]
        holder.binding.tvAddDeviceStart.text=details.cropName.toString()
        holder.binding.tvCloudy.text=details.area.toString()
        Glide.with(holder.itemView.context).load(details.cropLogo).into(holder.binding.ivAddDeviceEnd)
//        holder.binding.tvCloudy.text=details.

    }

    override fun getItemCount(): Int {
       return details.size
    }
}
class MyCropPremiumViewHolder(val binding: ItemPremiumCropsBinding) :
    RecyclerView.ViewHolder(binding.root) {


}