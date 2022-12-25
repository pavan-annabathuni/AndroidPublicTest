package com.example.profile.adapter

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.profile.R
import com.example.profile.databinding.ItemSupportBinding
import com.waycool.data.Network.NetworkModels.Disease
import com.waycool.data.Network.NetworkModels.GetFarmSupportData
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.domainModels.MandiHistoryDataDomain
import com.waycool.data.translations.TranslationsManager

class AddUseAdapter(val onClickListener: OnClickListener):ListAdapter<GetFarmSupportData,AddUseAdapter.ViewHolder>(DiffCallback){
    class ViewHolder(private val binding:ItemSupportBinding):
        RecyclerView.ViewHolder(binding.root) {
     val delete = binding.delete
        val name = binding.name
        val number = binding.tvNumber
        val firstName = binding.textView10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val properties = getItem(position)
        holder.name.text = properties.name
        holder.number.text = properties.contact
        val text = holder.name.text.get(0)
        holder.firstName.text = text.toString()

   holder.delete.setOnClickListener(){
       onClickListener.clickListener(properties)
//       val dialog = Dialog(context)
//
//       dialog.setCancelable(false)
//       dialog.setContentView(R.layout.dailog_delete)
//      // val body = dialog.findViewById(R.id.body) as TextView
//       val yesBtn = dialog.findViewById(R.id.cancel) as Button
//       val noBtn = dialog.findViewById(R.id.delete) as Button
//       yesBtn.setOnClickListener {
//           dialog.dismiss()
//       }
//       noBtn.setOnClickListener { dialog.dismiss() }
//       dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//       dialog.show()

   }
        TranslationsManager().loadString("str_delete",holder.delete)

    }
    companion object DiffCallback : DiffUtil.ItemCallback<GetFarmSupportData>() {

        override fun areItemsTheSame(
            oldItem: GetFarmSupportData,
            newItem: GetFarmSupportData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: GetFarmSupportData,
            newItem: GetFarmSupportData
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnClickListener(val clickListener: (data: GetFarmSupportData) -> Unit) {
        fun onClick(data: GetFarmSupportData) = clickListener(data)
    }
}