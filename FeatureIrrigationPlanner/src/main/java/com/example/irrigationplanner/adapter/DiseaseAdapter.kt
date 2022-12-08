package com.example.irrigationplanner.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemDiseaseBinding
import com.example.irrigationplanner.databinding.ItemDiseaseHistoryBinding
import com.waycool.data.Network.NetworkModels.Disease
import com.waycool.data.Network.NetworkModels.HistoricData

class DiseaseAdapter:ListAdapter<Disease,DiseaseAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDiseaseBinding): RecyclerView.ViewHolder(binding.root) {
       // val disName =binding.disName
        val slider2 = binding.slider
        val risk = binding.textView27
        val image = binding.disImg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return DiseaseAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
       // holder.disName.text = properties.
        holder.slider2.value = properties.probability!!.toFloat()
        if(properties.probability!!<50.00) {
            holder.risk.text = "Low Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_green)
        }
        else if (properties.probability!!>50.00&&properties.probability!!<75.00){
            holder.risk.text = "Medium Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_yellow)
        }
        else {
            holder.risk.text = "High Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_red)
        }
        holder.image.setOnClickListener() {
            val dialog = Dialog(holder.itemView.context)

            dialog.setCancelable(true)
            dialog.setContentView(R.layout.item_large_image)
            // val body = dialog.findViewById(R.id.body) as TextView
            val close = dialog.findViewById(R.id.closeImage) as ImageView
            val image = dialog.findViewById(R.id.large_image) as ImageView
            close.setOnClickListener { dialog.dismiss() }
            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            dialog.show()

    }
    }

//    override fun getItemCount(): Int {
//        val size=data
//        return if(size>=5) 5
//        else size
//    }
    companion object DiffCallback : DiffUtil.ItemCallback<Disease>() {

    override fun areItemsTheSame(
        oldItem: Disease,
        newItem: Disease
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Disease,
        newItem: Disease
    ): Boolean {
        return oldItem.id == newItem.id
    }
}

//    override fun getItemCount(): Int {
//        if(currentList.size>=7)
//            return 7
//        else
//            return currentList.size
//
//    }
}