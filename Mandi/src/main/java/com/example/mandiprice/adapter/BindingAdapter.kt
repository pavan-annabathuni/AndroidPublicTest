package com.example.mandiprice.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.MandiHistoryDataDomain

//@BindingAdapter("listData")
//fun bindRecyclerView(recyclerView: RecyclerView, data: List<MandiDomainRecord>?) {
//    val adapter = recyclerView.adapter as DistanceAdapter
//    adapter.submitList(data)
//}
@BindingAdapter("historyListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MandiHistoryDataDomain>?) {
    val adapter = recyclerView.adapter as DateAdapter
    adapter.submitList(data)
}
@BindingAdapter("imageUrl")
fun setImageUrl(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
       // SvgLoader.pluck().with(imgView.context as Activity?).load(imgUri,imgView)
    }
}