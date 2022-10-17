//package com.example.featurecrophealth.ui.history
//
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.featurecrophealth.R
//
//class CropVideoAdapter(
//    var context: Context,
//    cropVideoModelArrayList: ArrayList<CropInfoVideoList>
//) :
//    RecyclerView.Adapter<com.waycool.iwap.revampCropInfo.adapter.CropVideoAdapter.VideoVH_new>() {
//    var videosListModels: List<Video>? = null
//    private val cropVideoModelArrayList: ArrayList<CropInfoVideoList>
//    var TAG = javaClass.simpleName
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): com.waycool.iwap.revampCropInfo.adapter.CropVideoAdapter.VideoVH_new {
//        val view: View =
//            LayoutInflater.from(parent.context).inflate(R.layout.cropvideo_listview, parent, false)
//        return VideoVH_new(view)
//    }
//
//    override fun onBindViewHolder(holder: VideoVH_new, position: Int) {
//        val video: CropInfoVideoList = cropVideoModelArrayList[position]
//        holder.video_desc.setText(video.getVideoTitle())
//        holder.videodate.setText(AppUtil.changeDateFormat(video.getDate()))
//        AppUtil.showUrlImage(
//            context,
//            ApiClient.BASE_URL + video.getVideoThumbnail(),
//            holder.video_image
//        )
//    }
//
//    override fun getItemCount(): Int {
//        return cropVideoModelArrayList.size
//    }
//
//    inner class VideoVH_new(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var video_desc: TextView
//        var videodate: TextView
//        var video_image: ImageView
//        var video_image_play_btn: ImageView
//
//        init {
//            video_desc = itemView.findViewById(R.id.video_desc)
//            videodate = itemView.findViewById(R.id.video_date)
//            video_image = itemView.findViewById(R.id.video_image)
//            video_image_play_btn = itemView.findViewById(R.id.video_image_play_btn)
//            video_image_play_btn.setOnClickListener {
//                val videosListModel: CropInfoVideoList = cropVideoModelArrayList[adapterPosition]
//                val video_url: String = videosListModel.getVideoLink()
//                val intent = Intent(context, ExoPlayerActivity::class.java)
//                intent.putExtra("video_url", video_url)
//                context.startActivity(intent)
//            }
//            itemView.setOnClickListener {
//                val videosListModel: CropInfoVideoList = cropVideoModelArrayList[adapterPosition]
//                val video_url: String = videosListModel.getVideoLink()
//                val intent = Intent(context, ExoPlayerActivity::class.java)
//                intent.putExtra("video_url", video_url)
//                context.startActivity(intent)
//            }
//        }
//    }
//
//    init {
//        this.cropVideoModelArrayList = cropVideoModelArrayList
//    }
//}