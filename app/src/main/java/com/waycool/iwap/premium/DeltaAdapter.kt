package com.waycool.iwap.premium

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.utils.Constant.changeDateFormatSpraying
import com.waycool.data.Network.NetworkModels.FarmDetailsData
import com.waycool.iwap.R
import com.waycool.iwap.databinding.ItemDeltaProgressBarBinding

class DeltaAdapter(val context:Context) : RecyclerView.Adapter<DeltaViewHolder>() {
    var details = mutableListOf<FarmDetailsData>()
    fun setMovieList(movies: ArrayList<FarmDetailsData>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeltaViewHolder {
        val binding =
            ItemDeltaProgressBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeltaViewHolder(binding)

    }

    override fun onBindViewHolder(holder: DeltaViewHolder, position: Int) {
        val details = details[position]
//        holder.binding.timeTvSprayingVh.text=details.Today[position].datetime.toString()
//        holder.binding.nowTvSprayingVh.setText(
//            changeDateFormatSpraying(
//               details.Today[position].datetime
//            )
//        )
//
//        if (position == details.Today.size) {
//            holder.binding.timeTvSprayingVh.text = "08 PM"
//            holder.binding.timeTvSprayingVh.visibility = View.VISIBLE
//        } else {
//            holder.binding.timeTvSprayingVh.visibility = View.GONE
//        }

        holder.binding.nowTvSprayingVh.setText(
            changeDateFormatSpraying(
               details.Today[position].datetime
            )
        )


        if (position == details.Today.size - 1) {
            holder.binding.timeTvSprayingVh .setText("08 PM")
            holder.binding.timeTvSprayingVh.setVisibility(View.VISIBLE)
        } else {
            holder.binding.timeTvSprayingVh.setVisibility(View.GONE)
        }
//        holder.hrsTv.setText(HelperFunctions.changeDateFormatSpraying(sprayingList.get(position).getTime()));
        details.Today.forEach {
            if ( it.condition.equals("ideal")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.green))
            } else if (it.condition.equals("good")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.yellow))
            } else if (it.condition.equals("bad")) {
                holder.binding.statusViewSprayingVh.setBackgroundColor(context.resources.getColor(R.color.red))
            }
        }


    }

    override fun getItemCount(): Int {
       return details.size
    }

}

class DeltaViewHolder(val binding: ItemDeltaProgressBarBinding) :
    RecyclerView.ViewHolder(binding.root) {
}