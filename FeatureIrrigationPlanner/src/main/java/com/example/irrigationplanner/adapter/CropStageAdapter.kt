package com.example.irrigationplanner.adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemCropStageBinding
import com.example.irrigationplanner.databinding.ItemDiseaseBinding
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.repository.domainModels.CropMasterDomain
import java.text.SimpleDateFormat
import java.util.*

class CropStageAdapter(val onClickListener:OnClickListener):ListAdapter<CropStageData,CropStageAdapter.MyViewHolder>(DiffCallback) {

    var onDateSelected: ((String?) -> Unit)? = null

    class MyViewHolder(private val binding: ItemCropStageBinding): RecyclerView.ViewHolder(binding.root) {
        val name = binding.textView7
        val image = binding.imageView12
        val date = binding.cal1
        val view = binding.view9
        val holoImg = binding.imgHolo1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCropStageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CropStageAdapter.MyViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
     val properties = getItem(position)
      holder.name.text = properties.stageName
        Glide.with(holder.itemView.context).load(properties.stageIcon).into(holder.image)


        holder.date.setOnClickListener(){
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                holder.itemView.context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                  holder.date.text = ("$year-$monthOfYear-$dayOfMonth")
                  // date1 = binding.cal1.text.toString()
                    onDateSelected?.invoke("$year-$monthOfYear-$dayOfMonth")
                },
                year,
                month,
                day
            )
            // Log.d("CropStage", "getCropStage: ${date1}")

            dpd.show()
            onClickListener.clickListener(properties)
        }

        if(properties.date != null) {
            holder.date.text = properties.date
//            holder.view.backgroundTintList= ColorStateList(arrayOf(intArrayOf(R.color.DarkGreen)))
            holder.view.backgroundTintList= ContextCompat.getColorStateList(holder.view.context,R.color.DarkGreen)
        holder.holoImg.setImageResource(R.drawable.ic_holo_darkgreen)
        } else{ holder.date.text = "Select Date"
            holder.view.backgroundTintList= ContextCompat.getColorStateList(holder.view.context,R.color.LightGray)
            holder.holoImg.setImageResource(R.drawable.ic_holo_gray)
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<CropStageData>() {

        override fun areItemsTheSame(
            oldItem: CropStageData,
            newItem: CropStageData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CropStageData,
            newItem: CropStageData
        ): Boolean {
            return oldItem.cropVarietyId == newItem.cropVarietyId
        }
    }
    class OnClickListener(val clickListener: (data: CropStageData) -> Unit) {
        fun onClick(data: CropStageData) = clickListener(data)
    }
}