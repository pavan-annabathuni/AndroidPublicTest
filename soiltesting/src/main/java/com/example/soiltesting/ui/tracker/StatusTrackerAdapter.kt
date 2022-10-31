package com.example.soiltesting.ui.tracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.R
import com.example.soiltesting.databinding.ItemStatusTrackerBinding
import com.example.soiltesting.utils.Constant.TAG
import com.waycool.data.repository.domainModels.TrackerDemain
import java.text.SimpleDateFormat

class StatusTrackerAdapter(val feedbackListerner: FeedbackListerner) : RecyclerView.Adapter<StatusTrackerHolder>() {

    var details = mutableListOf<TrackerDemain>()
    fun setMovieList(movies: List<TrackerDemain>) {
        this.details = movies.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusTrackerHolder {
        val binding =
            ItemStatusTrackerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusTrackerHolder(binding)
    }

    @SuppressLint("ResourceAsColor", "SimpleDateFormat")
    override fun onBindViewHolder(holder: StatusTrackerHolder, position: Int) {
        val details = details[position]
        if (details.is_approved == 0 && details.date.toString().isNotEmpty()) {
            holder.binding.mcvCircle.setImageResource(R.drawable.ic_pending_status)
            holder.binding.tvTitle.text = details.title
            holder.binding.viewTracker .background.setColorFilter(Color.parseColor("#1FB04B"), PorterDuff.Mode.DARKEN)
        } else if (details.is_approved == 1 && details.date.toString().isNotEmpty()) {
            holder.binding.tvTitle.text = details.title
            holder.binding.viewTracker .background.setColorFilter(Color.parseColor("#1FB04B"), PorterDuff.Mode.DARKEN)
            holder.binding.mcvCircle.setImageResource(R.drawable.ic_status_completed)
        } else if (details.is_approved ==2  && details.date.toString().isNotEmpty()) {
            holder.binding.tvTitle.text = details.title
            holder.binding.viewTracker .background.setColorFilter(Color.parseColor("#1FB04B"), PorterDuff.Mode.DARKEN)
            holder.binding.mcvCircle.setImageResource(R.drawable.ic_rejected_status)
        }
        if (details.title!!.equals("Report Generated")){
            holder.binding.viewTracker.visibility=View.GONE
            if (details.date!=null){
                feedbackListerner.feedbackApiListener(details)
            }
        }
        holder.binding.tvTitle.text = details.title
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        val formatter = SimpleDateFormat("dd.MM.yyyy ,HH:mm")


        if (details.date ==null){
            holder.binding.tvTitle.text = details.title
            Log.d(TAG, "onBindViewHolderDate: ${details.date}")

        }
       else  {
            val output: String? = formatter.format(parser.parse(details.date?.toString()))
            holder.binding.tvDate.text = output
        }
    }

    override fun getItemCount(): Int {

        return details.size


    }
}

class StatusTrackerHolder(val binding: ItemStatusTrackerBinding) :
    RecyclerView.ViewHolder(binding.root) {


}