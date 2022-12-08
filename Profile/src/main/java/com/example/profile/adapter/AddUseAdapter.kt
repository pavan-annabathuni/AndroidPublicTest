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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.profile.R
import com.example.profile.databinding.ItemSupportBinding
import com.waycool.data.repository.domainModels.MandiDomainRecord

class AddUseAdapter(val onClickListener: OnClickListener): RecyclerView.Adapter<AddUseAdapter.ViewHolder>() {
    class ViewHolder(private val binding:ItemSupportBinding):
        RecyclerView.ViewHolder(binding.root) {
     val delete = binding.delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val properties = getItemId(position)
   holder.delete.setOnClickListener(){
       onClickListener.clickListener()
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
    }

    override fun getItemCount(): Int {
        return 4
    }
    class OnClickListener(val clickListener: () -> Unit) {
        fun onClick() = clickListener()
    }
}