package com.example.soiltesting.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemSoilHistoryBinding
import com.example.soiltesting.model.history.Data

class SoilHistoryAdapter(
    private val context: Context,
    private val statusTrackerListener: StatusTrackerListener
) :
    ListAdapter<Data, SoilHistoryAdapter.NoteViewHolder>(ComparatorDiffUtil()) {
//    var details = mutableListOf<Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            ItemSoilHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        note?.let {
//            holder.bind(it)
        }
    }

    inner class NoteViewHolder(private val binding: ItemSoilHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        @SuppressLint("ResourceAsColor")
//        fun bind(note: SoilTestHistoryDomain) {
//            when (note.status.length) {
//                7 -> {
//                    //pending
//                    binding.tvStatus.setTextColor(Color.parseColor("#FFC24C"))
//                    binding.tvStatus.text = note.status
////                    binding.ivStatus.setBackground(R.drawable.ic_pending)
//                    binding.ivStatus.setImageResource(R.drawable.ic_pending)
//
//                }
//                8 -> {
//                    //rejected
//                    binding.tvStatus.setTextColor(Color.parseColor("#EC4544"))
//                    binding.tvStatus.text = note.status
//                    binding.ivStatus.setImageResource(R.drawable.ic_rejected)
//                }
//                9 -> {
//                    //Completed
//                    binding.tvStatus.setTextColor(Color.parseColor("#1FB04B"))
//                    binding.tvStatus.text = note.status
//                    binding.ivStatus.setImageResource(R.drawable.ic_completed)
//
//
//                }
//            }
//            binding.tvRequest.text = "Plot Number : " + note.plot_no
//            binding.tvDesiessName.text = "Id : " + note.soil_test_number
//            binding.tvDate.text = note.updated_at
////            binding.clTracker.setOnClickListener {
////                statusTrackerListener.statusTracker(note)
////
////            }
//
//        }


    }




}

class ComparatorDiffUtil : DiffUtil.ItemCallback<Data>() {
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem == newItem
    }
}


