package com.example.soiltesting.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.R
import com.example.soiltesting.databinding.ItemSoilHistoryBinding
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.translations.TranslationsManager

class HistoryDataAdapter( private val statusTrackerListener: StatusTrackerListener) : RecyclerView.Adapter<HistoryDataHolder>() {

    var details = mutableListOf<SoilTestHistoryDomain>()
    fun setTrackerList(trackerList: List<SoilTestHistoryDomain>) {
        this.details = trackerList.toMutableList()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryDataHolder {
        val binding =
            ItemSoilHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryDataHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryDataHolder, position: Int) {
        val details = details[position]
        when (details.approve_status) {
            "0" -> {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#FFC24C"))
//                holder. binding.tvStatus.text = "Pending"
                TranslationsManager().loadString("pending",holder. binding.tvStatus,"Pending")
                holder. binding.ivStatus.setImageResource(R.drawable.ic_pending)
            }
            "1" -> {
                holder. binding.tvStatus.setTextColor(Color.parseColor("#1FB04B"))
//                holder. binding.tvStatus.text = "Accepted"
                TranslationsManager().loadString("txt_accepted",holder. binding.tvStatus,"Accepted")
                holder. binding.ivStatus.setImageResource(R.drawable.ic_completed)
            }
            "2" -> {
                //rejected
                holder. binding.tvStatus.setTextColor(Color.parseColor("#1FB04B"))
//                holder. binding.tvStatus.text = "Completed"
                TranslationsManager().loadString("txt_complete",holder. binding.tvStatus,"Completed")
                holder. binding.ivStatus.setImageResource(R.drawable.ic_completed)
            }
            "3" -> {
                //rejected
                holder. binding.tvStatus.setTextColor(Color.parseColor("#EC4544"))
//                holder. binding.tvStatus.text = "Rejected"
                TranslationsManager().loadString("txt_rejected",holder. binding.tvStatus,"Rejected")
                holder. binding.ivStatus.setImageResource(R.drawable.ic_rejected)
            }
        }



        holder.  binding.tvRequest.text = "Plot Number : " + details.plot_no
        holder.  binding.tvDesiessName.text = "Id : " + details.soil_test_number
        holder. binding.tvDate.text = details.updated_at
        TranslationsManager().loadString("view_status",holder.binding.tvViewStatus,"View Status")
        holder.binding.clTracker.setOnClickListener {
           statusTrackerListener.statusTracker(details)

        }
    }


    override fun getItemCount(): Int {
        return details.size
    }
    fun upDateList(list: ArrayList<SoilTestHistoryDomain>) {
//        details.clear()
//        details.addAll(list)
        details.clear()
        details.addAll(list)
        notifyDataSetChanged()

    }

}
class HistoryDataHolder( val binding: ItemSoilHistoryBinding) : RecyclerView.ViewHolder(binding.root) {


}