package com.example.cropinformation.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropinformation.apiservice.response.Data
import com.example.cropinformation.apiservice.response.DataX

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<DataX>?) {
    val adapter = recyclerView.adapter as VideoAdapter
    adapter.submitList(data)

}

@BindingAdapter("imageUrl")
fun setImageUrl(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("http").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}

@BindingAdapter("listNewsData")
fun bindRecyclerView2(recyclerView: RecyclerView, data: List<DataX>?) {
    val adapter = recyclerView.adapter as NewsAdapter
    adapter.submitList(data)

}
