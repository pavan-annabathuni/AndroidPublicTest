package com.example.soiltesting.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.R
import com.example.soiltesting.databinding.ItemSoilHistoryBinding
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain

class HistoryDataAdapter( private val statusTrackerListener: StatusTrackerListener) : RecyclerView.Adapter<HistoryDataHolder>() {

    var details = mutableListOf<SoilTestHistoryDomain>()
    fun setMovieList(movies: List<SoilTestHistoryDomain>) {
        this.details = movies.toMutableList()
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
                holder. binding.tvStatus.text = "Pending"
    //                    binding.ivStatus.setBackground(R.drawable.ic_pending)
                holder. binding.ivStatus.setImageResource(R.drawable.ic_pending)
            }
            "1" -> {
                holder. binding.tvStatus.setTextColor(Color.parseColor("#1FB04B"))
                holder. binding.tvStatus.text = "Accepted"
                holder. binding.ivStatus.setImageResource(R.drawable.ic_completed)
            }
            "2" -> {
                //rejected
                holder. binding.tvStatus.setTextColor(Color.parseColor("#1FB04B"))
                holder. binding.tvStatus.text = "Completed"
                holder. binding.ivStatus.setImageResource(R.drawable.ic_completed)
            }
            "3" -> {
                //rejected
                holder. binding.tvStatus.setTextColor(Color.parseColor("#EC4544"))
                holder. binding.tvStatus.text = "Rejected"
                holder. binding.ivStatus.setImageResource(R.drawable.ic_rejected)
            }
        }


//        when (details.status.length) {
//            7 -> {
//                //pending
//               holder.binding.tvStatus.setTextColor(Color.parseColor("#FFC24C"))
//                holder. binding.tvStatus.text = details.status
////                    binding.ivStatus.setBackground(R.drawable.ic_pending)
//                holder. binding.ivStatus.setImageResource(R.drawable.ic_pending)
//
//            }
//            8 -> {
//                //rejected
//                holder. binding.tvStatus.setTextColor(Color.parseColor("#EC4544"))
//                holder.   binding.tvStatus.text = details.status
//                holder. binding.ivStatus.setImageResource(R.drawable.ic_rejected)
//            }
//            9 -> {
//                //Completed
//                holder. binding.tvStatus.setTextColor(Color.parseColor("#1FB04B"))
//                holder.  binding.tvStatus.text = details.status
//                holder. binding.ivStatus.setImageResource(R.drawable.ic_completed)
//
//
//            }
//        }
        holder.  binding.tvRequest.text = "Plot Number : " + details.plot_no
        holder.  binding.tvDesiessName.text = "Id : " + details.soil_test_number
        holder. binding.tvDate.text = details.updated_at
        holder.   binding.clTracker.setOnClickListener {
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