package com.waycool.iwap.premium

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.utils.Constant
import com.waycool.data.Network.NetworkModels.FarmDetailsData
import com.waycool.iwap.R
import com.waycool.iwap.databinding.ItemDeltaProgressBarBinding

class DeltaTomAdapter(val context: Context) : RecyclerView.Adapter<DeltaTomHolder>() {
    var details = mutableListOf<FarmDetailsData>()
    fun setMovieList(movies: ArrayList<FarmDetailsData>?) {
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
        val details = details[position]
        holder.binding.nowTvSprayingVh.setText(
            Constant.changeDateFormatSpraying(
                details.Tomorrow[position].datetime
            )
        )
        if (position == details.Today.size - 1) {
            holder.binding.timeTvSprayingVh.text = "08 PM"
            holder.binding.timeTvSprayingVh.visibility = View.VISIBLE
        } else {
            holder.binding.timeTvSprayingVh.visibility = View.GONE
        }
        details.Tomorrow.forEach {
            if (it.condition.equals("ideal")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.green))
            } else if (it.condition.equals("good")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.yellow))
            } else if (it.condition.equals("bad")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.red))
            }
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