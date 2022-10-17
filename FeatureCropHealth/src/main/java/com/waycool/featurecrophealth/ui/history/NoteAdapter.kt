package com.waycool.featurecrophealth.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.featurecrophealth.databinding.ItemHistoryBinding
import com.waycool.featurecrophealth.model.historydata.Data


class NoteAdapter(private  val context: Context) :
    ListAdapter<Data, NoteAdapter.NoteViewHolder>(ComparatorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        note?.let {
            holder.bind(it)

        }
    }

    inner class NoteViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Data) {
            binding.tvCropID.text=note.crop_id.toString()
            binding.tvRequest.text=note.prediction
            binding.tvDesiessName.text=note.disease_id.toString()
            binding.tvDate.text=note.updated_at
            Glide.with(context)
                .load(note.image_url)
                .centerCrop()
                .placeholder(com.waycool.featurecrophealth.R.drawable.disease)
                .thumbnail(0.5f)
                .into(binding.iVHistory);
//            binding.title.text = note.title
//            binding.desc.text = note.description
            binding.root.setOnClickListener {

            }
        }

    }
//    fun upDateList(list: ArrayList<Data>) {
//
//        details.clear()
//        details.addAll(list)
//        notifyDataSetChanged()
//
//    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }
}