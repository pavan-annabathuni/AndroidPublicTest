package com.example.weather.adapters

import android.widget.ImageView
import androidx.core.net.ParseException
import androidx.core.net.toUri
import androidx.core.view.size
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.apiService.apiResponse.Daily
import com.example.weather.apiService.apiResponse.Hourly
import java.text.SimpleDateFormat


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Daily>?) {
    val adapter = recyclerView.adapter as WeatherAdapter
    adapter.submitList(data)

}
@BindingAdapter("hourlyData")
fun bindRecyclerViewHourly(recyclerView: RecyclerView, data2: List<Hourly>?) {
    val adapter = recyclerView.adapter as HourlyAdapter
    adapter.submitList(data2)

}
@BindingAdapter("imageUrl")
fun setImageUrl(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}