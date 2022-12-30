package com.waycool.iwap.premium

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.iwap.databinding.ItemPremiumCropsBinding

import java.util.ArrayList

class MyCropPremiumAdapter(val myCropListener: myCropListener) :
    RecyclerView.Adapter<MyCropPremiumViewHolder>() {
    var details = mutableListOf<MyCropDataDomain>()
    var farmsList: MutableList<MyFarmsDomain> = mutableListOf()
    fun setMovieList(movies: ArrayList<MyCropDataDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }


    fun updateMyfarms(movies: List<MyFarmsDomain>?) {
        farmsList.clear()
        if (movies != null) {
            farmsList.addAll(movies)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCropPremiumViewHolder {
        val binding =
            ItemPremiumCropsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCropPremiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCropPremiumViewHolder, position: Int) {
        val myCrop = details[position]
        holder.binding.tvAddDeviceStart.text = myCrop.cropName.toString()
        holder.binding.tvCloudy.text = myCrop.area.toString()
        holder.binding.tvEnableAddDevice.text = myCrop.plotNickname ?: "NA"
        if (myCrop.farmId == null) {
            holder.binding.ivFeedback.text = "Not associated to farm"
        } else {
            val farmName=farmsList.firstOrNull { it.id==myCrop.farmId }
            holder.binding.ivFeedback.text = "${farmName?.farmName?:"Not associated to farm"}"
        }

        Glide.with(holder.itemView.context).load(myCrop.cropLogo)
            .into(holder.binding.ivAddDeviceEnd)
        holder.binding.tvCloudy.isSelected = true
        if (myCrop.irrigationRequired == true) {
            holder.binding.tvCloudy.text = "Irrigation required"
        } else {
            holder.binding.tvCloudy.text = "Irrigation not required"

        }

        holder.binding.tvCloudyNoDisease.isSelected = true
        if (myCrop.disease == true) {
            holder.binding.tvCloudyNoDisease.text = "Chances of multiple diseases"
        } else {
            holder.binding.tvCloudyNoDisease.text = "No diseases alerts today"

        }

//        holder.binding.tvCloudy.text=details.

        holder.binding.cardAddDevice.setOnClickListener {
            myCropListener.myCropListener(myCrop)
        }

    }

    override fun getItemCount(): Int {
        return details.size
    }
}

class MyCropPremiumViewHolder(val binding: ItemPremiumCropsBinding) :
    RecyclerView.ViewHolder(binding.root) {


}