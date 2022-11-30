package com.example.soiltesting.ui.request

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.R
import com.example.soiltesting.databinding.ViewholderSoilReportStatusBinding
import com.waycool.data.Network.NetworkModels.ReportResult
import java.util.*

class SoilReportResultAdapter(context: Context, serviceType: List<ReportResult>) :
    RecyclerView.Adapter<SoilReportResultAdapter.MyViewHolder?>() {
    var context: Context
    var serviceType: List<ReportResult>
    var list: List<String> = ArrayList()
    var saclelist1 = Arrays.asList("High", "Acid", "Saline", "Highly Alkaline Low (OC,OM)")
    var saclelist3 = Arrays.asList("Medium", "Normal")
    var saclelist2 = Arrays.asList("Low", "Deficient")
    var saclelist4 = Arrays.asList("Neutral", "Sufficient", "Alkaline", "Slightly Acidic")
    var saclelist5 = Arrays.asList("Not selected")
    private val onBind = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ViewholderSoilReportStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: ReportResult = serviceType[position]
        if (data.value != null) {
            if (data.unit != null) {
                if (data.unit.equals("-")) {
                    holder.binding.statusValue.setText(data.value)
                } else {
                    holder.binding.statusValue.setText(
                        data.value.toString() + " " + data.unit
                    )
                }
            } else {
                holder.binding.statusValue.setText(data.value)
            }
        }
        if (data.name != null) holder.binding.compostName.setText(data.name)
        if (data.idealRange != null) holder.binding.compostRange.setText("Range: " + data.idealRange)
        if (data.recommendation != null) {
            holder.binding.recommendationLayout.setVisibility(View.VISIBLE)
            holder.binding.recommendationTv.setText(
                """Recommendation :
${data.recommendation .toString()} kg/ha"""
            )
        } else {
            holder.binding.recommendationLayout.setVisibility(View.VISIBLE)
            holder.binding.recommendationTv.setText(
                """
                    Recommendation :
                    NA
                    """.trimIndent()
            )
        }
        if (saclelist1.contains(data.rating)) {
            holder.binding.statusIcon.setBackgroundResource(R.drawable.ic_onp_scale1)
        }
        if (saclelist3.contains(data.rating)) {
            holder.binding.statusIcon.setBackgroundResource(R.drawable.ic_onp_scale3)
        }
        if (saclelist2.contains(data.rating)) {
            holder.binding.statusIcon.setBackgroundResource(R.drawable.ic_onp_scale2)
        }
        if (saclelist4.contains(data.rating)) {
            holder.binding.statusIcon.setBackgroundResource(R.drawable.ic_onp_scale4)
        }
        if (saclelist5.contains(data.rating)) {
            holder.binding.statusIcon.setBackgroundResource(R.drawable.ic_onp_scale6)
        }
    }

    override fun getItemCount(): Int {
        return serviceType.size
    }

    inner class MyViewHolder(b: ViewholderSoilReportStatusBinding) :
        RecyclerView.ViewHolder(b.getRoot()) {
        var binding: ViewholderSoilReportStatusBinding

        init {
            binding = b
        }
    }

    fun updateList(list: List<ReportResult>) {
        serviceType = list
        notifyDataSetChanged()
    }

    init {
        this.serviceType = serviceType
        this.context = context
    }
}