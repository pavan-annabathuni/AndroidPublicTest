package com.waycool.iwap.premium

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemPremiumAddFarmBinding
import com.example.soiltesting.databinding.ItemPremiumCropsBinding
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import java.util.ArrayList

class MyFarmPremiumAdapter :  RecyclerView.Adapter<MyFarmPremiumViewHolder>() {
    var details = mutableListOf<MyFarmsDomain>()
    fun setMovieList(movies: ArrayList<MyFarmsDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFarmPremiumViewHolder {
        val binding =
            ItemPremiumAddFarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFarmPremiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyFarmPremiumViewHolder, position: Int) {
        val details = details[position]
        holder.binding.tvAddDeviceStart.text=details.farmName

    }

    override fun getItemCount(): Int {
        return details.size
    }

}
class MyFarmPremiumViewHolder(val binding: ItemPremiumAddFarmBinding) :
    RecyclerView.ViewHolder(binding.root) {
}