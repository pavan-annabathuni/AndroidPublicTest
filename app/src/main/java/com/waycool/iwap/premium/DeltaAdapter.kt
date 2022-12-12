package com.waycool.iwap.premium

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemAddDeviceBinding
import com.example.soiltesting.databinding.ItemPremiumCropsBinding
import com.waycool.data.Network.NetworkModels.FarmDetailsData
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.iwap.databinding.ItemDeltaProgressBarBinding
import java.util.ArrayList

class DeltaAdapter : RecyclerView.Adapter<DeltaViewHolder>() {
    var details = mutableListOf<FarmDetailsData>()
    fun setMovieList(movies: ArrayList<FarmDetailsData>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeltaViewHolder {
        val binding =
            ItemDeltaProgressBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeltaViewHolder(binding)

    }

    override fun onBindViewHolder(holder: DeltaViewHolder, position: Int) {
        val details = details[position]
        holder.binding.timeOne.text=details.Today[0].datetime.toString()

    }

    override fun getItemCount(): Int {
       return details.size
    }

}

class DeltaViewHolder(val binding: ItemDeltaProgressBarBinding) :
    RecyclerView.ViewHolder(binding.root) {
}