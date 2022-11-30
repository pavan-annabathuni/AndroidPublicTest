package com.waycool.addfarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.waycool.uicomponents.databinding.ViewholderPlacePredictionBinding


class PlacesListAdapter :
    ListAdapter<AutocompletePrediction, PlacesListAdapter.CropsListVH>(DiffCallback) {

    var onItemClick: ((AutocompletePrediction) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropsListVH {
        val binding: ViewholderPlacePredictionBinding = ViewholderPlacePredictionBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return CropsListVH(binding)
    }

    override fun onBindViewHolder(holder: CropsListVH, position: Int) {
        holder.bind(getItem(position))
        holder.binding.root.setOnClickListener {
            onItemClick?.invoke(getItem(position))
        }
    }


    inner class CropsListVH(val binding: ViewholderPlacePredictionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: AutocompletePrediction) {
            binding.textViewTitle.text = place.getPrimaryText(null)
            binding.textViewAddress.text = place.getSecondaryText(null)

        }
    }

    override fun getItemCount(): Int {
        if (currentList.size >= 4)
            return 4
        else
            currentList.size
        return super.getItemCount()
    }


    companion object DiffCallback : DiffUtil.ItemCallback<AutocompletePrediction>() {

        override fun areItemsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem.placeId == newItem.placeId
        }
    }

}
