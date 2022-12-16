package com.example.irrigationplanner.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemDiseaseHistoryBinding
import com.example.irrigationplanner.databinding.ItemHistoryBinding
import com.waycool.data.Network.NetworkModels.Disease

class DiseaseHistoryAdapter: ListAdapter<Disease,DiseaseHistoryAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDiseaseHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
       val date = binding.tvDate
        val slider2 = binding.slider2
        val risk = binding.tvRisk
        val image = binding.disImg2
        val name = binding.tvDisName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemDiseaseHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseHistoryAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
        holder.date.text = properties.createdAt
        holder.slider2.value = properties.probability!!.toFloat()
        if(properties.probability!!>=15.99&&properties.probability!!<=43.99) {
            holder.risk.text = "Low Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_green)
        }
        else if (properties.probability!!<=15.00){
            holder.risk.text = "NIll"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_gray)
        }
        else if (properties.probability!!>=44&&properties.probability!!<=72.99){
            holder.risk.text = "Medium Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_yellow)
        }
        else {
            holder.risk.text = "High Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_red)
        }
        Glide.with(holder.itemView.context).load(properties.disease.diseaseImg).into(holder.image)
        holder.image.setOnClickListener() {
            val dialog = Dialog(holder.itemView.context)

            dialog.setCancelable(true)
            dialog.setContentView(R.layout.item_large_image)
            // val body = dialog.findViewById(R.id.body) as TextView
            val close = dialog.findViewById(R.id.closeImage) as ImageView
            val image = dialog.findViewById(R.id.large_image) as ImageView
            Glide.with(holder.itemView.context).load(properties.disease.diseaseImg).into(image)
            close.setOnClickListener { dialog.dismiss() }
            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            dialog.show()

        }
        holder.name.text = properties.disease.diseaseName
    }


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

}