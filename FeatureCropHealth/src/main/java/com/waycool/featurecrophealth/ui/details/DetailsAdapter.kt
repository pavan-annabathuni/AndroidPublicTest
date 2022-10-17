package com.waycool.featurecrophealth.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.featurecrophealth.DetailsCLickLIstener
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.ItemSelectCropBinding
import com.waycool.featurecrophealth.model.cropdetails.Data
import com.waycool.featurecrophealth.utils.NetworkUtils.loadUrl


class DetailsAdapter(private val detailsCLickLIstener: DetailsCLickLIstener) :
    RecyclerView.Adapter<DetailViewHolder>() {
    var details = mutableListOf<Data>()
    fun setMovieList(movies: List<Data>) {
        this.details = movies.toMutableList()
        notifyDataSetChanged()
    }

    //    lateinit var mlistener:onItemClickListener
//    interface onItemClickListener{
//        fun onItemClick(position: Data)
//
//    }
//    fun setOnClick(listener:onItemClickListener){
//        mlistener=listener
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {

        val binding =
            ItemSelectCropBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val details = details[position]
        holder.binding.tvTitile.text = details.crop_name
        holder.binding.ivMyCrop.loadUrl(details.crop_logo)

//        loadImageToImageView(holder.binding.ivMyCrop, details.crop_logo)

//        Glide.with(holder.itemView.context)
//            .load(details.crop_logo)
//            .centerCrop()
//            .placeholder(com.example.featurecrophealth.R.drawable.disease)
//            .thumbnail(0.5f)
//            .into(holder.binding.ivMyCrop);


//      val   requestBuilder = Glide.with(holder.itemView.context).  using(
//                Glide.buildStreamModelLoader(Uri::class.java, holder.itemView.context),
//                InputStream::class.java
//            )
//            .from(Uri::class.java)
//            .`as`(SVG::class.java)
//            .transcode(SvgDrawableTranscoder(), PictureDrawable::class.java)
//            .sourceEncoder(StreamEncoder())
//            .cacheDecoder(FileToStreamDecoder<SVG>(SvgDecoder()))
//            .decoder(SvgDecoder())
//            .placeholder(com.example.featurecrophealth.R.drawable.disease)
//            .error(R.drawable.ic_web)
//            .animate(android.R.anim.fade_in)
//            .listener(SvgSoftwareLayerSetter<Uri>())
//        val uri = Uri.parse("http://upload.wikimedia.org/wikipedia/commons/e/e8/Svg_example3.svg")
//        requestBuilder
//            .diskCacheStrategy(AnnotationRetention.SOURCE) // SVG cannot be serialized so it's not worth to cache it
//            .load(uri)
//            .into(mImageView)
//
//        val options: RequestOptions = RequestOptions()
//            .centerCrop()
//            .transform(CircleCrop())
//            .override(
//                holder.binding.ivMyCrop.getWidth(),
//                holder.binding.ivMyCrop.getHeight()
//            )
//            .placeholder(R.drawable.disease)
//            .error(R.drawable.disease)
//
//        Glide.with(holder.itemView.context)
//            .load(details.crop_logo)
//            .thumbnail(0.25f)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .apply(options)
//            .into(holder.binding.ivMyCrop)


//        Glide.with(holder.itemView.context)
//            .load(details.crop_logo)
//            .into(holder.binding.ivMyCrop);
//        Glide.with(holder.itemView.context)
//            .`as`(PictureDrawable::class.java)
////            .listener(SvgSoftwareLayerSetter())
//            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
////            .load(uri).into(imageView)
//
//            .load("https://outgrow.blob.core.windows.net/outgrowstorage/Prod/CropLogo/Basil.svg")
//            .into(holder.binding.ivMyCrop)


        holder.itemView.setOnClickListener {
            detailsCLickLIstener.clickOnDetails(details)
            holder.binding.clSelectedItem.setBackgroundResource(R.drawable.bg_details)
            holder.binding.tick.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return details.size
    }

    fun upDateList(list: ArrayList<Data>) {
        details.clear()
        details.addAll(list)
        notifyDataSetChanged()

    }


}


class DetailViewHolder(val binding: ItemSelectCropBinding) : RecyclerView.ViewHolder(binding.root) {


}

//fun loadImageToImageView(imageView: ImageView, imageUrl: String) {
//    val requestOptions: RequestOptions = RequestOptions()
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .override(imageView.width, imageView.height)
//        .placeholder(com.example.featurecrophealth.R.drawable.disease)
//        .error(com.example.featurecrophealth.R.drawable.disease)
//    if (StringUtils.isNotEmpty(imageUrl)) Glide.with(imageView.context)
//        .load(imageUrl)
//        .thumbnail(0.15f)
//        .apply(requestOptions)
//        .into(imageView) else imageView.setImageResource(com.example.featurecrophealth.R.drawable.disease)
//}

object StringUtils {
    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.toString().trim { it <= ' ' }.isEmpty()
    }

    fun isNotEmpty(str: CharSequence?): Boolean {
        return str != null && !str.toString().trim { it <= ' ' }.isEmpty()
    }
}
