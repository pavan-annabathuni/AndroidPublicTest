package com.waycool.featurecrophealth.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.featurecrophealth.databinding.ViewholderHistoryBinding


class AiCropHistoryAdapter(private val context: Context) :
    ListAdapter<AiCropHistoryDomain, AiCropHistoryAdapter.NoteViewHolder>(ComparatorDiffUtil()) {
//    var details = mutableListOf<AiCropHistoryDomain>()

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

        @SuppressLint("SetTextI18n")
        fun bind(note: AiCropHistoryDomain) {
            TranslationsManager().loadString("txt_complete",binding.tvStatus,"Completed")
            binding.tvCropID.text = "id : " + note.id.toString()
            binding.tvDate.text = note.updated_at
            binding.tvRequest.text = note.cropName.toString()
            if(note.disease_name!=null){
            binding.tvDesiessName.text = note.disease_name}
            binding.tvStatus
            Glide.with(context)
                .load(note.image_url)
                .centerCrop()
                .placeholder(com.waycool.featurecrophealth.R.drawable.background_selected_item)
                .thumbnail(0.5f)
                .into(binding.iVHistory);
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(absoluteAdapterPosition))
            }
        }

    }
//
    fun upDateList(list: ArrayList<AiCropHistoryDomain>) {

        list.addAll(list)
        notifyDataSetChanged()

    }
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