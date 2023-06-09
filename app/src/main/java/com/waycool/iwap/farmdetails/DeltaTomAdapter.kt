package com.waycool.iwap.premium

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.utils.Constant
import com.waycool.data.Network.NetworkModels.DeltaT
import com.waycool.iwap.R
import com.waycool.iwap.databinding.ItemDeltaProgressBarBinding

class DeltaTomAdapter(val context: Context) : RecyclerView.Adapter<DeltaTomHolder>() {
    var details = mutableListOf<DeltaT>()
    fun setMovieList(movies: List<DeltaT>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeltaTomHolder {
        val binding =
            ItemDeltaProgressBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeltaTomHolder(binding)
    }

    override fun onBindViewHolder(holder: DeltaTomHolder, position: Int) {
        val detail = details[position]
        holder.binding.nowTvSprayingVh.setText(
            Constant.changeDateFormatSpraying(
                detail.datetime
            )
        )
        if (position == details.size - 1) {
            holder.binding.timeTvSprayingVh.text = "8 PM"
            holder.binding.timeTvSprayingVh.visibility = View.VISIBLE
        } else {
            holder.binding.timeTvSprayingVh.visibility = View.GONE
        }

            if (detail.condition.equals("ideal")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.green))
            } else if (detail.condition.equals("good")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.yellow))
            } else if (detail.condition.equals("bad")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.red))
            }

    }

        override fun getItemCount(): Int {
//            details.size
            return details.size
        }
    }

    class DeltaTomHolder(val binding: ItemDeltaProgressBarBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }