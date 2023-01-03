package com.waycool.weather.adapters

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.weather.DailyDomain
import com.waycool.data.repository.domainModels.weather.HourlyDomain


@SuppressLint("NotifyDataSetChanged")
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<DailyDomain>?) {
    val adapter = recyclerView.adapter as WeatherAdapter
    if (!data.isNullOrEmpty()){
        adapter.submitList(data.subList(1,data.size))
    }else
//    adapter.notifyDataSetChanged()
    adapter.submitList(data)

}

@BindingAdapter("hourlyData")
fun bindRecyclerViewHourly(recyclerView: RecyclerView, data2: List<HourlyDomain>?) {
    val adapter = recyclerView.adapter as HourlyAdapter
    adapter.submitList(data2)

}

@BindingAdapter("imageUrl")
fun setImageUrl(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}