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
import com.waycool.data.Network.NetworkModels.DiseaseCurrentData
import com.waycool.data.Network.NetworkModels.HistoricData

class DiseaseAdapter:ListAdapter<DiseaseCurrentData,DiseaseAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder(private val binding: ItemDiseaseBinding): RecyclerView.ViewHolder(binding.root) {
       // val disName =binding.disName
        val slider2 = binding.slider
        val risk = binding.textView27
        val image = binding.disImg
        val name = binding.disName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val properties = getItem(position)
       // holder.disName.text = properties.
        holder.slider2.value = properties.probability!!.toFloat()
        /** Checking the risk factor for plant */
        holder.risk.text = properties.probabilityDesc
        if(properties.probabilityDesc=="Low Risk") {

            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_green)
        }
        else if (properties.probabilityDesc=="Nil"){
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_gray)
        }
        else if (properties.probabilityDesc=="Medium Risk"){
            holder.risk.text = "Medium Risk"
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_yellow)
        }
        else {
            holder.slider2.setCustomThumbDrawable(R.drawable.ic_holo_red)
        }
        Glide.with(holder.itemView.context).load(properties.disease?.diseaseImg).into(holder.image)
        /** showing large image of when user click on image*/
        holder.image.setOnClickListener {
            val dialog = Dialog(holder.itemView.context)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.item_large_image)
            // val body = dialog.findViewById(R.id.body) as TextView
            val close = dialog.findViewById(R.id.closeImage) as ImageView
            val image = dialog.findViewById(R.id.large_image) as ImageView
            Glide.with(holder.itemView.context).load(properties.disease?.diseaseImg).into(image)
            close.setOnClickListener { dialog.dismiss() }
            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            dialog.show()

    }
        if(properties.disease?.diseaseNameTag==null)
        holder.name.text = properties.disease?.diseaseName
//            if (properties.disease?.diseaseNameTranslated.equals("--", ignoreCase = true))
//            properties.disease?.diseaseName
        else
            holder.name.text = "${properties.disease?.diseaseName}(${properties.disease?.diseaseNameTag})"
           // properties.disease?.diseaseNameTranslated

    }

    companion object DiffCallback : DiffUtil.ItemCallback<DiseaseCurrentData>() {

    override fun areItemsTheSame(
        oldItem: DiseaseCurrentData,
        newItem: DiseaseCurrentData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: DiseaseCurrentData,
        newItem: DiseaseCurrentData
    ): Boolean {
        return oldItem.diseaseId == newItem.diseaseId
    }
}

    override fun getItemCount(): Int {
        if(currentList.size>=7)
            return 7
        else
            return currentList.size

    }
}