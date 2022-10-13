package com.waycool.featurecrophealth.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.featurecrophealth.databinding.ItemVideoBinding
import com.waycool.featurecrophealth.model.WeatherResponseItem

class CropHealthVideoAdapter :RecyclerView.Adapter<VideoHolder>() {
    var videoList = mutableListOf<WeatherResponseItem>()
    fun setMovies(video: List<WeatherResponseItem>) {
        this.videoList = video.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoBinding.inflate(inflater, parent, false)
        return VideoHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val movie = videoList[position]
    }

    override fun getItemCount(): Int {
       return videoList.size
    }

}
class VideoHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {

}