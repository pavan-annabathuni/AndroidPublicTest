package com.example.soiltesting.ui.request

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ViewholderOnpSoilReportRecommendationBinding
import com.waycool.data.Network.NetworkModels.Recommendation

class SoilReportRecommendationAdapter(context: Context, serviceType: List<Recommendation>) :
    RecyclerView.Adapter<SoilReportRecommendationAdapter.MyViewHolder?>() {
    var context: Context
    var serviceType: List<Recommendation>
    var list: List<String> = ArrayList()
    private val onBind = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ViewholderOnpSoilReportRecommendationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Recommendation = serviceType[position]
        holder.binding.recommendationText.setText(
            data.fertilizer.toString() + " - " + data.quantity
        )
    }

    override fun getItemCount(): Int {
        return serviceType.size
    }

    inner class MyViewHolder(b: ViewholderOnpSoilReportRecommendationBinding) :
        RecyclerView.ViewHolder(b.getRoot()) {
        var binding: ViewholderOnpSoilReportRecommendationBinding

        init {
            binding = b
        }
    }

    init {
        this.serviceType = serviceType
        this.context = context
    }
}
