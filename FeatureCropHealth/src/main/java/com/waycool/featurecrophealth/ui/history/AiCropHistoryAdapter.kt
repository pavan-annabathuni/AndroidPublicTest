package com.waycool.featurecrophealth.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
import com.waycool.featurecrophealth.databinding.ViewholderHistoryBinding


class AiCropHistoryAdapter(private val context: Context) :
    ListAdapter<AiCropHistoryDomain, AiCropHistoryAdapter.NoteViewHolder>(ComparatorDiffUtil()) {

    var onItemClick: ((AiCropHistoryDomain?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            ViewholderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        note?.let {
            holder.bind(it)

        }
    }

    override fun getItemCount(): Int {

        return currentList.size
//        else currentList.size
    }

    inner class NoteViewHolder(private val binding: ViewholderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: AiCropHistoryDomain) {
            binding.tvCropID.text = "id : " + note.crop_id.toString()
//            binding.tvRequest.text = note.prediction
//            binding.tvDesiessName.text = note.disease_id.toString()
            binding.tvDate.text = note.updated_at
            binding.tvRequest.text = note.cropdata?.name.toString()
            Glide.with(context)
                .load(note.image_url)
                .centerCrop()
                .placeholder(com.waycool.featurecrophealth.R.drawable.disease)
                .thumbnail(0.5f)
                .into(binding.iVHistory);
//            binding.title.text = note.title
//            binding.desc.text = note.description
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(absoluteAdapterPosition))
            }
        }

    }
//
//    fun upDateList(list: ArrayList<AiCropHistoryDomain>) {
//        list.clear()
//        list.addAll(list)
//        notifyDataSetChanged()
//
//    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<AiCropHistoryDomain>() {
        override fun areItemsTheSame(
            oldItem: AiCropHistoryDomain,
            newItem: AiCropHistoryDomain
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AiCropHistoryDomain,
            newItem: AiCropHistoryDomain
        ): Boolean {
            return oldItem == newItem
        }
    }
}